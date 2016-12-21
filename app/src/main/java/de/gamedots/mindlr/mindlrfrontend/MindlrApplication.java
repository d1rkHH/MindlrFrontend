package de.gamedots.mindlr.mindlrfrontend;


import android.app.Application;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import de.gamedots.mindlr.mindlrfrontend.auth.IdentityProvider;
import io.fabric.sdk.android.Fabric;


public class MindlrApplication extends Application {

    private static MindlrApplication _instance;
    public static MindlrApplication getInstance() {
        return _instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.twitter_key),
                getString(R.string.twitter_secret));
        Fabric.with(this, new Twitter(authConfig));
    }



    /* Class representing global user */
    public static class User {
        /* User id set on application start */
        private static long _id;
        /* Global accessible reference to provider in current session */
        private static IdentityProvider _provider;

        public static void create(long id, IdentityProvider provider) {
            _id = id;
            _provider = provider;
        }

        public static long getId() {
            return _id;
        }

        /* Provider getter and setter */
        public static void setIdentityProvider(IdentityProvider provider){
            _provider = provider;
        }
        public static IdentityProvider getIdentityProvider(){
            return _provider;
        }
    }
}
