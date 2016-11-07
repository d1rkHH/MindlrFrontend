package de.gamedots.mindlr.mindlrfrontend.jobs;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.util.Global;

import static de.gamedots.mindlr.mindlrfrontend.util.DebugUtil.toast;

public class SignInTask extends APICallTask {

    public interface OnSignInProcessSuccessListener {
        void onSignInProcessSuccess();
    }

    private OnSignInProcessSuccessListener _callback;

    public SignInTask(Context context, JSONObject content, String provider, OnSignInProcessSuccessListener
            callback) {
        super(context, content);
        _apiMethod = Global.BACKEND_METHOD_SIGN_IN;
        _authProvider = provider;
        _callback = callback;
    }

    @Override
    public void onSuccess(JSONObject result) {
        Log.d(LOG.VERIFIED, "successful verified the user on backend");
        toast(_context, "Verified User");
        if (_callback != null) {
            _callback.onSignInProcessSuccess();
        }
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

