package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;

import de.gamedots.mindlr.mindlrfrontend.AuthHandlerActivity;
import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.controller.PostLoader;
import de.gamedots.mindlr.mindlrfrontend.util.ShareUtil;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.PostViewFragment;

/**
 * "MAIN" activity in the app. When it launches, it checks if the user is (still)
 * logged in. If not, show the LoginFragment to the user, otherwise initialize
 * posts.
 */
public class MainActivity extends AuthHandlerActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    /*R.string.LoginStatePreference, R.string.UserLoginState)*/
    private static final String PREF_NAME = "";
    private SharedPreferences _prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // save any state that should be persistent upon user session
    }


    // Authentication callbacks
    @Override
    public void onSignInSuccess() {}

    @Override
    public void onSignInFailure() {}

    // menu items and actions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_share) {
            //TODO: put in the current post text and category
            ShareUtil.showShareIntent("Empty Test text", this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // navigation drawer actions
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_profile: startActivity(new Intent(this, ProfileActivity.class)); break;
            case R.id.nav_drafts:  startActivity(new Intent(this, DraftsActivity.class)); break;
            case R.id.nav_setting: break;
            case R.id.nav_logout:  signOut();
            case R.id.nav_help:    break;
        }

        //getSupportActionBar().setTitle(item.getTitle());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeUI() {
        PostViewFragment fragment = new PostViewFragment();
        if (!PostLoader.getInstance().isInitialized()) {
            PostLoader.getInstance().initialize(this, fragment);
        }
        //add PostViewFragment dynamically
        getSupportFragmentManager().beginTransaction().add(R.id.main_content,fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, getToolbar(), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected boolean isFABenabled() {
        return true;
    }

    private SharedPreferences getPref() {
        if (_prefs == null) {
            _prefs = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        return _prefs;
    }
}
