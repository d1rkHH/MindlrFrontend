package de.gamedots.mindlr.mindlrfrontend.jobs;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.gamedots.mindlr.mindlrfrontend.controller.PostLoader;
import de.gamedots.mindlr.mindlrfrontend.data.DatabaseIntentService;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;
import de.gamedots.mindlr.mindlrfrontend.util.Global;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.PostViewFragment;

import static de.gamedots.mindlr.mindlrfrontend.util.DebugUtil.toast;

/**
 * Created by Max Wiechmann on 23.07.16.
 */
public class LoadPostsTask extends APICallTask {

    private PostViewFragment _fragment;
    private Context _context;

    public LoadPostsTask(Context context, JSONObject content) {
        super(context, content);
        _context = context;
        _apiMethod = Global.BACKEND_METHOD_LOAD_POSTS;
    }

    public LoadPostsTask setFragment(PostViewFragment fragment) {
        _fragment = fragment;
        return this;
    }

    @Override
    public void onSuccess(JSONObject result) {
        try {
            if (result.has("posts")) {
                JSONArray posts = result.getJSONArray("posts");
                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.getJSONObject(i);
                    if (post.has("id") && post.has("content_text")) {
                        ViewPost viewPost = new ViewPost(post.getInt("id"), post.getString("content_text"));
                        PostLoader.getInstance().addPost(viewPost);
                        if (_fragment != null && _fragment.getPostView() != null && PostLoader.getInstance
                                ().getPostList().size() == 1) {
                            _fragment.getPostView().setText(viewPost.getContentText());
                        }

                    } else {
                        Log.e(LOG.POSTS, "Post JSON invalid");
                    }
                }

                // insert posts into database
                Intent intent = new Intent(_context, DatabaseIntentService.class);
                intent.setAction(DatabaseIntentService.INSERT_POST_ACTION);
                _context.startService(intent);

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
            toast(_context, text);
        } catch (JSONException e) {
            Log.e(LOG.JSON, Log.getStackTraceString(e));
        }
    }
}