package de.gamedots.mindlr.mindlrfrontend.jobs;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.gamedots.mindlr.mindlrfrontend.controller.PostLoader;
import de.gamedots.mindlr.mindlrfrontend.controller.StoreVotesHandler;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;
import de.gamedots.mindlr.mindlrfrontend.util.Global;

/**
 * Created by Max Wiechmann on 23.07.16.
 */
public class StoreVotesTask extends APICallTask {

    public StoreVotesTask(Context context, JSONObject content){
        super(context, content);
        _apiMethod = Global.BACKEND_METHOD_SEND_VOTES;
    }

    @Override
    public void onSuccess(JSONObject result) {
        Log.d(LOG.POSTS, "Success storing votes");
        StoreVotesHandler.getInstance().sendingSuccess();
    }

    @Override
    public void onFailure(JSONObject result) {
        Log.e(LOG.POSTS, "ERROR storing votes");
        StoreVotesHandler.getInstance().sendingFailed();
    }
}