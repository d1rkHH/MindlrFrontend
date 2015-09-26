package de.gamedots.mindlr.mindlrfrontend;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.Arrays;
import java.util.Date;

import de.gamedots.mindlr.mindlrfrontend.models.UserPost;

public class UserPostsActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView listView = (ListView) findViewById(R.id.listview);

        UserPost[] posts = {new UserPost(1, new Date(), "This is a hardcoded string.", 1),
                new UserPost(1, new Date(), "This is a hardcoded string.", 1),
                new UserPost(1, new Date(), "This is a hardcoded string.", 2),
                new UserPost(1, new Date(), "This is a hardcoded string.", 4),
                new UserPost(1, new Date(), "This is a hardcoded string.", 6),
                new UserPost(1, new Date(), "This is a hardcoded string.", 1),
                new UserPost(1, new Date(), "This is a hardcoded string.", 1),
                new UserPost(1, new Date(), "This is a hardcoded string.", 2),
                new UserPost(1, new Date(), "This is a hardcoded string.", 2),
                new UserPost(1, new Date(), "This is a hardcoded string.", 1),
                new UserPost(1, new Date(), "This is a hardcoded string.", 7),
                new UserPost(1, new Date(), "This is a hardcoded string.", 7),
                new UserPost(1, new Date(), "This is a hardcoded string.", 5),
                new UserPost(1, new Date(), "This is a hardcoded string.", 1)};

        PostListAdapter adapter =
                new PostListAdapter(this, R.layout.post_list_item, Arrays.asList(posts));
        listView.setAdapter(adapter);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_user_posts;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_posts, menu);
        return true;
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
}
