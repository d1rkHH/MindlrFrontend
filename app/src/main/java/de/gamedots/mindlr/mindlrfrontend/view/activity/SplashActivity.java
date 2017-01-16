package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.controller.PostLoader;
import de.gamedots.mindlr.mindlrfrontend.data.DatabaseIntentService;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract;
import de.gamedots.mindlr.mindlrfrontend.jobs.GetCategoriesTask;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.util.Utility;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean firstStart = Utility.isFirstStart(this);
        Log.v(LOG.AUTH, "firststart " + firstStart);


        boolean authenticated = Utility.getAuthStateFromPreference(this);
        Log.v(LOG.AUTH, "user is authenticated " + authenticated);

        new GetCategoriesTask(this, null).execute();

        if (firstStart) {
            // create auth provider
            ContentValues cv = new ContentValues();
            cv.put(MindlrContract.AuthProviderEntry.COLUMN_NAME, getString(R.string.google_provider));
            getContentResolver().insert(MindlrContract.AuthProviderEntry.CONTENT_URI, cv);

            cv = new ContentValues();
            cv.put(MindlrContract.AuthProviderEntry.COLUMN_NAME, getString(R.string.twitter_provider));
            getContentResolver().insert(MindlrContract.AuthProviderEntry.CONTENT_URI, cv);

            // fill categories
            Intent intent = new Intent(this, DatabaseIntentService.class);
            intent.setAction(DatabaseIntentService.INSERT_CATEGORIES_ACTION);
            startService(intent);
            Utility.invalidateFirstStart(this);
            finishAndRedirect(TutorialActivity.class);
        } else {
            if (authenticated) {
                Utility.loadUserFromDB(this);
                if (!PostLoader.getInstance().isInitialized()) {
                    PostLoader.getInstance().initialize();
                }
                finishAndRedirect(MainActivity.class);
            } else {
                // no user signedIn earlier, so launch LoginActivity and try to authenticate him
                finishAndRedirect(TutorialActivity.class);
            }
        }
    }

    private void finishAndRedirect(Class cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        finish();
    }
}
