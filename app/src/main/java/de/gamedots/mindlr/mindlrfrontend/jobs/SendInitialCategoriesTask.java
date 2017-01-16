package de.gamedots.mindlr.mindlrfrontend.jobs;

import android.content.Context;

import org.json.JSONObject;

import de.gamedots.mindlr.mindlrfrontend.util.Global;

/**
 * Created by Dirk on 15.01.17.
 */

public class SendInitialCategoriesTask extends APICallTask {

    public SendInitialCategoriesTask(Context context, JSONObject content, boolean authenticated) {
        super(context, content, true);
        _apiMethod = Global.BACKEND_METHOD_INITIAL_USER_CAT;
    }

    @Override
    public void onSuccess(JSONObject result) {
    }

    @Override
    public void onFailure(JSONObject result) {

    }
}
