package de.gamedots.mindlr.mindlrfrontend;

import android.accounts.Account;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.gamedots.mindlr.mindlrfrontend.Global.BACKEND_METHOD_KEY;
import static de.gamedots.mindlr.mindlrfrontend.Global.METHOD_POST;
import static de.gamedots.mindlr.mindlrfrontend.Global.METHOD_VERIFY;
import static de.gamedots.mindlr.mindlrfrontend.Global.SERVER_URL;

/**
 * Created by dirk on 27.10.2015.
 */
public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private final String SERVER_CLIENT_ID
            = "319486160725-00pmin4g2p7sar8p7k7mjod7fra8hiep.apps.googleusercontent.com";

    public static final String TAG = "LoginActivity";

    /* Keys for persisting instance variables in savedInstanceState */
    private static final String KEY_IS_RESOLVING = "is_resolving";
    private static final String KEY_SHOULD_RESOLVE = "should_resolve";

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Google Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    private boolean mShouldResolve = false;

    private ConnectionResult mConnectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "On Create()-method");
        setContentView(R.layout.activity_login);

        // Restore from saved instance state
        if (savedInstanceState != null) {
            mIsResolving = savedInstanceState.getBoolean(KEY_IS_RESOLVING);
            mShouldResolve = savedInstanceState.getBoolean(KEY_SHOULD_RESOLVE);
        }

        // Build GoogleApiClient and specify the initial scope
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                        // .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(new Scope(Scopes.PROFILE)) // access to basic profile information
                .addScope(new Scope(Scopes.EMAIL))   // access to the users email address
                        // PLUS_LOGIN or PLUS_ME for Google+ ID
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Client connect OnSTART()");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "disconnect client OnSTOP()");
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        mShouldResolve = false;
        Log.d(TAG, "connection successful");

        Log.d(TAG, "start SendGetIdTokenTask");
        new GetAndSendIdTokenTask().execute();

        //TODO: look for good way to start in mainactivity and check if loged in, otherwise redirect to login activity
        startActivity(new Intent(this, MainActivity.class));

        /* Get Profile Information only CLIENT SIDE !!!!! NOT USED TO SEND TO BACKEND */
        // if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
        // Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        // String personName = currentPerson.getDisplayName();
        // String personPhoto = currentPerson.getImage().getUrl();
        // String personGooglePlusProfile = currentPerson.getUrl();
    }


    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost. The GoogleApiClient will automatically
        // attempt to re-connect. Any UI elements that depend on connection to Google APIs should
        // be hidden or disabled until onConnected is called again.
        Log.w(TAG, "onConnectionSuspended:" + i);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "sign in clicked");

        if (v.getId() == R.id.sign_in_button)
            signInWithGplus();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in.

        Log.d(TAG, "connec failed" + connectionResult);
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIsResolving) {
            // Store the ConnectionResult for later usage
            mConnectionResult = connectionResult;

            if (mShouldResolve) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {

        Log.d(TAG, "Activity Result comes");
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mShouldResolve = false;  // if error solution was not successful - not resolve further
            }

            mIsResolving = false;

            // user has resolved the issue -> call connect() again
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    /**
     * Sign-in into google
     */
    private void signInWithGplus() {
        Log.d(TAG, "SIGNIN with GPLUS");
        if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
            // User clicked the sign-in button, so begin the sign-in process and automatically
            // attempt to resolve any errors that occur.
            mShouldResolve = true;
            resolveSignInError();
        }
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        Log.d(TAG, "resolving Error" + (mConnectionResult == null));
        if (!mIsResolving && mShouldResolve) {

            // is there a resolution that can be started?
            if (mConnectionResult.hasResolution()) {
                try {
                    mIsResolving = true;
                    // resolves an error by starting any intents requiring user interaction
                    // the activity's onActivityResult method will be invoked after the user is done
                    mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);

                } catch (IntentSender.SendIntentException e) {
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_RESOLVING, mIsResolving);
        outState.putBoolean(KEY_SHOULD_RESOLVE, mShouldResolve);
    }


    // Step 1: get the auth token for the user by calling getToken(...) and send it to the server via https
    // Step 2: the server verify the token using GoogleIdTokenVerfier class
    // Step 3: handle the server´s verification result in onPostExecute()
    private class GetAndSendIdTokenTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
            Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            String scopes = "audience:server:client_id:" + SERVER_CLIENT_ID;

            try {
                /* get token */
                String idToken = GoogleAuthUtil.getToken(getApplicationContext(), account, scopes);
                Log.i(TAG, "ID token: " + idToken);

                /* prepare to send IdToken to the server */
                if (idToken != null) {
                    HashMap<String, String> parameter = new HashMap<>();
                    parameter.put("idToken", idToken);
                    parameter.put(BACKEND_METHOD_KEY, METHOD_VERIFY);

                /* send the token via HTTPS */
                    Log.d(LOG.JSON, "About to create JSONParser");
                    JSONParser parser = new JSONParser();
                    Log.d(LOG.CONNECTION, "About to make HTTPRequest");

                    // TODO: verification on the backend
                    return parser.makeHttpRequest(SERVER_URL, METHOD_VERIFY, parameter);

                } else {
                    // There was some error getting the ID Token
                    Toast.makeText(getApplication(), "No token available", Toast.LENGTH_SHORT).show();
                    return null;
                }
            } catch (IOException e) {
                Log.e(TAG, "Error retrieving ID token.", e);
                return null;
            } catch (GoogleAuthException e) {
                Log.e(TAG, "Error retrieving ID token.", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                try {
                    if (result.getBoolean("SUCCESS")) {
                        // TODO: read json, save triplet .....
                        Log.d(LOG.VERIFIED, "Sucessfuly verified the Google user on the backend");
                        Toast.makeText(getApplication(), "Nutzer wurde im Backend verifiziert", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException jex) {
                    Log.d(LOG.JSON, "Error parsing data into objects");
                    jex.printStackTrace();
                }
            }
        }

    }


}
