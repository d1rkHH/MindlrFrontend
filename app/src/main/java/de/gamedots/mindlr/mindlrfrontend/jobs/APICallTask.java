package de.gamedots.mindlr.mindlrfrontend.jobs;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import de.gamedots.mindlr.mindlrfrontend.AuthHandlerHelper;
import de.gamedots.mindlr.mindlrfrontend.MindlrApplication;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.util.ServerComUtil;

import static de.gamedots.mindlr.mindlrfrontend.util.Global.SERVER_URL;

/**
 * Created by Max Wiechmann on 24.04.16.
 * <p/>
 * JSON CONTRACT:
 * {
 * "token": TOKEN,
 * "auth_provider": AUTH_PROVIDER,
 * "content":
 * {
 * // Content relevant to specific method
 * }
 * "metadata":
 * {
 * // Standard Metadata (SDK, Device, ...)
 * }
 * }
 */
public abstract class APICallTask extends AsyncTask<Void, Void, JSONObject> {

    protected Context _context;
    protected String _apiMethod;
    protected String _authProvider;
    protected String _token;
    protected JSONObject _content;
    protected JSONObject _metadata;

    private AuthHandlerHelper _authHandlerHelper;
    private boolean signInFailed = false;

    public APICallTask(Context context, JSONObject content) {
        _context = context;
        _content = content;
        _metadata = ServerComUtil.getMetaDataJSON();
        _authHandlerHelper = new AuthHandlerHelper(_context);
        //_token = "DEFAULT_TOKEN";
    }


    /**
     * By default, the metadata is a JSONObject created by ServerComUtil.getMetaDataJSON
     * You can override it manually with this method
     *
     * @param metaData
     * @return this Task
     */
    public APICallTask setMetaData(JSONObject metaData) {
        _metadata = metaData;
        return this;
    }

    @Override
    protected void onPreExecute() {
        if(_authProvider == null){
            _authProvider = MindlrApplication.User.getAuthProvider();
        }
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        Log.v(LOG.AUTH, "inside APICALL: provider " + _authProvider);

        // connect GoogleApiClient and retrieve idToken
        GoogleApiClient gac = _authHandlerHelper.getGoogleApiClient();
        try {
            ConnectionResult result = gac.blockingConnect();
            if (result.isSuccess()) {
                GoogleSignInResult gsr = Auth.GoogleSignInApi.silentSignIn(gac).await();
                if (gsr != null && gsr.isSuccess()) {
                    _token = gsr.getSignInAccount().getIdToken();
                    Log.d(LOG.AUTH, "doInBackground: GoogleApiClient connected: " + gac.isConnected());
                    Log.d(LOG.AUTH, "doInBackground: idToken retrieved: " + _token);
                } else {
                    Log.d(LOG.AUTH, "doInBackground: FAILURE did not get idToken");
                    signInFailed = true;
                    return null; // break execution
                }
            }
        } catch (Exception ex) {
            Log.d(LOG.AUTH, "doInBackground: ERROR idToken retrieve");
            signInFailed = true;
            return null; // break execution
        } finally {
            gac.disconnect();
        }
        //_authProvider = "google";
        JSONObject requestJSON = new JSONObject();
        try {
            requestJSON = new JSONObject();
            requestJSON.put("content", _content);
            requestJSON.put("metadata", _metadata);
            requestJSON.put("token", _token);
            requestJSON.put("auth_provider", _authProvider);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ServerComUtil.httpPostRequest(SERVER_URL + _apiMethod + "/", requestJSON);
    }

    /**
     * Defines the default behaviour for all onPostExecute-methods of AsyncTasks who communicate with the server
     * and get a JSON back
     * <p/>
     * The standard for returned JSONs is:
     * 1. ALWAYS a "SUCCESS"-key, either with a value "true" or "false"
     * 2. If SUCCESS is false, ALWAYS a "ERROR"-key with an error message.
     * 3. If SUCCESS is true, there MAY BE more data in the JSON.
     * <p/>
     * The specific actions if SUCCESS was true or false should be implemented by overriding onSuccess() and onFailure()
     * On failure, the error message will automatically be logged with a "LOG.JSON"-Tag
     * <p/>
     * Uses abstract class instead of interface, because no 1.8 support in Android (yet)
     * Created by Max Wiechmann on 09.11.2015.
     */
    protected void onPostExecute(JSONObject result) {
        if (signInFailed) {
            //_context.startActivity(new Intent(_context, LoginActivity.class));
            return;
        }

        if (result != null) {
            try {
                boolean success = result.getBoolean("SUCCESS");
                result.remove("SUCCESS");
                if (success) {
                    onSuccess(result);
                } else {
                    try {
                        int status = result.getInt("STATUS");
                        if (status == 401 /*UNAUTHORIZED*/) {
                            //TODO: Try to get new access token, if not successfull, log out
                            //If successfull, call backend task again
                        }
                    } catch (JSONException e) {
                        Log.e(LOG.JSON, Log.getStackTraceString(e));
                    }
                    try {
                        String text = result.getString("ERROR");
                        Log.d(LOG.JSON, text);
                    } catch (JSONException e) {
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
