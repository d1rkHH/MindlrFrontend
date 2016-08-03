package de.gamedots.mindlr.mindlrfrontend;

import android.content.Context;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Dirk on 03.08.16.
 */
public class AuthHandlerHelper {

    private GoogleApiClient _googleApiClient;
    private Context _context;

    public AuthHandlerHelper(Context appContext) {
        this._context = appContext;
        createGoogleApiClient();
    }

    private void createGoogleApiClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(_context.getString(R.string.server_client_id))
                .requestEmail()
                .build();

        _googleApiClient = new GoogleApiClient.Builder(_context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public GoogleApiClient getGoogleApiClient(){
        return _googleApiClient;
    }
}
