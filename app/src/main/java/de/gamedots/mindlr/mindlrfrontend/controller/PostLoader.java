package de.gamedots.mindlr.mindlrfrontend.controller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.gamedots.mindlr.mindlrfrontend.jobs.LoadPostsTask;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;
import de.gamedots.mindlr.mindlrfrontend.util.Utility;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.PostViewFragment;

/**
 * Created by Max Wiechmann on 26.09.15.
 */
public class PostLoader {
    public static final int MIN_SIZE_THRESHOLD = 10;

    private static final PostLoader LOADER = new PostLoader();
    private LinkedList<ViewPost> _postList = new LinkedList<>();
    private Context _context;
    private PostLoader() {
    }

    public static PostLoader getInstance() {
        return LOADER;
    }

    public ViewPost getCurrent() {
        return _postList.peek();
    }

    public void addPost(ViewPost post){
        _postList.add(post);
    }

    public void addAllPosts(List<ViewPost> toAdd){
        _postList.addAll(toAdd);
    }

    public boolean isInitialized() {
        return !_postList.isEmpty();
    }

    public void initialize(Context context, PostViewFragment fragment) {
        _context = context.getApplicationContext();

        int postsLoaded = Utility.loadUnvotedPostsOrNothing(_context);
        Toast.makeText(_context, "Loaded posts: " + postsLoaded, Toast.LENGTH_SHORT).show();
        // loaded unvoted posts size below threshold so try to load more
        if(postsLoaded < MIN_SIZE_THRESHOLD){
            Log.d(LOG.POSTS, "Load posts from the server for the first time");
            new LoadPostsTask(_context, new JSONObject()).setFragment(fragment).execute();
        }
    }

    /**
     * Sets the index to the next post in post
     * Triggers loading new posts and adds last post to the storevotes handler
     * @return TRUE if next post available, FALSE if not
     */
    public boolean next() {
        if (_postList.size() < MIN_SIZE_THRESHOLD) {
            loadNewPosts();
        }

        if (_postList.size() >= 2) {
            //Remove read post from list and add it to the store votes handler
            StoreVotesHandler.getInstance().addPost(_postList.poll());
            return true;
        } else {
            return false;
        }
    }

    private void loadNewPosts() {
        Log.d(LOG.POSTS, "Load new Posts from Server");
        new LoadPostsTask(_context, new JSONObject()).execute();
    }

    public List<ViewPost> getPostList() {
        return Collections.unmodifiableList(_postList);
    }

}