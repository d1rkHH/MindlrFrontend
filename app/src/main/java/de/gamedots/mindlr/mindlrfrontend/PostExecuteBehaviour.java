package de.gamedots.mindlr.mindlrfrontend;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;


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
public abstract class PostExecuteBehaviour {

    public final void onPostExec(JSONObject result) {
        if (result != null) {
            try {
                boolean success = result.getBoolean("SUCCESS");
                result.remove("SUCCESS");
                if (success) {
                    onSuccess(result);
                } else {
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

    abstract void onSuccess(JSONObject result);

    abstract void onFailure(JSONObject result);

}