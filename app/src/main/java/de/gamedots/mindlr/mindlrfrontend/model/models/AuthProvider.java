package de.gamedots.mindlr.mindlrfrontend.model.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

/**
 * Auth provider selection.
 */
@Table(name = "auth_providers")
public class AuthProvider extends Model {

    public enum Auth_Provider {
        GOOGLE, TWITTER, LINKEDIN, REDDIT
    }

    @Expose
    @Column(name = "name")
    public Auth_Provider name;

    public AuthProvider() {
        super();
    }

    public AuthProvider(Auth_Provider name) {
        this.name = name;
    }
}