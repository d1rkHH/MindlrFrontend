package de.gamedots.mindlr.mindlrfrontend.model.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "profile")
public class Profile extends Model {

    @Column(name = "user", notNull = true)
    User user;

    //global sync fields
    @Column(name = "uppercent")
    int upPercent = -1;

    @Column(name = "downpercent")
    int downPercent = -1;

    // more data for profile information that are not directly
    // bound to the user table
}
