package de.gamedots.mindlr.mindlrfrontend;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;

import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.models.AuthProvider;
import de.gamedots.mindlr.mindlrfrontend.model.models.Post;
import de.gamedots.mindlr.mindlrfrontend.model.models.Profile;
import de.gamedots.mindlr.mindlrfrontend.model.models.User;
import de.gamedots.mindlr.mindlrfrontend.model.models.UserCreatedPost;
import de.gamedots.mindlr.mindlrfrontend.model.models.UserPost;


public class MindlrApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private static User _user = null;
    private static MindlrApplication _instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Application", "MindlrApplication will be created!");
        //this.deleteDatabase("mindlr.db");

        _instance = this;

        registerActivityLifecycleCallbacks(this);

        Configuration.Builder config = new Configuration.Builder(this);
        //noinspection unchecked
        config.addModelClasses(Post.class, User.class, AuthProvider.class, UserPost.class, UserCreatedPost.class, Profile.class);
        ActiveAndroid.initialize(config.create());
    }

    public static void setUser(User user) {
        _user = user;
    }

    public static MindlrApplication getInstance() {
        return _instance;
    }

    public static User userInstance() {
        return (_user == null) ? null : _user;
    }

    // Lifecycle logging
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d(LOG.LIFECYCLE, "onActivityCreated: " + "with: " + (savedInstanceState != null) + " " + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d(LOG.LIFECYCLE, "onActivityStarted: " + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(LOG.LIFECYCLE, "onActivityResumed: " + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d(LOG.LIFECYCLE, "onActivityPaused: " + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(LOG.LIFECYCLE, "onActivityStopped: " + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d(LOG.LIFECYCLE, "onActivitySaveInstanceState: " + "with : " + (outState != null) + " " + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(LOG.LIFECYCLE, "onActivityDestroyed: " + activity.getClass().getSimpleName());
    }
}
