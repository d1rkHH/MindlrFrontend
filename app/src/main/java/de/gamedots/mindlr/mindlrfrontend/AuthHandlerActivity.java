package de.gamedots.mindlr.mindlrfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import de.gamedots.mindlr.mindlrfrontend.model.models.User;
import de.gamedots.mindlr.mindlrfrontend.util.DebugUtil;
import de.gamedots.mindlr.mindlrfrontend.view.activity.BaseActivity;
import de.gamedots.mindlr.mindlrfrontend.view.activity.LoginActivity;

public abstract class AuthHandlerActivity extends BaseActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "AuthHandlerActivity";
    private static final int RC_GET_TOKEN = 9000;
    protected GoogleApiClient _googleApiClient;

    // need to be refreshed with every APICallTask
    protected String _idToken;

    public abstract void onSignInSuccess();
    public abstract void onSignInFailure();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        _googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    // Main Handler for GoogleSignInResults
    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleGoogleSignIn:" + (result == null ? "null" : result.getStatus()));

        boolean isSignedIn = (result != null) && result.isSuccess();
        if (isSignedIn) {
            GoogleSignInAccount acct = result.getSignInAccount();
            _idToken = acct.getIdToken();
            onSignInSuccess();

        } else {
            onSignInFailure();
        }
    }

    // Try to sign the user in silently using cached credentials
    protected void googleSilentSignIn() {
        OptionalPendingResult<GoogleSignInResult> opr =
                Auth.GoogleSignInApi.silentSignIn(_googleApiClient);

        if (opr.isDone()) {
            Log.d(TAG, "googleSilentSignIn: Cached sign in");
            handleGoogleSignInResult(/*GoogleSignInResult*/ opr.get());
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleGoogleSignInResult(googleSignInResult);
                }
            });
        }
    }

    // Full login process with account chooser.
    protected void startGoogleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(_googleApiClient);
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GET_TOKEN) {
            handleGoogleSignInResult(/*GoogleSignInResult*/Auth.GoogleSignInApi.getSignInResultFromIntent(data));
        }
    }

    // SignOut/Revoke
    protected void signOut() {
        User.clearUser(MindlrApplication.userInstance());

        Auth.GoogleSignInApi.signOut(_googleApiClient).
                setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        startActivity(new Intent(AuthHandlerActivity.this, LoginActivity.class));
                        AuthHandlerActivity.this.finish();
                    }
                });
    }

    protected void revokeAccess() {
        // TODO: confirmation dialog
        Auth.GoogleSignInApi.revokeAccess(_googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        handleGoogleSignInResult(null);
                    }
                });
    }

    // Logging Callbacks
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        DebugUtil.toast(this, "Ein Fehler trat auf.");
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected: GoogleApiClient was successful connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
    }
}
