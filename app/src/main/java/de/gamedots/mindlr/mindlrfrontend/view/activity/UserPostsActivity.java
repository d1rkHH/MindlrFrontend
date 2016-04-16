package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.adapter.ViewPagerAdapter;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.FavoritePostsFragment;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.UserPostsFragment;

public class UserPostsActivity extends AppCompatActivity {

    private ViewPager _viewPager;
    private TabLayout _tapLayout;

    private int[] tabIcons = {
            R.drawable.ic_my_posts_archive_white_24dp,
            R.drawable.ic_favor_star_white_24dp,
    };

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_popup_action, popup.getMenu());
        popup.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _viewPager = (ViewPager) findViewById(R.id.viewpager);
        initViewPager(_viewPager);

        _tapLayout = (TabLayout) findViewById(R.id.tablayout);
        _tapLayout.setupWithViewPager(_viewPager);
        initTapIcons();



       /* ListView listView = (ListView) findViewById(R.id.listview);

        UserPost[] posts = {new UserPost(1, new Date(), "This is a hardcoded string.", PERSONAL),
                new UserPost(1, new Date(), "This is a hardcoded string.", SCIENCE_NATURE),
                new UserPost(1, new Date(), "This is a hardcoded string.", SPORTS),
                new UserPost(1, new Date(), "This is a hardcoded string.", ART_CULTURE),
                new UserPost(1, new Date(), "This is a hardcoded string.", QUOTES_MOTIVATION),
                new UserPost(1, new Date(), "This is a hardcoded string.", NEWS),
                new UserPost(1, new Date(), "This is a hardcoded string.", POLITICS_ECONOMICS),
                new UserPost(1, new Date(), "This is a hardcoded string.", POPCULTURE),
                new UserPost(1, new Date(), "This is a hardcoded string.", LIFESTYLE_FEMALE),
                new UserPost(1, new Date(), "This is a hardcoded string.", TECHNOLOGY),
                new UserPost(1, new Date(), "This is a hardcoded string.", LIFESTYLE_MALE),
                new UserPost(1, new Date(), "This is a hardcoded string.", TECHNOLOGY),
                new UserPost(1, new Date(), "This is a hardcoded string.", FUNNY_FASCINATING),
                new UserPost(1, new Date(), "This is a hardcoded string.", GAMING)};

        PostListAdapter adapter =
                new PostListAdapter(this, R.layout.post_list_item, Arrays.asList(posts));
        listView.setAdapter(adapter);*/
    }

    protected int getLayoutResourceId() {
        return R.layout.activity_user_posts;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_user_posts, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UserPostsFragment(), "Posts");
        adapter.addFragment(new FavoritePostsFragment(), "Favor");
        viewPager.setAdapter(adapter);
    }

    private void initTapIcons() {
        _tapLayout.getTabAt(0).setIcon(tabIcons[0]);
        _tapLayout.getTabAt(1).setIcon(tabIcons[1]);
    }
}
