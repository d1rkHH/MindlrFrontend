package de.gamedots.mindlr.mindlrfrontend.controller;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;
import de.gamedots.mindlr.mindlrfrontend.util.BackendTask;
import de.gamedots.mindlr.mindlrfrontend.util.Global;
import de.gamedots.mindlr.mindlrfrontend.util.ServerComUtil;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.PostViewFragment;

import static de.gamedots.mindlr.mindlrfrontend.util.DebugUtil.toast;

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
        new LoadNewPostsTask(_context, new HashMap<String, String>(),
                ServerComUtil.getMetaDataHashMap()).setFragment(fragment).execute();
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
        new LoadNewPostsTask(_context, new HashMap<String, String>(),
                ServerComUtil.getMetaDataHashMap()).execute();
    }

    private List<ViewPost> getOldestPosts() {
        return postList.subList(0, indexCurrent - 30); // only 30 posts remain
    }

    private void sendVotes() {
        HashMap<String, String> content = new HashMap<>();
        sendPosts = getOldestPosts();
        for(ViewPost post : sendPosts){
            content.put(Long.toString(post.getId()), Integer.toString(post.getVote()));
        }
        new SendVotesTask(_context, content, ServerComUtil.getMetaDataHashMap()).execute();
    }

    private void removeSendPosts(List<Long> failedPostIDs) {
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

    private class LoadNewPostsTask extends BackendTask {

        private PostViewFragment _fragment;

        public LoadNewPostsTask(Context context, HashMap<String, String> content, HashMap<String,
                String> metadata){
            super(context, content, metadata);
            _apiMethod = Global.BACKEND_METHOD_LOAD_POSTS;
        }

        public LoadNewPostsTask setFragment(PostViewFragment fragment){
            _fragment = fragment;
            return this;
        }

        @Override
        public void onSuccess(JSONObject result) {
            Iterator<?> keys = result.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                try {
                    String text = result.getString(key);
                    Log.d(LOG.JSON, "Key: " + key + " - Text: " + text);
                    try {
                        int id = Integer.parseInt(key);
                        Log.d(LOG.POSTS, "Add post to postList");
                        ViewPost post = new ViewPost(id, text);
                        postList.add(post);

                        if (_fragment != null && postList.size() == 1) {
                            _fragment.getPostView().setText(post.getContentText());
                        }
                    } catch (NumberFormatException e) {
                        Log.d(LOG.JSON, "JSON key is NaN");
                    }
                } catch (JSONException e) {
                    Log.e(LOG.JSON, Log.getStackTraceString(e));
                }
            }
        }

        @Override
        public void onFailure(JSONObject result) {
            try {
                String text = result.getString("ERROR");
                toast(_fragment.getActivity(), text);
            } catch (JSONException e) {
                Log.e(LOG.JSON, Log.getStackTraceString(e));
            }
        }
    }

    private class SendVotesTask extends BackendTask {

        public SendVotesTask(Context context, HashMap<String, String> content, HashMap<String, String> metadata){
            super(context, content, metadata);
            _apiMethod = Global.BACKEND_METHOD_SEND_VOTES;
        }

        @Override
        public void onSuccess(JSONObject result) {
            Log.d(LOG.POSTS, "successful posted.");
            removeSendPosts(new ArrayList<Long>());
        }

        @Override
        public void onFailure(JSONObject result) {
            try {
                JSONArray failedPostIDs = result.getJSONArray("FAILED");
                List<Long> postIDs = new ArrayList<>();
                for (int i = 0; i < failedPostIDs.length(); i++) {
                    postIDs.add(failedPostIDs.getLong(i));
                }
                removeSendPosts(postIDs);
            } catch (JSONException e) {
                Log.e(LOG.JSON, Log.getStackTraceString(e));
            }
        }
    }
}