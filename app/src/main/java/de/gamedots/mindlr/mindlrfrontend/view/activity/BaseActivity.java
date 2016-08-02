package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import de.gamedots.mindlr.mindlrfrontend.R;

/**
 * Created by Dirk on 24.09.15.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // back arrow navigation
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Hide fake shadow view if sdk version (>=21) is reached
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View v = findViewById(R.id.toolbar_shadow);
            if (v != null)
                v.setVisibility(View.GONE);
        }

        if (isFABenabled()) {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            if (fab != null)
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(BaseActivity.this, WritePostActivity.class));
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    }
                });
        }
    }

    protected abstract int getLayoutResourceId();

    protected boolean isFABenabled() {
        return false;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }
}