package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import de.gamedots.mindlr.mindlrfrontend.AuthHandlerActivity;
import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.jobs.SignInTask;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.util.DebugUtil;
import de.gamedots.mindlr.mindlrfrontend.util.Utility;

@SuppressWarnings("ConstantConditions")
public class LoginActivity extends AuthHandlerActivity implements SignInTask.OnSignInProcessSuccessListener {

    public static final String SIGNOUT_EXTRA = "signout";

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login;
    }

    @Override
    protected boolean isToolbarEnabled() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.google_signIn_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG.AUTH, "Start GoogleSignIn");
                _authProvider = getString(R.string.google_provider);
                startGoogleSignIn();
            }
        });

        if (!(getIntent() != null && getIntent().hasExtra(SIGNOUT_EXTRA))) {
            _authProvider = getString(R.string.google_provider);
            googleSilentSignIn();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        _googleApiClient.registerConnectionCallbacks(this);
        _googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _googleApiClient.unregisterConnectionCallbacks(this);
        _googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (getIntent() != null && getIntent().hasExtra(SIGNOUT_EXTRA)) {
            Intent launchIntent = getIntent();
            boolean signout = launchIntent.getBooleanExtra(SIGNOUT_EXTRA, false);
            if (signout) {
                signOut();
            }
        }
    }

    // Callback for the google sign in success event
    @Override
    public void onSignInSuccess() {
        Log.d(LOG.AUTH, "onSignInSuccess: SignIn was successful start TASK");
        //create user account on backend server if not existing
        if(Utility.isNetworkAvailable(this)){
            new SignInTask(this, new JSONObject(), _authProvider, this).execute();
        } else {
            DebugUtil.toast(this, getString(R.string.auth_error_network_unavailable));
        }
    }

    @Override
    public void onSignInFailure() {
        if (!_isSilentTry) {
            DebugUtil.toast(this, getString(R.string.auth_error_unknown));
        }
    }

    // SignInTask callback when google account synced with backend
    @Override
    public void onSignInProcessSuccess() {
        Log.d(LOG.AUTH, "onSignInSuccess: User verified start MAIN");

        // user was verified on backend so set authstate accordingly
        Utility.addAuthStateToPreference(this, true);

        // Create new user if not already exists
        Utility.createUserEntryIfNotExists(this, _email, _authProvider);

        // user was successfully verified and account was created
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
