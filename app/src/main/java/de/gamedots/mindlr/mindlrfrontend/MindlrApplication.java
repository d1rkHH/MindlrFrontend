package de.gamedots.mindlr.mindlrfrontend;


import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by Dirk on 24.04.16.
 */
public class MindlrApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ActiveAndroid.initialize(this);
    }
}
