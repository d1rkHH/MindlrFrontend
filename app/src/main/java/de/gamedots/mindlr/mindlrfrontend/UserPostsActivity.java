package de.gamedots.mindlr.mindlrfrontend;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UserPostsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);

        ListView listView = (ListView) findViewById(R.id.listview);

        Post[] posts = {new Post("Lipsum orodi madura", "Sport", "15.1.15"),
                new Post("Lipsum orodi madura", "Sport", "15.1.15"),
                new Post("Lipsum orodi madura", "Sport", "15.1.15"),
                new Post("Lipsum orodi madura", "Sport", "15.1.15"),
                new Post("Lipsum orodi madura", "Sport", "15.1.15"),
                new Post("Lipsum orodi madura", "Sport", "15.1.15"),
                new Post("Lipsum orodi madura", "Sport", "15.1.15"),
                new Post("Lipsum orodi madura", "Sport", "15.1.15"),
                new Post("Lipsum orodi madura", "Sport", "15.1.15"),
                new Post("Lipsum orodi madura", "Sport", "15.1.15"),
                new Post("Lipsum orodi madura", "Sport", "15.1.15"),
                new Post("Lipsum orodi madura", "Sport", "15.1.15", 880000, 5000000)};

        PostListAdapter adapter =
                new PostListAdapter(this, R.layout.post_list_item, Arrays.asList(posts));
        listView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_posts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
