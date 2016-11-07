package de.gamedots.mindlr.mindlrfrontend;


import android.app.Application;
import android.util.Log;

import de.gamedots.mindlr.mindlrfrontend.logging.LOG;


public class MindlrApplication extends Application {

    private static MindlrApplication _instance;

    @Override
    public void onCreate() {
        super.onCreate();

        _instance = this;
    }

    public static MindlrApplication getInstance() {
        return _instance;
    }

    public static class User {
        // set on application startup
        private static long _id;
        private static String _authProvider;

        public static void create(long id, String provider) {
            _id = id;
            _authProvider = provider;
        }

        public static long getId() {
            return _id;
        }

        public static String getAuthProvider() {
            Log.v(LOG.AUTH, "AUthprovider ++++++++++" + (_authProvider == null));
            return (_authProvider == null) ?
                    _instance.getString(R.string.google_provider)
                    : _authProvider;
        }
    }
}
