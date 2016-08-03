package de.gamedots.mindlr.mindlrfrontend.model.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * User profile information.
 */
@Table(name = "profile")
public class Profile extends Model {

    @Column(name = "user_id", notNull = true)
    public User user;

    //global sync fields

    @Column(name = "uppercent")
    public int upPercent = -1;

    @Column(name = "downpercent")
    public int downPercent = -1;
}
