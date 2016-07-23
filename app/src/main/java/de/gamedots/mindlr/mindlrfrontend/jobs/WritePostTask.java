package de.gamedots.mindlr.mindlrfrontend.jobs;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import de.gamedots.mindlr.mindlrfrontend.util.Global;
import de.gamedots.mindlr.mindlrfrontend.view.activity.MainActivity;

/**
 * Created by Max Wiechmannn on 23.07.16.
 */
public class WritePostTask extends APICallTask {

    public WritePostTask(Context context, JSONObject content){
        super(context, content);
        _apiMethod = Global.BACKEND_METHOD_WRITE_POST;
    }

    @Override
    public void onSuccess(JSONObject result) {
        Log.d(de.gamedots.mindlr.mindlrfrontend.logging.LOG.POSTS, "Successfull posted.");
        Toast.makeText(_context, "Erfolgreich gepostet", Toast.LENGTH_SHORT).show();
        _context.startActivity(new Intent(_context, MainActivity.class));
    }
    @Override
    public void onFailure(JSONObject result) {
        try {
            String text = result.getString("ERROR");
            Toast.makeText(_context, text, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Log.e(de.gamedots.mindlr.mindlrfrontend.logging.LOG.JSON, Log.getStackTraceString(e));
        }
    }
}