package de.gamedots.mindlr.mindlrfrontend.controller;

import android.util.Log;

import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.gamedots.mindlr.mindlrfrontend.MindlrApplication;
import de.gamedots.mindlr.mindlrfrontend.jobs.LoadPostsTask;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;
import de.gamedots.mindlr.mindlrfrontend.util.Utility;

/**
 * Created by Max Wiechmann on 26.09.15.
 */
public class PostLoader {
    public static final int MIN_SIZE_THRESHOLD = 10;

    private static final PostLoader LOADER = new PostLoader();
    private LinkedList<ViewPost> _postList = new LinkedList<>();
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

    public void initialize() {
        int postsLoaded = Utility.loadUnvotedPostsOrNothing(MindlrApplication.getInstance());
        // loaded unvoted posts size below threshold so try to load more
        if(postsLoaded < MIN_SIZE_THRESHOLD){
            Log.d(LOG.AUTH, "Load posts from the server for the first time");
            new LoadPostsTask(MindlrApplication.getInstance(), new JSONObject())
                    .execute();
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
            //Remove read post from list and increase send threshold in storevoteshandler
            _postList.poll();
            StoreVotesHandler.getInstance().increaseSendThreshold();
            return true;
        } else {
            return false;
        }
    }

    private void loadNewPosts() {
        Log.d(LOG.AUTH, "Load new Posts from Server");
        new LoadPostsTask(MindlrApplication.getInstance(), new JSONObject()).execute();
    }

    public List<ViewPost> getPostList() {
        return Collections.unmodifiableList(_postList);
    }

}