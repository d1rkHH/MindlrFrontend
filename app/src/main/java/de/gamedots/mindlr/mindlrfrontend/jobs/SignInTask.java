package de.gamedots.mindlr.mindlrfrontend.jobs;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import de.gamedots.mindlr.mindlrfrontend.auth.IdpResponse;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.util.Global;

import static de.gamedots.mindlr.mindlrfrontend.util.DebugUtil.toast;

public class SignInTask extends APICallTask {

    public interface AuthRequestCallback {
        void onSendingSuccess(IdpResponse idpResponse);
    }

    private AuthRequestCallback _callback;

    public SignInTask(Context context, JSONObject content, String provider, AuthRequestCallback
            callback, IdpResponse idpResponse) {
        super(context, content, true);
        _apiMethod = Global.BACKEND_METHOD_SIGN_IN;
        _authProvider = provider;
        _callback = callback;
        _idpResponse = idpResponse;
    }

    @Override
    public void onSuccess(JSONObject result) {
        Log.d(LOG.AUTH, "successful verified the user on backend");
        toast(_context, "Verified User");
        if (_callback != null) {
            _callback.onSendingSuccess(_idpResponse);
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

