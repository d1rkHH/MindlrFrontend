package de.gamedots.mindlr.mindlrfrontend;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.Arrays;
import java.util.Date;

import de.gamedots.mindlr.mindlrfrontend.models.UserPost;
import static de.gamedots.mindlr.mindlrfrontend.Global.Categories.*;

public class UserPostsActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView listView = (ListView) findViewById(R.id.listview);

        UserPost[] posts = {new UserPost(1, new Date(), "This is a hardcoded string.", PERSONAL),
                new UserPost(1, new Date(), "This is a hardcoded string.", SCIENCE_NATURE),
                new UserPost(1, new Date(), "This is a hardcoded string.", SPORTS),
                new UserPost(1, new Date(), "This is a hardcoded string.", ART_CULTURE),
                new UserPost(1, new Date(), "This is a hardcoded string.", QUOTES_MOTIVATION),
                new UserPost(1, new Date(), "This is a hardcoded string.", NEWS),
                new UserPost(1, new Date(), "This is a hardcoded string.", POLITICS_ECONOMICS),
                new UserPost(1, new Date(), "This is a hardcoded string.", POPCULTURE),
                new UserPost(1, new Date(), "This is a hardcoded string.", LIFESTYLE_FEMALE),
                new UserPost(1, new Date(), "This is a hardcoded string.", TECHNOLOGY),
                new UserPost(1, new Date(), "This is a hardcoded string.", LIFESTYLE_MALE),
                new UserPost(1, new Date(), "This is a hardcoded string.", TECHNOLOGY),
                new UserPost(1, new Date(), "This is a hardcoded string.", FUNNY_FASCINATING),
                new UserPost(1, new Date(), "This is a hardcoded string.", GAMING)};

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
