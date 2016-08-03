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

@SuppressWarnings("ConstantConditions")
public class LoginActivity extends AuthHandlerActivity implements APICallTask.OnProcessSuccessListener {
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
    }

    @Override
    public void onSignInSuccess() {
        //create user account on backend server
        new SignInTask(this, new JSONObject()).execute();
    }

    @Override
    public void onProcessSuccess() {
        // user was successfully verified and account was created
        startActivity(new Intent(this, MainActivity.class));
    }
}
