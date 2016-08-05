package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;

public class ProfileActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout _swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        if (_swipeRefreshLayout != null) _swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_profile_activity;
    }

    @Override
    public void onRefresh() {
        Log.d(LOG.AUTH, "onRefresh: Refreshing");
        _swipeRefreshLayout.setRefreshing(false);
    }
}
