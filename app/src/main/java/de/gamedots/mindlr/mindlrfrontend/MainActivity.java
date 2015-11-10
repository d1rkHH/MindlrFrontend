package de.gamedots.mindlr.mindlrfrontend;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static de.gamedots.mindlr.mindlrfrontend.Global.BACKEND_METHOD_KEY;
import static de.gamedots.mindlr.mindlrfrontend.Global.METHOD_POST;
import static de.gamedots.mindlr.mindlrfrontend.Global.METHOD_VERIFY;
import static de.gamedots.mindlr.mindlrfrontend.Global.SERVER_URL;

public class MainActivity extends ToolbarActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private enum FragTrans {ADD, REPLACE}

    private static final String TAG = "SignInActivity";

    /* constant defining the request code for get idToken */
    private static final int RC_GET_TOKEN = 9002;


    private GoogleApiClient _mGoogleApiClient;


    private ProgressDialog _mProgressDialog;


    private boolean saveInstanceAvaibable;


    private boolean _isUserSignedIn;


    private boolean _shouldReplace;


    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        Log.d(TAG, "fragment attached = " + fragment.getId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        saveInstanceAvaibable = savedInstanceState != null;
        Log.d(TAG, "bilde api client");
        buildGoogleApiClient();

    }


    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPref =
                getSharedPreferences(getString(R.string.LoginStatePreference), Context.MODE_PRIVATE);
        _isUserSignedIn = sharedPref.getBoolean(getString(R.string.UserLoginState), false);

        Log.d(TAG, "user signed in : " + _isUserSignedIn);
        handleUserSignInResult(_isUserSignedIn);



        /* Try GoogleSilentSignIn */
        //googleSilentSignIn();
    }

    private void handleUserSignInResult(boolean isUserSignedIn) {

        if (isUserSignedIn) {
            PostViewFragment fragment = new PostViewFragment();

            //Load the first bunch of posts in the list of posts
            if (!PostLoader.getInstance().isInitialized()) {
                PostLoader.getInstance().initialize(fragment);
            }

            if(_shouldReplace){
                /* setup post View fragment shown dynamically */
                handleFragmentTransaction(fragment, FragTrans.REPLACE);
            } else{
                handleFragmentTransaction(fragment, FragTrans.ADD);
            }

            if (!saveInstanceAvaibable) {
            }

        } else { /* user need to sign in -> launch login fragment */
            boolean shouldAdd = (this.getSupportFragmentManager().getFragments() == null); /* no fragments attached earlier */
            LoginFragment fragment = new LoginFragment();
            if (shouldAdd) {
                handleFragmentTransaction(fragment, FragTrans.ADD);
            } else {
                handleFragmentTransaction(fragment, FragTrans.REPLACE);
            }
        }
    }

    /**
     * Start SignIn-process by starting the SignIn_Intent
     * which will be retrieved in onActivityResult()
     */
    public void signInAndGetIdToken() {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no
        // consent screen will be shown here.
        //findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);
        Log.d(TAG, "auth api is : " + (Auth.GoogleSignInApi == null));
        Log.d(TAG, "api client is: " + (_mGoogleApiClient == null));
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(_mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GET_TOKEN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess());

            handleGoogleSignInFor(result);
        }
    }

    private void handleGoogleSignInFor(GoogleSignInResult result) {
        Log.d(TAG, "handleGoogleSign-in:" + result.isSuccess());

        if (result.isSuccess()) {
            /* when success GoogleAPIClient is connected as well*/
            GoogleSignInAccount acct = result.getSignInAccount();
            String idToken = acct.getIdToken();
            Log.d(TAG, "API Client : isConnected = " + _mGoogleApiClient.isConnected());

            _isUserSignedIn = true;
            _shouldReplace = true;
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.LoginStatePreference), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.UserLoginState), true);
            editor.commit();
            handleUserSignInResult(_isUserSignedIn);

            // Show signed-in UI.
            Log.d(TAG, "idToken:" + idToken);
            Toast.makeText(this, "idToken: " + idToken, Toast.LENGTH_LONG).show();

            // TODO(user): send token to server and validate server-side
            Log.d(TAG, "start idTokenTask");
            new SendIdTokenTask().execute(idToken, acct.getEmail());

        } else {
            //signed out, show log in UI
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void buildGoogleApiClient() {

        // Request only the user's ID token, which can be used to identify the
        // user securely to your backend. This will contain the user's basic
        // profile (name, profile picture URL, etc) so you should not need to
        // make an additional call to personalize your application.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // Build GoogleAPIClient with the Google Sign-In API and the above options.
        _mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void googleSilentSignIn() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(_mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult signInResult = opr.get();
            handleGoogleSignInFor(signInResult);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleGoogleSignInFor(googleSignInResult);
                }
            });
        }
    }

    //**********************[Sign Out or Revoke Access]***************

    /**
     * Clears the account which is connected to Mindlr.
     * To sign in again, the user must choose their account again.
     */
    private void signOut() {
        Auth.GoogleSignInApi.signOut(_mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "signOut:onResult:" + status);
                    }
                });
        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
    }

    /**
     * [OPTIONAL]
     * Completely disconnect the user´s google account from Mindlr.
     * Delete all information obtained from Google API´s
     */
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(_mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "revokeAccess:onResult:" + status);
                        //TODO: redirect to login status/ delete all collection informations
                    }
                });
    }

    //**********************[Progress for user exp]*******************

    private void showProgressDialog() {
        if (_mProgressDialog == null) {
            _mProgressDialog = new ProgressDialog(this);
            _mProgressDialog.setMessage(getString(R.string.loading));
            _mProgressDialog.setIndeterminate(true);
        }

        _mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (_mProgressDialog != null && _mProgressDialog.isShowing()) {
            _mProgressDialog.hide();
        }
    }

    //****************************************************************************
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.overflowmenu) {
            return true;
        }
        if (id == R.id.writePost) {
            //TODO: start post write activity
            startActivity(new Intent(this, WritePostActivity.class));
            Toast.makeText(this, "Post Write clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.share) {
            //TODO: start share activity
            Toast.makeText(this, "Share clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.profil) {
            //TODO: start profil activity
            Toast.makeText(this, " Profil clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.posts) {
            //TODO: start posts activity
            startActivity(new Intent(this, UserPostsActivity.class));
            Toast.makeText(this, " Show Posts clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.favorit) {
            //TODO: start favorits acitivity
            Toast.makeText(this, " Favorits clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.settings) {
            //TODO: start setting acitivity
            Toast.makeText(this, " Settings clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleFragmentTransaction(Fragment fragment, FragTrans todo) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (todo) {
            case ADD:
                transaction.add(R.id.activity_content, fragment);
                break;
            case REPLACE:
                transaction.replace(R.id.activity_content, fragment);
                break;
        }
        transaction.commit();
    }

    private class SendIdTokenTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            HashMap<String, String> parameter = ServerCommunicationUtilities.newDefaultParameterHashMap();
            //METHOD SPECIFIC PARAMETERS
            parameter.put(BACKEND_METHOD_KEY, METHOD_VERIFY);
            parameter.put("AUTH_PROVIDER", Global.AuthProvider.GOOGLE.name());
            parameter.put("ID_TOKEN", params[0] /* idToken */);
            parameter.put("EMAIL", params[1] /* email */ );

            Log.d(LOG.JSON, "About to create JSONParser");
            JSONParser parser = new JSONParser();
            Log.d(LOG.CONNECTION, "About to make HTTPRequest");
            return parser.makeHttpRequest(SERVER_URL, METHOD_POST, parameter);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            new PostExecuteBehaviour() {
                @Override
                public void onSuccess(JSONObject result) {
                    Log.d(LOG.VERIFIED, "successful verified the user on backend");
                    Toast.makeText(getApplicationContext(), "Verfied User", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(JSONObject result) {
                    try {
                        String errorMsg = result.getString("ERROR");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Log.e(LOG.JSON, Log.getStackTraceString(e));
                    }
                }
            }.onPostExec(result);
        }
    }

}
