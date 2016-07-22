package de.gamedots.mindlr.mindlrfrontend.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.helper.JSONParser;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;

import static de.gamedots.mindlr.mindlrfrontend.util.Global.METHOD_POST;
import static de.gamedots.mindlr.mindlrfrontend.util.Global.SERVER_URL;

/**
 * Created by Max on 24.04.16.
 *
 * JSON CONTRACT:
 * {
 *     "token": TOKEN,
 *     "auth_provider": AUTH_PROVIDER,
 *     "content":
 *          {
 *              // Content relevant to specific method
 *          }
 *     "metadata":
 *          {
 *              // Standard Metadata (SDK, Device, ...)
 *          }
 * }
 */
public abstract class BackendTask extends AsyncTask<Void, Void, JSONObject> {


    protected Context _context;
    protected String _apiMethod;
    protected String _authProvider;
    protected String _token;
    protected HashMap<String, String> _content;
    protected HashMap<String, String> _metadata;

    public BackendTask(Context context, HashMap<String, String> content, HashMap<String, String> metadata){
        _context = context;
        Log.d(LOG.CONNECTION, _context.toString());
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.LoginStatePreference), Context.MODE_PRIVATE);
        _authProvider = sharedPreferences.getString("authProvider", "google");
        _token = sharedPreferences.getString("token", "TOKEMON");
        _content = content;
        _metadata = metadata;
    }

    @Override
    protected void onPreExecute() {
        if(_authProvider == null || _token == null){
            //TODO: Try to connect to GoogleAPIClient and get new token, if successfull override token
            //If not successfull, log user out
        }
        Log.e("ALOAH SNACKBAR", _apiMethod);
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        //generate new HashMap with default values such as SDK etc.
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("token", _token);
        parameter.put("auth_provider", _authProvider);
        parameter.put("content",  new JSONObject(_content).toString());
        parameter.put("metadata", new JSONObject(_metadata).toString());

        Log.d(LOG.JSON, "About to create JSONParser");
        JSONParser parser = new JSONParser();
        Log.d(LOG.CONNECTION, "About to make HTTPRequest");
        return parser.makeHttpRequest(SERVER_URL + _apiMethod, METHOD_POST, parameter);
    }

    /**
     *
     * Defines the default behaviour for all onPostExecute-methods of AsyncTasks who communicate with the server
     * and get a JSON back
     *
     * The standard for returned JSONs is:
     * 1. ALWAYS a "SUCCESS"-key, either with a value "true" or "false"
     * 2. If SUCCESS is false, ALWAYS a "ERROR"-key with an error message.
     * 3. If SUCCESS is true, there MAY BE more data in the JSON.
     *
     * The specific actions if SUCCESS was true or false should be implemented by overriding onSuccess() and onFailure()
     * On failure, the error message will automatically be logged with a "LOG.JSON"-Tag
     *
     * Uses abstract class instead of interface, because no 1.8 support in Android (yet)
     * Created by Max Wiechmann on 09.11.2015.
     */
    protected void onPostExecute(JSONObject result) {
        if (result != null) {
            try {
                boolean success = result.getBoolean("SUCCESS");
                result.remove("SUCCESS");
                if (success) {
                    onSuccess(result);
                } else {
                    try {
                        int status = result.getInt("STATUS");
                        if (status == 401 /*UNAUTHORIZED*/){
                            //TODO: Try to get new access token, if not successfull, log out
                            //If successfull, call backend task again
                        }
                    } catch (JSONException e){
                        Log.e(LOG.JSON, Log.getStackTraceString(e));
                    }
                    try {
                        String text = result.getString("ERROR");
                        Log.d(LOG.JSON, text);
                    } catch (JSONException e){
                        Log.e(LOG.JSON, Log.getStackTraceString(e));
                    }
                    onFailure(result);
                }

            } catch (JSONException e) {
                Log.d(LOG.JSON, "Error parsing data into objects");
                Log.e(LOG.JSON, Log.getStackTraceString(e));
            }
        } else {
            Log.d(LOG.JSON, "JSONObject was null");
        }
    }

    public abstract void onSuccess(JSONObject result);

    public abstract void onFailure(JSONObject result);
}
