package de.gamedots.mindlr.mindlrfrontend.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Interface describing and Identity Provider. A provider is
 * responsible for authentication the user, returning a response with
 * auth information and log the user out.
 */

public interface IdentityProvider {

    String getProviderId();

    void setAuthenticationCallback(IdpCallback callback);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    IdpResponse refreshToken();

    void startLogin(Activity activity);

    void signOut();

    interface IdpCallback {
        void onSuccess(IdpResponse idpResponse);

        void onFailure(Bundle extra);
    }
}
