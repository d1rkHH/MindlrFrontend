package de.gamedots.mindlr.mindlrfrontend.auth;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class wraps the information returned by an {@link IdentityProvider} on
 * successful authentication.
 */

public class IdpResponse {

    /* Provider unique identifier [GOOGLE, TWITTER] */
    private final String _providerId;

    /* Email for the user. May be null for twitter */
    private final String _email;

    /* Provider specific authentication token */
    private final String _token;

    /* Provider secret. This is twitter related */
    private final String _secret;


    public IdpResponse(
            String providerId, String email, String token) {
        this(providerId, email, token, null);
    }

    public IdpResponse(
            String providerId,
            String email,
            String token,
            String secret) {
        _providerId = providerId;
        _email = email;
        _token = token;
        _secret = secret;
    }

    /* Return the type of provider that generated this response object. */
    public String getProviderType() {
        return _providerId;
    }

    /* Get email received from login if available (for twitter it is not guaranteed) */
    public String getEmail() {
        return (_email != null) ? _email : "";
    }

    /* Get the received token from logging in with an Identity Provider */
    public String getIdpToken() {
        return (_token != null) ? _token : "";
    }

    /* Return the token secret received as a result of logging in with Twitter */
    public String getIdpSecret() {
        return (_secret != null) ? _secret : "";
    }

    /* Helper method for API-Call post parameters */
    public JSONObject toAuthDataJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("token", getIdpToken());
        json.put("secret", getIdpSecret());
        return json;
    }
}
