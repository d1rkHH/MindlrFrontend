package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import de.gamedots.mindlr.mindlrfrontend.R;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by Dirk on 24.09.15.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());



        /* toolbar setup and app icon */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setIcon(R.mipmap.ic_action_brain_);
        //getSupportActionBar().setLogo(R.mipmap.ic_action_brain_);
    }

    protected abstract int getLayoutResourceId();

    public Toolbar getToolbar(){
        return toolbar;
    }

    public void hideToolbar(){
        toolbar.setVisibility(INVISIBLE);
    }

    public void showToolbar(){
        toolbar.setVisibility(VISIBLE);
    }

}