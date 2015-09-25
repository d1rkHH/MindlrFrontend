package de.gamedots.mindlr.mindlrfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by Dirk on 24.09.15.
 */
public abstract class ToolbarActivity extends AppCompatActivity {
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());



        /* toolbar setup and app icon */
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setIcon(R.mipmap.ic_action_brain_);
        getSupportActionBar().setLogo(R.mipmap.ic_action_brain_);
    }

    protected abstract int getLayoutResourceId();

    public Toolbar getToolbar(){
        return toolbar;
    }

}