package de.gamedots.mindlr.mindlrfrontend.model.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "users")
public class User extends Model {

    @Expose
    @Column(name = "remote_id", notNull = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    long server_id;

    @Column(name = "email", unique = true)
    String email;


    public User() {
        super();
    }

    public User(long server_id, String email) {
        this.server_id = server_id;
        this.email = email;
    }
}
