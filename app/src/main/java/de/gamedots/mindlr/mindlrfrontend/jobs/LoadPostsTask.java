package de.gamedots.mindlr.mindlrfrontend.jobs;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
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
        try {
            JSONArray posts = result.getJSONArray("posts");
            for(int i = 0; i < posts.length(); i++){
                JSONObject post = posts.getJSONObject(i);
                ViewPost viewPost = new ViewPost(post.getInt("id"), post.getString("content_text"));
                _postList.add(viewPost);
                if (_fragment != null && _postList.size() == 1) {
                    _fragment.getPostView().setText(viewPost.getContentText());
                }
            }
        } catch (JSONException e){
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