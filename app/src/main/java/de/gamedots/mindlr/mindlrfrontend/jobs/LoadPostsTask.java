package de.gamedots.mindlr.mindlrfrontend.jobs;

import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.List;
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
    private List<ViewPost> _postList;

    public LoadPostsTask(Context context, JSONObject content, List<ViewPost> postList){
        super(context, content);
        _apiMethod = Global.BACKEND_METHOD_LOAD_POSTS;
        _postList = postList;
    }

    public LoadPostsTask setFragment(PostViewFragment fragment){
        _fragment = fragment;
        return this;
    }

    @Override
    public void onSuccess(JSONObject result) {
        Iterator<?> keys = result.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            try {
                String text = result.getString(key);
                Log.d(LOG.JSON, "Key: " + key + " - Text: " + text);
                try {
                    int id = Integer.parseInt(key);
                    Log.d(LOG.POSTS, "Add post to postList");
                    ViewPost post = new ViewPost(id, text);
                    _postList.add(post);

                    if (_fragment != null && _postList.size() == 1) {
                        _fragment.getPostView().setText(post.getContentText());
                    }
                } catch (NumberFormatException e) {
                    Log.d(LOG.JSON, "JSON key is NaN");
                }
            } catch (JSONException e) {
                Log.e(LOG.JSON, Log.getStackTraceString(e));
            }
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