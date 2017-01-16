package de.gamedots.mindlr.mindlrfrontend.jobs;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import de.gamedots.mindlr.mindlrfrontend.controller.PostLoader;
import de.gamedots.mindlr.mindlrfrontend.data.DatabaseIntentService;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.PostLoadedEvent;
import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;
import de.gamedots.mindlr.mindlrfrontend.util.Global;

/**
 * Created by Max Wiechmann on 23.07.16.
 */
public class LoadPostsTask extends APICallTask {

    private WeakReference<Context> _context;

    public LoadPostsTask(Context context, JSONObject content) {
        super(context, content, true);
        _context = new WeakReference<>(context);
        _apiMethod = Global.BACKEND_METHOD_LOAD_POSTS;
    }

    @Override
    public void onSuccess(JSONObject result) {
        try {
            if (result.has("items")) {
                JSONArray posts = result.getJSONArray("items");
                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.getJSONObject(i);
                    if (post.has("id") && post.has("content_text") && post.has("content_url")) {
                        ViewPost viewPost = new ViewPost(
                                post.getInt("id"),
                                post.getString("content_text"),
                                post.getString("content_url")
                        );
                        PostLoader.getInstance().addPost(viewPost);
                    } else {
                        Log.e(LOG.POSTS, "Post JSON invalid");
                    }
                }
                EventBus.getDefault().post(new PostLoadedEvent(true));

                // insert posts into database

                if (_context.get() != null) {
                    Intent intent = new Intent(_context.get(), DatabaseIntentService.class);
                    intent.setAction(DatabaseIntentService.INSERT_POST_ACTION);
                    _context.get().startService(intent);
                } else {
                    Log.v(LOG.AUTH, "load task context was null");
                }

            } else {
                Log.e(LOG.POSTS, "Posts JSON invalid");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(JSONObject result) {
        try {
            String text = result.getString("ERROR");
            Log.v(LOG.JSON, text);
        } catch (JSONException e) {
            Log.e(LOG.JSON, Log.getStackTraceString(e));
        }
    }
}