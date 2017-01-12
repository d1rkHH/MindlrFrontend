package de.gamedots.mindlr.mindlrfrontend.jobs;

import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;

import de.gamedots.mindlr.mindlrfrontend.data.DatabaseIntentService;
import de.gamedots.mindlr.mindlrfrontend.util.Global;

import static de.gamedots.mindlr.mindlrfrontend.data.DatabaseIntentService.*;
import static de.gamedots.mindlr.mindlrfrontend.data.DatabaseIntentService.JSON_EXTRA;

/**
 * Created by Dirk on 12.01.17.
 */

public class SyncUserPostTask extends APICallTask {


    public SyncUserPostTask(Context context, JSONObject content) {
        super(context, content, true);
        _apiMethod = Global.BACKEND_METHOD_SYNC_USERPOSTS;
    }

    @Override
    public void onSuccess(JSONObject result) {
        if (result != null) {
            // insert posts into database
            Intent intent = new Intent(_context, DatabaseIntentService.class);
            intent.putExtra(JSON_EXTRA, result.toString());
            intent.setAction(UPDATE_USERPOSTS_VOTES_ACTION);
            _context.startService(intent);
        }
    }

    @Override
    public void onFailure(JSONObject result) {
    }
}
