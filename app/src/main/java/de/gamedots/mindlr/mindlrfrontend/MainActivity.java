package de.gamedots.mindlr.mindlrfrontend;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
//TODO: superactivity
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
        /* setup first fragment shown dynamically */
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.activity_content, new PostViewFragment());
            transaction.commit();
        }

        /* toolbar setup and app icon */
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setIcon(R.mipmap.ic_action_brain_);
        getSupportActionBar().setLogo(R.mipmap.ic_action_brain_);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.overflowmenu) {
            return true;
        }
        if (id == R.id.writePost) {
            //TODO: start post write activity
            startActivity(new Intent(this, WritePostActivity.class));
            Toast.makeText(this, "Post Write clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.share) {
            //TODO: start share activity
            Toast.makeText(this, "Share clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.profil) {
            //TODO: start profil activity
            Toast.makeText(this, " Profil clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.posts) {
            //TODO: start posts activity
            startActivity(new Intent(this, UserPostsActivity.class));
            Toast.makeText(this, " Show Posts clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.favorit) {
            //TODO: start favorits acitivity
            Toast.makeText(this, " Favorits clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.settings) {
            //TODO: start setting acitivity
            Toast.makeText(this, " Settings clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
