package de.gamedots.mindlr.mindlrfrontend.controller;

import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import de.gamedots.mindlr.mindlrfrontend.jobs.LoadPostsTask;
import de.gamedots.mindlr.mindlrfrontend.jobs.StoreVotesTask;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.PostViewFragment;

/**
 * Created by max on 26.09.15.
 */
public class PostLoader {

    private static final PostLoader LOADER = new PostLoader();
    private int indexCurrent = 0;
    private List<ViewPost> postList = new ArrayList<>();
    private List<ViewPost> sendPosts = new ArrayList<>();
    private Context _context;
    private PostLoader() {
    }

    public static PostLoader getInstance() {
        return LOADER;
    }

    public ViewPost getCurrent() {
        return postList.get(indexCurrent);
    }

    public boolean isInitialized() {
        return postList.size() > 0;
    }

    public void initialize(Context context, PostViewFragment fragment) {
        _context = context;
        Log.d(LOG.POSTS, "Load posts from the server for the first time");
        new LoadPostsTask(_context, new JSONObject(), postList).setFragment(fragment).execute();
    }

    /**
     * Sets the index to the next post in post
     *
     * @return TRUE if next post available, FALSE if not
     */
    public boolean next() {
        //if only 15 posts are in the pipeline or less than 15 in the whole list,
        //load new posts from the DB
        if (postList.size() - indexCurrent == 15 || postList.size() < 15) {
            Log.d(LOG.POSTS, "Condition for loading new posts true (Only 15 Posts remaining)");
            loadNewPosts();
        }

        if (indexCurrent >= 60 && indexCurrent % 10 == 0) {
            //normally, this way the indexCurrent should never be higher than 60, but if the post-
            //sending fails somehow, it will still try to send it after another 10 posts
            sendVotes();
        }

        //if there is at least 1 post remaining, set current post the the next one
        if (indexCurrent < postList.size() - 1) {
            indexCurrent++;
            Log.d(LOG.POSTS, "Next Post: " + indexCurrent);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return TRUE if previous post available, FALSE if not
     */
    public boolean previous() {
        Log.d(LOG.POSTS, "Previous Post: " + indexCurrent);
        if (indexCurrent > 0) {
            indexCurrent--;
            return true;
        } else {
            return false;
        }
    }

    private void loadNewPosts() {
        Log.d(LOG.POSTS, "Load new Posts from Server");
        new LoadPostsTask(_context, new JSONObject(), postList).execute();
    }

    private List<ViewPost> getOldestPosts() {
        return postList.subList(0, indexCurrent - 30); // only 30 posts remain
    }

    private void sendVotes() {
        JSONObject content = new JSONObject();
        sendPosts = getOldestPosts();
        try {
            for (ViewPost post : sendPosts) {
                content.put(Long.toString(post.getId()), Integer.toString(post.getVote()));
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        new StoreVotesTask(_context, content, this).execute();
    }

    public void removeSendPosts(List<Long> failedPostIDs) {
        Log.d(LOG.POSTS, "About to remove send posts from postList");
        ArrayList<ViewPost> sendPostsCopy = new ArrayList<>(sendPosts);
        ArrayList<ViewPost> postListCopy = new ArrayList<>(postList);

        int numberOfRemovals = 0;
        for (Long l : failedPostIDs) {
            for (int i = 0; i < sendPosts.size(); i++) {
                if (sendPosts.get(i).getId() == l) {
                    sendPostsCopy.remove(sendPostsCopy.get(i));
                }
            }
        }
        Log.d(LOG.POSTS, "Number of removed Posts: " + sendPostsCopy.size());
        for (ViewPost post : sendPosts) {
            int limit = indexCurrent;
            for (int i = 0; i < limit; i++) {
                if (postList.get(i) == post) {
                    postListCopy.remove(post);
                    numberOfRemovals++;
                }
            }
        }
        postList = postListCopy;
        Log.d(LOG.POSTS, "Number of remaining Posts: " + postList.size());
        indexCurrent -= numberOfRemovals;
        sendPosts = new ArrayList<>();
    }

    public List<ViewPost> getPostList() {
        return postList;
    }

}