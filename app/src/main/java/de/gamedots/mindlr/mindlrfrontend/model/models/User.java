package de.gamedots.mindlr.mindlrfrontend.model.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

/**
 * Simple User model.
 */
@Table(name = "users")
public class User extends Model {

    @Column(name = "remote_id", notNull = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    long server_id;

    /* If the a user signs in using a different auth provider than last time,
     * the email will be used to identify him. In that way the existing user
     * and user related data is used without creating a new user.
     */
    @Column(name = "email", unique = true)
    public String email;

    /* A User only have one auth provider at a time. That will be used to
    * authenticate the user in further session (silently) unless he logs out, so the provider
    * will be cleared and he needs to choose provider again */
    @Column(name = "auth_provider_id", notNull = true)
    public AuthProvider provider;

    /* Flag indicating the current activated user */
    @Column(name = "isActive")
    public boolean isActive = false;

    public User() {
        super();
    }

    public User(long server_id, String email) {
        this.server_id = server_id;
        this.email = email;
    }

    public static User getLastUserIfAny() {
        return new Select().from(User.class).where("isActive = ?", true).executeSingle();
    }

    public static void activateUser(User user){
        user.isActive = true;
        user.save();
    }

    public static void clearUser(User user) {
        if (user != null) {
            user.isActive = false;
            user.save();
        }
    }
}
