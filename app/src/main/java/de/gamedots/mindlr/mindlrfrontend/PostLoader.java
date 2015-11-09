package de.gamedots.mindlr.mindlrfrontend;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.FormatFlagsConversionMismatchException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.gamedots.mindlr.mindlrfrontend.models.ViewPost;

import static de.gamedots.mindlr.mindlrfrontend.Global.BACKEND_METHOD_KEY;
import static de.gamedots.mindlr.mindlrfrontend.Global.BACKEND_METHOD_LOAD_POSTS;
import static de.gamedots.mindlr.mindlrfrontend.Global.BACKEND_METHOD_SEND_VOTES;
import static de.gamedots.mindlr.mindlrfrontend.Global.SERVER_URL;
import static de.gamedots.mindlr.mindlrfrontend.Global.METHOD_POST;

/**
 * Created by max on 26.09.15.
 */
public class PostLoader {

    private static final PostLoader LOADER = new PostLoader();
    private int indexCurrent = 0;
    private List<ViewPost> postList = new ArrayList<>();
    private List<ViewPost> sendPosts = new ArrayList<>();

    private PostLoader(){
    }

    public static PostLoader getInstance(){
        return LOADER;
    }

    public ViewPost getCurrent(){
        return postList.get(indexCurrent);
    }

    public boolean isInitialized(){
        return postList.size() > 0;
    }

    public void initialize(PostViewFragment fragment){
        Log.d(LOG.POSTS, "Load posts from the server for the first time");
        new LoadNewPostsTask(fragment).execute();
    }

    /**
     * Sets the index to the next post in post
     * @return TRUE if next post available, FALSE if not
     */
    public boolean next(){
        //If only 15 posts are in the pipeline or less than 15 in the whole list, load new posts from the DB
        if(postList.size() - indexCurrent == 15 || postList.size() < 15){
            Log.d(LOG.POSTS, "Condition for loading new posts true (Only 15 Posts remaining)");
            loadNewPosts();
        }

        if(indexCurrent >= 60 && indexCurrent % 10 == 0){ //When indexCurrent is 60, 70, 80, ... (
            //Normally, this way the indexCurrent should never be higher than 60, but if the post-
            //sending failes somehow, it will still try to send it after another 10 posts
            sendVotes();
        }

        //If there is at least 1 post remaining, set current post the the next one
        if(indexCurrent < postList.size() - 1) {
            indexCurrent++;
            Log.d(LOG.POSTS, "Next Post: " + indexCurrent);
            return true;
        } else {
            return false;
        }

    }

    /**
     *
     * @return TRUE if previous post available, FALSE if not
     */
    public boolean previous(){
        Log.d(LOG.POSTS, "Previous Post: " + indexCurrent);
        if(indexCurrent > 0) {
            indexCurrent--;
            return true;
        } else{
            return false;
        }
    }

    public void loadNewPosts(){
        Log.d(LOG.POSTS, "Load new Posts from Server");
        new LoadNewPostsTask().execute();
    }

    public List<ViewPost> getOldestPosts(){
        return postList.subList(0, indexCurrent - 30); // only 30 posts remain
    }

    public void sendVotes(){
        new SendVotesTask().execute();
    }

    public void removeSendPosts(){
        removeSendPosts(new ArrayList<Long>());
    }

    public void removeSendPosts(List<Long> failedPostIDs){
        Log.d(LOG.POSTS, "About to remove send posts from postList");
        ArrayList<ViewPost> sendPostsCopy = new ArrayList<>(sendPosts);
        ArrayList<ViewPost> postListCopy = new ArrayList<>(postList);
        int numberOfRemovals = 0;
        for(Long l : failedPostIDs){
            for(int i = 0; i < sendPosts.size(); i++){
                if(sendPosts.get(i).getId() == l){
                    sendPostsCopy.remove(sendPostsCopy.get(i));
                }
            }
        }
        Log.d(LOG.POSTS, "Number of removed Posts: " + sendPostsCopy.size());
        for(ViewPost post : sendPosts){
            int limit = indexCurrent;
            for(int i = 0; i < limit; i++){
                if(postList.get(i) == post){
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


    private class LoadNewPostsTask extends AsyncTask<Void, Void, JSONObject> {

        PostViewFragment fragment;
        Context context;

        public LoadNewPostsTask(){

        }

        public LoadNewPostsTask(PostViewFragment fragment) {
            this.fragment = fragment;
        }

        public LoadNewPostsTask(PostViewFragment fragment, Context context) {
            this.context = context;
            this.fragment = fragment;
        }

        protected void onPreExecute(){
            //Keine Funktionalität
        }

        protected JSONObject doInBackground(Void... params){
            HashMap<String, String> parameter = ServerCommunicationUtilities.newDefaultParameterHashMap();

            parameter.put(BACKEND_METHOD_KEY,BACKEND_METHOD_LOAD_POSTS);
            parameter.put("USER_ID", "TRIAL");

            Log.d(LOG.JSON, "About to create JSONParser");
            JSONParser parser = new JSONParser();
            Log.d(LOG.CONNECTION, "About to make HTTPRequest");
            return parser.makeHttpRequest(SERVER_URL, METHOD_POST, parameter);
        }

        protected void onPostExecute(JSONObject jsonPosts){
            if(jsonPosts != null){
                try {
                    boolean success = jsonPosts.getBoolean("SUCCESS");
                    jsonPosts.remove("SUCCESS");
                    if(success){
                        Iterator<?> keys = jsonPosts.keys();
                        while(keys.hasNext()) {
                            String key = (String) keys.next();
                            String text = jsonPosts.getString(key);
                            Log.d(LOG.JSON, "Key: " + key +  " - Text: " + text);
                            try {
                                int id = Integer.parseInt(key);
                                ViewPost post = new ViewPost(id, text);
                                Log.d(LOG.POSTS, "Add post to postList");
                                postList.add(post);
                                if (this.fragment != null && postList.size() == 1) {
                                    fragment.getPostView().setText(post.getContentText());
                                }
                            } catch (NumberFormatException e){
                                Log.d(LOG.JSON, "JSON key is NaN");
                            }
                        }
                    } else {
                        String text = jsonPosts.getString("ERROR");
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.d(LOG.JSON, "Error parsing data into objects");
                    e.printStackTrace();
                }
            } else{
                Log.d(LOG.JSON, "JSONObject was null");
            }
        }
    }


    private class SendVotesTask extends AsyncTask<Void, Void, JSONObject>{

        protected void onPreExecute(){
            //Keine Funktionalität
        }

        protected JSONObject doInBackground(Void... params){
            //Generate new HashMap with default values such as SDK etc.
            HashMap<String, String> parameter = ServerCommunicationUtilities.newDefaultParameterHashMap();

            parameter.put(BACKEND_METHOD_KEY,BACKEND_METHOD_SEND_VOTES);
            parameter.put("USER_ID", "1"); //should only work with real user, not TRIAL
            sendPosts = getOldestPosts();
            for(ViewPost post : sendPosts){
                parameter.put(Long.toString(post.getId()), Integer.toString(post.getVote()));
            }
            Log.d(LOG.JSON, "About to create JSONParser");
            JSONParser parser = new JSONParser();
            Log.d(LOG.CONNECTION, "About to make HTTPRequest");
            return parser.makeHttpRequest(SERVER_URL, METHOD_POST, parameter);
        }

        protected void onPostExecute(JSONObject result){
            if(result != null){
                try {
                    boolean success = result.getBoolean("SUCCESS");
                    if(success){
                        Log.d(LOG.POSTS, "Successfull posted.");
                        removeSendPosts();
                    } else {
                        String text = result.getString("ERROR");
                        Log.d(LOG.POSTS, text);
                        JSONArray failedPostIDs = result.getJSONArray("FAILED");
                        List<Long> postIDs = new ArrayList<>();
                        for(int i = 0; i < failedPostIDs.length(); i++){
                            postIDs.add(failedPostIDs.getLong(i));
                        }
                        removeSendPosts(postIDs);
                    }

                } catch (JSONException e) {
                    Log.d(LOG.JSON, "Error parsing data into objects");
                    e.printStackTrace();
                }
            } else{
                Log.d(LOG.JSON, "JSONObject was null");
            }
        }
    }

}
