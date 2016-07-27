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
    private boolean sending = false;
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

    public void setSending(boolean sending){
        this.sending = sending;
    }

    /**
     * Sets the index to the next post in post
     *
     * @return TRUE if next post available, FALSE if not
     */
    public boolean next() {
        if (postList.size() - indexCurrent < 10) {
            loadNewPosts();
        }

        if (!sending && indexCurrent >= 20) {
            sendVotes();
        }

        //if there is at least 1 post remaining, set current post the the next one
        if (indexCurrent < postList.size() - 1) {
            indexCurrent++;
            return true;
        } else {
            return false;
        }
    }

    private void loadNewPosts() {
        Log.d(LOG.POSTS, "Load new Posts from Server");
        new LoadPostsTask(_context, new JSONObject(), postList).execute();
    }

    private void sendVotes() {
        JSONObject content = new JSONObject();
        sendPosts = postList.subList(0, indexCurrent);
        for (ViewPost post : sendPosts) {
            try {
                content.put(Long.toString(post.getId()), Integer.toString(post.getVote()));
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        this.sending = true;
        Log.d(LOG.POSTS, "Sending votes to server");
        new StoreVotesTask(_context, content, this).execute();
    }

    public void removeSendPosts(List<Long> failedPostIDs) {
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