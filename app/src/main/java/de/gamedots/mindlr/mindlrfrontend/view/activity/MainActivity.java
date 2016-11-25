package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.controller.PostLoader;
import de.gamedots.mindlr.mindlrfrontend.util.DebugUtil;
import de.gamedots.mindlr.mindlrfrontend.util.ShareUtil;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.PostViewFragment;

public class MainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    /*R.string.LoginStatePreference, R.string.UserLoginState)*/
    private static final String PREF_NAME = "";
    private ActionBarDrawerToggle _drawerToggle;
    private DrawerLayout _drawerLayout;
    private NavigationView _navigationView;
    private boolean _saveInstanceStateAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _saveInstanceStateAvailable = (savedInstanceState != null);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initializeUI();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (_drawerToggle != null) _drawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        _drawerToggle.onConfigurationChanged(newConfig);
    }

    // menu items and actions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (_drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_share:
                ShareUtil.showShareIntent(PostLoader.getInstance().getCurrent().getContentText(), this);
                break;
            case R.id.action_report:
                DebugUtil.toast(this, "Reported");
                break;
        }
        //TODO: put in the current post text and category
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (_drawerLayout.isDrawerOpen(GravityCompat.START)) {
            _drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // navigation drawer actions
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.nav_drafts:
                startActivity(new Intent(this, DraftsActivity.class));
                break;
            case R.id.nav_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_help:
                startActivity(new Intent(this, TutorialActivity.class));
                break;
        }

        //getSupportActionBar().setTitle(item.getTitle());
        if (_drawerLayout != null) _drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeUI() {
        PostViewFragment fragment = (_saveInstanceStateAvailable)
                ? (PostViewFragment) getSupportFragmentManager().findFragmentByTag("PostView")
                : new PostViewFragment();

        if (!PostLoader.getInstance().isInitialized()) {
            PostLoader.getInstance().initialize(this, fragment);
        }

        //add PostViewFragment dynamically
        if (!_saveInstanceStateAvailable)
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_content, fragment, "PostView")
                    .commit();

        // navigation drawer setup
        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        _drawerToggle = new ActionBarDrawerToggle(
                this,
                _drawerLayout,
                getToolbar(),
                R.string.navigation_drawer_open,
                R.string
                .navigation_drawer_close
        );

        if (_drawerLayout != null) {
            _drawerLayout.addDrawerListener(_drawerToggle);
        }
        _drawerToggle.syncState();

        _navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (_navigationView != null) {
            _navigationView.setNavigationItemSelectedListener(this);
        }
    }

    @Override
    protected boolean isFABenabled() {
        return true;
    }
}
