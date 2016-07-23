package de.gamedots.mindlr.mindlrfrontend.jobs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.util.ServerComUtil;

import static de.gamedots.mindlr.mindlrfrontend.util.Global.SERVER_URL;

/**
 * Created by Max Wiechmann on 24.04.16.
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
public abstract class APICallTask extends AsyncTask<Void, Void, JSONObject> {

    protected Context _context;
    protected String _apiMethod;
    protected String _authProvider;
    protected String _token;
    protected JSONObject _content;
    protected JSONObject _metadata;

    public APICallTask(Context context, JSONObject content){
        _context = context;
        Log.d(LOG.CONNECTION, _context.toString());
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.LoginStatePreference), Context.MODE_PRIVATE);
        _authProvider = sharedPreferences.getString("authProvider", "DEFAULT_AUTH_PROVIDER");
        _token = sharedPreferences.getString("token", "DEFAULT_TOKEN");
        _content = content;
        _metadata = ServerComUtil.getMetaDataJSON();
    }

    /**
     * By default, the metadata is a JSONObject created by ServerComUtil.getMetaDataJSON
     * You can override it manually with this method
     * @param metaData
     * @return this Task
     */
    public APICallTask setMetaData(JSONObject metaData){
        _metadata = metaData;
        return this;
    }

    @Override
    protected void onPreExecute() {
        if(_authProvider == null || _token == null){
            //TODO: Try to connect to GoogleAPIClient and get new token, if successfull override token
            //If not successfull, log user out
        }
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject requestJSON = new JSONObject();
        try {
            requestJSON = new JSONObject();
            requestJSON.put("content", _content);
            requestJSON.put("metadata", _metadata);
            requestJSON.put("token", _token);
            requestJSON.put("auth_provider", _authProvider);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return ServerComUtil.httpPostRequest(SERVER_URL + _apiMethod + "/", requestJSON);
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