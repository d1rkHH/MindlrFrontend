package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.controller.PostLoader;
import de.gamedots.mindlr.mindlrfrontend.jobs.SignInTask;
import de.gamedots.mindlr.mindlrfrontend.util.ShareUtil;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.LoginFragment;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.PostViewFragment;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.ProfileFragment;

/**
 * "MAIN" activity in the app. When it launches, it checks if the user is (still)
 * logged in. If not, show the LoginFragment to the user, otherwise initialize
 * posts.
 */
public class MainActivity extends BaseNavActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        LoginFragment.OnSignInButtonClickedListener {

    private ShareActionProvider _shareActionProvider;

    @Override
    public void onSignInButtonClicked() {
        signInAndGetIdToken();
    }

    private enum FragTrans {ADD, REPLACE}

    private static final String TAG = "SignInActivity";
    /* constant defining the request code for get idToken */
    private static final int RC_GET_TOKEN = 9002;
    private GoogleApiClient _googleApiClient;
    private boolean _saveInstanceAvailable;
    private boolean _isUserSignedIn;
    private boolean _shouldReplace;
    private SharedPreferences _prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _saveInstanceAvailable = savedInstanceState != null;
        _prefs = getSharedPreferences(getString(R.string.LoginStatePreference), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = _prefs.edit();
        editor.putBoolean(getString(R.string.UserLoginState), false);
        editor.commit();
        Log.d(TAG, "build api client");
        buildGoogleApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();

        _isUserSignedIn = _prefs.getBoolean(getString(R.string.UserLoginState), false);
        _isUserSignedIn = true;
        Log.d(TAG, "user signed in : " + _isUserSignedIn);
        handleUserSignInResult(_isUserSignedIn);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // save any state that should be persistent upon user session
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // result returned from launching the intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GET_TOKEN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess());

            handleGoogleSignInFor(result);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // an unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       if(item.getItemId() == R.id.action_share){
           //TODO: put in the current post text and category
           ShareUtil.showShareIntent("Empty Test text", this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_content, new PostViewFragment());
            transaction.commit();

        } else if (id == R.id.nav_profile) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_content, new ProfileFragment());
            transaction.commit();

        } else if (id == R.id.nav_write_post) {
            startActivity(new Intent(this, WritePostActivity.class));

        } else if (id == R.id.nav_my_posts) {
            Intent intent = new Intent(this, UserPostsActivity.class);
            intent.putExtra("TabSelection", 0);
            startActivity(intent);

        } else if (id == R.id.nav_favorite) {
            Intent intent = new Intent(this, UserPostsActivity.class);
            intent.putExtra("TabSelection", 1);
            startActivity(intent);
        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_logout) {
            signOut();

        } else if (id == R.id.nav_help) {

        }

        //getSupportActionBar().setTitle(item.getTitle());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleUserSignInResult(boolean isUserSignedIn) {

        if (isUserSignedIn) {
            PostViewFragment fragment = new PostViewFragment();

            //Load the first bunch of posts in the list of posts
            if (!PostLoader.getInstance().isInitialized()) {
                PostLoader.getInstance().initialize(this, fragment);
            }

            if (_shouldReplace) {
                /* setup post View fragment shown dynamically */
                handleFragmentTransaction(fragment, FragTrans.REPLACE);
            } else {
                handleFragmentTransaction(fragment, FragTrans.ADD);
            }

            if (!_saveInstanceAvailable) {
            }

        } else { /* user need to sign in -> launch login fragment */
            boolean shouldAdd = (this.getSupportFragmentManager().getFragments() == null); /* no fragments attached earlier */
            LoginFragment fragment = LoginFragment.newInstance();
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
    private void signInAndGetIdToken() {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no
        // consent screen will be shown here.
        //findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);
        Log.d(TAG, "auth api is : " + (Auth.GoogleSignInApi == null));
        Log.d(TAG, "api client is: " + (_googleApiClient == null));
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(_googleApiClient);
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    private void handleGoogleSignInFor(GoogleSignInResult result) {
        Log.d(TAG, "handleGoogleSign-in:" + result.isSuccess());

        if (result.isSuccess()) {
            /* when success GoogleAPIClient is connected as well*/
            GoogleSignInAccount acct = result.getSignInAccount();
            String idToken = acct.getIdToken();
            Log.d(TAG, "API Client : isConnected = " + _googleApiClient.isConnected());

            _isUserSignedIn = true;
            _shouldReplace = true;
            SharedPreferences.Editor editor = _prefs.edit();
            editor.putBoolean(getString(R.string.UserLoginState), _isUserSignedIn);
            editor.putString("authProvider", "google");
            editor.putString("token", idToken);
            editor.commit();
            handleUserSignInResult(_isUserSignedIn);

            // Show signed-in UI.
            Log.d(TAG, "idToken:" + idToken);
            Toast.makeText(this, "idToken: " + idToken, Toast.LENGTH_LONG).show();

            Log.d(TAG, "start idTokenTask");
            new SignInTask(getApplicationContext(), new JSONObject()).execute();

        } else {
            //signed out, show log in UI
        }
    }

    private void buildGoogleApiClient() {

        // request the user's ID token and email; The ID token is used to
        // identify the user securely to the backend.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // build GoogleAPIClient with the Google Sign-In API and the above options.
        _googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    //**********************[Sign Out or Revoke Access]***************

    /**
     * Clears the account which is connected to Mindlr.
     * To sign in again, the user must choose their account again.
     */
    private void signOut() {
        Auth.GoogleSignInApi.signOut(_googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "signOut:onResult:" + status);
                    }
                });
        SharedPreferences.Editor editor = _prefs.edit();
        editor.putBoolean(getString(R.string.UserLoginState), false);
        editor.commit();
        _isUserSignedIn = false;
        handleUserSignInResult(_isUserSignedIn);
        Log.d(TAG, "AFTER SIGN OUT API CLIENT Is: " + _googleApiClient.isConnected());
        //findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
    }

    /**
     * [OPTIONAL]
     * In settings choos disconnect client -> log out UI -> revoke access
     * Completely disconnect the user´s google account from Mindlr.
     * Delete all information obtained from Google API´s
     */
    private void revokeAccess() {
        // TODO: confirmation dialog if(confirm) -> revokeAccess else -> ...
        Auth.GoogleSignInApi.revokeAccess(_googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "revokeAccess:onResult:" + status);
                        //TODO: redirect to login status/ delete all collection informations
                    }
                });
    }


    //****************************************************************

    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    private void handleFragmentTransaction(Fragment fragment, FragTrans todo) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (todo) {
            case ADD:
                transaction.add(R.id.main_content, fragment);
                break;
            case REPLACE:
                transaction.replace(R.id.main_content, fragment);
                break;
        }
        transaction.commit();
    }
}
