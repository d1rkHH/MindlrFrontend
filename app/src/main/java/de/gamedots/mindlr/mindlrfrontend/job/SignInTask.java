package de.gamedots.mindlr.mindlrfrontend.job;

import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.util.BackendTask;
import de.gamedots.mindlr.mindlrfrontend.util.Global;
import static de.gamedots.mindlr.mindlrfrontend.util.DebugUtil.toast;

public class SignInTask extends BackendTask {

    public SignInTask(Context context, HashMap<String, String> content, HashMap<String, String> metadata){
        super(context, content, metadata);
        _apiMethod = Global.BACKEND_METHOD_SIGN_IN;
    }

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
}

