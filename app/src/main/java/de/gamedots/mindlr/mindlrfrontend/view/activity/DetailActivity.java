package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.PostViewFragment;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.UserPostsFragment;

/**
 * Created by Dirk on 08.01.17.
 */

public class DetailActivity extends AppCompatActivity {

    public static final String FRAGMENT_EXTRA = "fragment_extra";
    private boolean upToMainActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setBackgroundDrawable(null);

        Fragment fragment;
        if(getIntent().hasExtra(FRAGMENT_EXTRA)){
            fragment = new UserPostsFragment();
            upToMainActivity = false;
        } else {
            fragment = new PostViewFragment();
            upToMainActivity = true;
        }
        Bundle args = new Bundle();
        args.putBoolean(PostViewFragment.DETAIL_EXTRA, true);
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.detail_post_container, fragment)
                .commit();
    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        Class dest = (upToMainActivity)? MainActivity.class : ProfileActivity.class;
        Intent intent = new Intent(this, dest);
        intent.putExtra("parent", upToMainActivity? "main": "profile");
        return intent;
    }
}
