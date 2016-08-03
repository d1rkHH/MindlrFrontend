package de.gamedots.mindlr.mindlrfrontend;


import android.app.Application;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;

import de.gamedots.mindlr.mindlrfrontend.model.models.AuthProvider;
import de.gamedots.mindlr.mindlrfrontend.model.models.Post;
import de.gamedots.mindlr.mindlrfrontend.model.models.Profile;
import de.gamedots.mindlr.mindlrfrontend.model.models.User;
import de.gamedots.mindlr.mindlrfrontend.model.models.UserCreatedPost;
import de.gamedots.mindlr.mindlrfrontend.model.models.UserPost;


public class MindlrApplication extends Application {

    private static User _user = null;
    private static MindlrApplication _instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Application", "MindlrApplication will be created!");
        //this.deleteDatabase("mindlr.db");

        _instance = this;

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
}
