package de.gamedots.mindlr.mindlrfrontend.jobs;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import de.gamedots.mindlr.mindlrfrontend.controller.PostLoader;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.util.Global;

/**
 * Created by Max Wiechmann on 23.07.16.
 */
public class StoreVotesTask extends APICallTask {

    PostLoader _loader;

    public StoreVotesTask(Context context, JSONObject content, PostLoader loader){
        super(context, content);
        _apiMethod = Global.BACKEND_METHOD_SEND_VOTES;
        _loader = loader;
    }

    @Override
    public void onSuccess(JSONObject result) {
        Log.d(LOG.POSTS, "successful posted.");
        _loader.removeSendPosts(new ArrayList<Long>());
        _loader.setSending(false);
    }

    @Override
    public void onFailure(JSONObject result) {
        try {
            JSONArray failedPostIDs = result.getJSONArray("FAILED");
            List<Long> postIDs = new ArrayList<>();
            for (int i = 0; i < failedPostIDs.length(); i++) {
                postIDs.add(failedPostIDs.getLong(i));
            }
            _loader.removeSendPosts(postIDs);
        } catch (JSONException e) {
            Log.e(LOG.JSON, Log.getStackTraceString(e));
        } finally {
            _loader.setSending(false);
        }
    }
}