package de.gamedots.mindlr.mindlrfrontend.controller;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.gamedots.mindlr.mindlrfrontend.MindlrApplication;
import de.gamedots.mindlr.mindlrfrontend.jobs.StoreVotesTask;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;

/**
 * Created by Max Wiechmann on 18.08.16.
 */
public class StoreVotesHandler {

    private static final StoreVotesHandler HANDLER = new StoreVotesHandler();
    private StoreVotesHandler(){}
    public static StoreVotesHandler getInstance(){  return HANDLER;  }

    private final Set<ViewPost> _posts = new HashSet<>();
    private final Set<ViewPost> _postsInSending = new HashSet<>();

    public void addPost(ViewPost post){
        _posts.add(post);
        triggerSending();
    }

    public void removeAll(){
        _postsInSending.clear();
    }

    public void removeExcept(Set<ViewPost> failedPosts){
        _posts.addAll(failedPosts);
        _postsInSending.clear();
    }

    public Set<ViewPost> getInSending(){
        return Collections.unmodifiableSet(_postsInSending);
    }

    public void sendingFailed(){
        _posts.addAll(_postsInSending);
        _postsInSending.clear();
    }

    private void triggerSending(){
        if(_posts.size() > 10){
            _postsInSending.addAll(_posts);
            _posts.clear();

            JSONObject content = new JSONObject();
            for (ViewPost post : _postsInSending) {
                try {
                    content.put(Long.toString(post.getId()), Integer.toString(post.getVote()));
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            Log.d(LOG.POSTS, "Sending votes to server");
            new StoreVotesTask(MindlrApplication.getInstance(), content).execute();
        }
    }
}
