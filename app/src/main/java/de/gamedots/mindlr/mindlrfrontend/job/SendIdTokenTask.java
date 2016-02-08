package de.gamedots.mindlr.mindlrfrontend.job;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.gamedots.mindlr.mindlrfrontend.helper.JSONParser;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.util.Global;
import de.gamedots.mindlr.mindlrfrontend.util.PostExecuteTemplate;
import de.gamedots.mindlr.mindlrfrontend.util.ServerComUtil;

import static de.gamedots.mindlr.mindlrfrontend.util.DebugUtil.toast;
import static de.gamedots.mindlr.mindlrfrontend.util.Global.BACKEND_METHOD_KEY;
import static de.gamedots.mindlr.mindlrfrontend.util.Global.METHOD_POST;
import static de.gamedots.mindlr.mindlrfrontend.util.Global.METHOD_VERIFY;
import static de.gamedots.mindlr.mindlrfrontend.util.Global.SERVER_URL;

public class SendIdTokenTask extends AsyncTask<String, Void, JSONObject> {

    private Context _context;

    public SendIdTokenTask(Context context) {
        _context = context;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        HashMap<String, String> parameter = ServerComUtil.newDefaultParameterHashMap();
        //METHOD SPECIFIC PARAMETERS
        parameter.put(BACKEND_METHOD_KEY, METHOD_VERIFY);
        parameter.put("AUTH_PROVIDER", Global.AuthProvider.GOOGLE.name());
        parameter.put("ID_TOKEN", params[0] /* idToken */);
        parameter.put("EMAIL", params[1] /* email */);

        Log.d(LOG.JSON, "About to create JSONParser");
        JSONParser parser = new JSONParser();
        Log.d(LOG.CONNECTION, "About to make HTTPRequest");
        return parser.makeHttpRequest(SERVER_URL, METHOD_POST, parameter);
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        new PostExecuteTemplate() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.d(LOG.VERIFIED, "successful verified the user on backend");
                toast(_context, "Verified User");
            }

            @Override
            public void onFailure(JSONObject result) {
                try {
                    String errorMsg = result.getString("ERROR");
                    toast(_context, errorMsg);
                } catch (JSONException e) {
                    Log.e(LOG.JSON, Log.getStackTraceString(e));
                }
            }
        }.onPostExec(result);
    }
}

