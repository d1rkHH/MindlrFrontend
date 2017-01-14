package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.model.ImageUploadResult;
import de.gamedots.mindlr.mindlrfrontend.util.Utility;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.PostViewFragment;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.UserPostsFragment;

/**
 * Created by Dirk on 08.01.17.
 */

public class DetailActivity extends AppCompatActivity {

    public static final String FRAGMENT_EXTRA = "fragment_extra";
    public static final String LIKED_EXTRA = "liked_extra";
    private boolean upToMainActivity;
    private int tapSelectionOnNavigation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Fragment fragment;
        if(getIntent().hasExtra(FRAGMENT_EXTRA)){
            fragment = new UserPostsFragment();
            upToMainActivity = false;
            tapSelectionOnNavigation = 2;
        } else {
            fragment = new PostViewFragment();
            upToMainActivity = !getIntent().hasExtra(LIKED_EXTRA);
            if (!upToMainActivity){
                tapSelectionOnNavigation = 0;
            }
        }
        Bundle args = new Bundle();
        args.putBoolean(PostViewFragment.DETAIL_EXTRA, true);
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.detail_post_container, fragment)
                .commit();
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

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        Class dest = (upToMainActivity)? MainActivity.class : ProfileActivity.class;
        Intent intent = new Intent(this, dest);
        intent.putExtra("parent", tapSelectionOnNavigation);
        return intent;
    }
}
