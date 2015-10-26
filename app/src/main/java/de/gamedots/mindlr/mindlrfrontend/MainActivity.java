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
import java.util.ArrayList;

import de.gamedots.mindlr.mindlrfrontend.models.ViewPost;

import static de.gamedots.mindlr.mindlrfrontend.Global.*;

public class MainActivity extends ToolbarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PostViewFragment fragment = new PostViewFragment();

        //Load the first bunch of posts in the list of posts
        if (!PostLoader.getInstance().isInitialized()) {
            PostLoader.getInstance().initialize(fragment);
        }

        if (savedInstanceState == null) {
        /* setup first fragment shown dynamically */
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.activity_content, fragment);
            transaction.commit();
        }


    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
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
