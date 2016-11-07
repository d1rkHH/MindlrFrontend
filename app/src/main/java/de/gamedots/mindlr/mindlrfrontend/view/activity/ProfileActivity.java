package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.adapter.ViewPagerAdapter;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.PostFragment;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.UserPostsFragment;

public class ProfileActivity extends AppCompatActivity {

    private ViewPager _viewPager;
    private TabLayout _tapLayout;

    private int[] tabIcons = {
            R.drawable.ic_favorite_black_24dp,
            R.drawable.ic_favor_star_white_24dp,
            R.drawable.ic_my_posts_archive_white_24dp
    };

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

        _tapLayout = (TabLayout) findViewById(R.id.tablayout);
        if(_tapLayout != null) {
            _tapLayout.setupWithViewPager(_viewPager);
        }
        initTapIcons();
    }

    private void initViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PostFragment(), "Liked");
        adapter.addFragment(new PostFragment(), "Favor");
        adapter.addFragment(new UserPostsFragment(), "Posts");
        viewPager.setAdapter(adapter);
    }

    private void initTapIcons() {
        _tapLayout.getTabAt(0).setIcon(tabIcons[0]);
        _tapLayout.getTabAt(1).setIcon(tabIcons[1]);
        _tapLayout.getTabAt(2).setIcon(tabIcons[2]);
    }
}
