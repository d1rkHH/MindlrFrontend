package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.util.Utility;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean firstStart = Utility.isFirstStart(this);

        boolean authenticated = Utility.getAuthStateFromPreference(this);
        Log.v(LOG.AUTH, "user is authenticated " + authenticated);

        if (firstStart){
            // fill categories
           /* Intent intent = new Intent(this, DatabaseIntentService.class);
            intent.setAction(DatabaseIntentService.INSERT_CATEGORIES_ACTION);
            startService(intent);
            Utility.invalidateFirstStart(this);*/
        }

        if (authenticated) {
            Utility.loadUserFromDB(this);
            finishAndRedirect(MainActivity.class);
        } else {
            // no user signedIn earlier, so launch LoginActivity and try to authenticate him
            finishAndRedirect(LoginActivity.class);
        }
    }

    private void finishAndRedirect(Class cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        finish();
    }
}
