package de.gamedots.mindlr.mindlrfrontend.model.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


@Table(name = "UserAuthProvider")
public class UserAuthProvider extends Model {

    @Column(name = "user", notNull = true)
    User user;

    @Column(name = "auth_provider", notNull = true)
    AuthProvider provider;

    public UserAuthProvider() {
        super();
    }

    public UserAuthProvider(User user, AuthProvider provider) {
        this.user = user;
        this.provider = provider;
    }
}
