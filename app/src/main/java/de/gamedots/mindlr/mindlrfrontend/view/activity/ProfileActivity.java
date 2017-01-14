package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.adapter.ViewPagerAdapter;
import de.gamedots.mindlr.mindlrfrontend.jobs.SyncUserPostTask;
import de.gamedots.mindlr.mindlrfrontend.model.ImageUploadResult;
import de.gamedots.mindlr.mindlrfrontend.util.Utility;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.PostFragment;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.UserPostsFragment;

public class ProfileActivity extends AppCompatActivity {

    private ViewPager _viewPager;
    private TabLayout _tapLayout;

    private int[] tabIcons = {
            R.drawable.ic_favorite_black_24dp,
            R.drawable.ic_my_posts_archive_white_24dp
    };

    public static final int TAP_LIKED = 0;
    public static final int TAP_USERPOST = 1;
    public static final String TAP_SELECT_KEY = "tap_select_pos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        _viewPager = (ViewPager) findViewById(R.id.viewpager);
        initViewPager(_viewPager);
        if (getIntent().hasExtra(TAP_SELECT_KEY)){
            _viewPager.setCurrentItem(getIntent().getIntExtra(TAP_SELECT_KEY,0));
        }

        _tapLayout = (TabLayout) findViewById(R.id.tablayout);
        if(_tapLayout != null) {
            _tapLayout.setupWithViewPager(_viewPager);
        }
        initTapIcons();

        syncUserPost();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImageUploadResultEvent(ImageUploadResult event) {
        Utility.handleImageResult(event, this);
    }

    private void initViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PostFragment(), "Liked");
        adapter.addFragment(new UserPostsFragment(), "Posts");
        viewPager.setAdapter(adapter);
    }

    private void initTapIcons() {
        _tapLayout.getTabAt(0).setIcon(tabIcons[0]);
        _tapLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    private void syncUserPost(){
        if (Utility.isNetworkAvailable(this)) {
            new SyncUserPostTask(this, new JSONObject()).execute();
        }
    }
}
