package de.gamedots.mindlr.mindlrfrontend.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.util.DebugUtil;

/**
 * Created by Dirk on 19.12.16.
 */

public class GoogleProvider implements IdentityProvider, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_GET_TOKEN = 9000;
    private static final String TAG = "GOOGLE_PROVIDER";
    private static final String ERROR_KEY = "error";
    private GoogleApiClient _googleApiClient;
    private Activity _activity;
    private IdpCallback _idpCallback;


    public GoogleProvider(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.server_client_id))
                .requestEmail()
                .build();

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso);
        if (context instanceof FragmentActivity) {
            _activity = (FragmentActivity) context;
            builder.enableAutoManage((FragmentActivity) context, this);
        }
        _googleApiClient = builder.build();
    }

    @Override
    public String getProviderId() {
        return _activity.getString(R.string.google_provider);
    }

    @Override
    public void setAuthenticationCallback(IdpCallback callback) {
        _idpCallback = callback;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_GET_TOKEN) {
            Log.v(LOG.AUTH, "result back from token request");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result != null) {
                if (result.isSuccess()) {
                    Log.v(LOG.AUTH, "google provider success create response");
                    disconnect();
                    _idpCallback.onSuccess(createIDPResponse(result.getSignInAccount()));
                } else {
                    onError(result);
                }
            } else {
                onError("No result found in intent");
            }
        }
    }

    @Override
    public IdpResponse refreshToken() {
        // connect GoogleApiClient and retrieve idToken
        IdpResponse response = null;
        try {
            ConnectionResult result = _googleApiClient.blockingConnect();
            if (result.isSuccess()) {
                GoogleSignInResult gsr = Auth.GoogleSignInApi.silentSignIn(_googleApiClient).await();
                if (gsr != null && gsr.isSuccess()) {
                    response = createIDPResponse(gsr.getSignInAccount());
                    Log.d(LOG.AUTH, "doInBackground: GoogleApiClient connected: " + _googleApiClient
                            .isConnected());
                    Log.d(LOG.AUTH, "doInBackground: idToken retrieved: " + gsr.getSignInAccount()
                            .getIdToken());
                } else {
                    Log.d(LOG.AUTH, "doInBackground: FAILURE did not get idToken");
                }
            }
        } catch (Exception ex) {
            Log.d(LOG.AUTH, "doInBackground: ERROR idToken retrieve");
        }
        finally {
            _googleApiClient.disconnect();
        }
        return response;
    }

    @Override
    public void startLogin(Activity activity) {
        Log.v(LOG.AUTH, "start google provider " + (_googleApiClient == null) + " " + (_googleApiClient
                .isConnected()));
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(_googleApiClient);
        activity.startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    @Override
    public void signOut() {
        _googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                _googleApiClient.unregisterConnectionCallbacks(this);
                Auth.GoogleSignInApi.signOut(_googleApiClient).
                        setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()) {
                                    Log.v(LOG.AUTH, "---------+++++++ signout success " + _googleApiClient);
                                }
                            }
                        });
            }
            @Override
            public void onConnectionSuspended(int i) {

            }
        });
        _googleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        DebugUtil.toast(_activity, "Ein Fehler trat auf.");
    }

    private void onError(GoogleSignInResult result) {
        String errorMessage = result.getStatus().getStatusMessage();
        onError(String.valueOf(result.getStatus().getStatusCode()) + " " + errorMessage);
    }

    private void onError(String errorMessage) {
        Log.v(LOG.AUTH, "Error logging in with Google. " + errorMessage);
        Bundle extra = new Bundle();
        extra.putString(ERROR_KEY, errorMessage);
        _idpCallback.onFailure(extra);
    }

    private void disconnect() {
        if (_googleApiClient != null) {
            _googleApiClient.stopAutoManage((FragmentActivity) _activity);
            _googleApiClient.disconnect();
        }
    }

    private IdpResponse createIDPResponse(GoogleSignInAccount account) {
        return new IdpResponse(
                getProviderId(),
                account.getEmail(),
                account.getIdToken()
        );
    }
}
