package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import de.gamedots.mindlr.mindlrfrontend.model.models.User;

import static de.gamedots.mindlr.mindlrfrontend.model.models.AuthProvider.Auth_Provider.GOOGLE;

/**
 * Created by dirk on 12.04.2016.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User user = User.getLastUserIfAny();
        // no user signedIn earlier, so launch LoginActivity and try to authenticate him
        if (user == null) {
            finishAndRedirect(LoginActivity.class);
        } else if (user.provider.name.equals(GOOGLE)) {
            // a user (latest) already signed up earlier with google
            finishAndRedirect(MainActivity.class);
        }
    }

    private void finishAndRedirect(Class cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        finish();
    }
}
