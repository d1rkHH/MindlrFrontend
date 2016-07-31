package de.gamedots.mindlr.mindlrfrontend;


import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;

import de.gamedots.mindlr.mindlrfrontend.model.models.AuthProvider;
import de.gamedots.mindlr.mindlrfrontend.model.models.Post;
import de.gamedots.mindlr.mindlrfrontend.model.models.User;
import de.gamedots.mindlr.mindlrfrontend.model.models.UserPost;

/**
 * Created by Dirk on 24.04.16.
 */
public class MindlrApplication extends Application {

    private static User _user = null;

    @Override
    public void onCreate() {
        super.onCreate();
        //this.deleteDatabase("mindlr.db");

        Configuration.Builder config = new Configuration.Builder(this);
        config.addModelClasses(Post.class, User.class, AuthProvider.class, UserPost.class);
        ActiveAndroid.initialize(config.create());
    }

    public static void setUser(int id) {
        _user = User.load(User.class, id);
    }

    public static User userInstance() {
        return (_user == null) ? null : _user;
    }
}
