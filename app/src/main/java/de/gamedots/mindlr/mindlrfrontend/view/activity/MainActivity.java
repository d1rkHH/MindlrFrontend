package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.adapter.FlingAdapter;
import de.gamedots.mindlr.mindlrfrontend.adapter.ViewPostCardAdapter;
import de.gamedots.mindlr.mindlrfrontend.controller.PostLoader;
import de.gamedots.mindlr.mindlrfrontend.data.DatabaseIntentService;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract;
import de.gamedots.mindlr.mindlrfrontend.helper.IntentHelper;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.ImageUploadResult;
import de.gamedots.mindlr.mindlrfrontend.model.PostLoadedEvent;
import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;
import de.gamedots.mindlr.mindlrfrontend.util.DebugUtil;
import de.gamedots.mindlr.mindlrfrontend.util.Utility;
import in.arjsna.swipecardlib.SwipeCardView;

import static de.gamedots.mindlr.mindlrfrontend.R.id.fab;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle _drawerToggle;
    private DrawerLayout _drawerLayout;
    private NavigationView _navigationView;

    /* Swipe control views for card stack */
    private ViewPostCardAdapter adapter;
    private SwipeCardView swipeCardView;
    private Toolbar _toolbar;
    private FloatingActionButton _fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!PostLoader.getInstance().isInitialized()){
            PostLoader.getInstance().initialize();
        }

        // initialize all UI components
        setupToolbar();
        setupFab();
        setupNavDrawer();
        setupCardAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        adapter.clear();
        adapter.addItems(PostLoader.getInstance().getPostList());
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImageUploadResultEvent(ImageUploadResult event) {
        Log.v(LOG.AUTH, "received upload event from BUS");
        Utility.handleImageResult(event, this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostLoadedEvent(PostLoadedEvent event) {
        Log.v(LOG.AUTH, "New loaded post received");
        if (event.success){
            if (adapter != null) {
                adapter.clear();
                adapter.addItems(PostLoader.getInstance().getPostList());
            }
            Intent intent = new Intent(this, DatabaseIntentService.class);
            intent.setAction(DatabaseIntentService.INSERT_POST_ACTION);
            startService(intent);
        }
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
                IntentHelper.showShareIntent(PostLoader.getInstance().getCurrent().getContentText(), this);
                break;
            case R.id.action_report:
                // TODO: report dialog with report options store in user post entry
                DebugUtil.toast(this, "Reported");
                break;
        }
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

    // region UI setup
    private void setupCardAdapter(){
        // TODO: check empty/ network error
        LinkedList<ViewPost> posts = new LinkedList<>();
        //posts.addAll(PostLoader.getInstance().getPostList());

        adapter = new ViewPostCardAdapter(this, posts);

        swipeCardView = (SwipeCardView) findViewById(R.id.viewposts_swipe_container);
        swipeCardView.setAdapter(adapter);
        swipeCardView.setFlingListener(new FlingAdapter() {
            @Override
            public void onCardExitLeft(Object dataObject) {
                Toast.makeText(MainActivity.this, "LEFT", Toast.LENGTH_SHORT).show();
                PostLoader.getInstance().getCurrent().ratePositive();
                Utility.updatePostVoteType(
                        MainActivity.this,
                        PostLoader.getInstance().getCurrent().getServerId(),
                        MindlrContract.UserPostEntry.VOTE_LIKED
                );
                PostLoader.getInstance().next();
                //adapter.popNotify();
            }

            @Override
            public void onCardExitRight(Object dataObject) {
                Toast.makeText(MainActivity.this, "RIGHT", Toast.LENGTH_SHORT).show();
                PostLoader.getInstance().getCurrent().rateNegative();
                Utility.updatePostVoteType(
                        MainActivity.this,
                        PostLoader.getInstance().getCurrent().getServerId(),
                        MindlrContract.UserPostEntry.VOTE_DISLIKED);
                PostLoader.getInstance().next();
                //adapter.popNotify();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }
        });

        swipeCardView.setOnItemClickListener(
                new SwipeCardView.OnItemClickListener() {
                    @Override
                    public void onItemClicked(int itemPosition, Object dataObject) {
                        ViewPost card = (ViewPost) dataObject;
                        startActivity(new Intent(MainActivity.this, DetailActivity.class)
                                .putExtra(DetailActivity.POST_EXTRA, card));
                    }
                });
    }

    private void setupToolbar() {
        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(_toolbar);
        // back arrow navigation
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupFab(){
        _fab = (FloatingActionButton) findViewById(fab);
        if (_fab != null)
            _fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, WritePostActivity.class));
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }
            });
    }

    private void setupNavDrawer(){
        // navigation drawer setup
        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        _drawerToggle = new ActionBarDrawerToggle(
                this,
                _drawerLayout,
                _toolbar,
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
    // endregion
}
