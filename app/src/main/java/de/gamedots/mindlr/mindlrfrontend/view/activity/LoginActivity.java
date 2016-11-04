package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import de.gamedots.mindlr.mindlrfrontend.AuthHandlerActivity;
import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.jobs.APICallTask;
import de.gamedots.mindlr.mindlrfrontend.jobs.SignInTask;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.util.DebugUtil;

@SuppressWarnings("ConstantConditions")
public class LoginActivity extends AuthHandlerActivity implements APICallTask.OnProcessSuccessListener {

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
                startGoogleSignIn();
            }
        });

        googleSilentSignIn();
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

    @Override
    public void onSignInSuccess() {
        Log.d(LOG.AUTH, "onSignInSuccess: SignIn was successful start TASK");
        //create user account on backend server if not existing
        new SignInTask(this, new JSONObject(), this).execute();
    }

    @Override
    public void onSignInFailure() {
        DebugUtil.toast(this, "Anmeldung fehlgeschlagen");
    }

    // APICallTask callback
    @Override
    public void onProcessSuccess() {
        Log.d(LOG.AUTH, "onProcessSuccess: User verified start MAIN");

        // user was successfully verified and account was created
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
