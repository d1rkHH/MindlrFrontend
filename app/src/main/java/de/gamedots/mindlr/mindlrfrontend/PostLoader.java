package de.gamedots.mindlr.mindlrfrontend;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.gamedots.mindlr.mindlrfrontend.models.ViewPost;

import static de.gamedots.mindlr.mindlrfrontend.Global.BACKEND_METHOD_KEY;
import static de.gamedots.mindlr.mindlrfrontend.Global.BACKEND_METHOD_LOAD_POSTS;
import static de.gamedots.mindlr.mindlrfrontend.Global.LOAD_POSTS_COUNT;
import static de.gamedots.mindlr.mindlrfrontend.Global.SERVER_URL;
import static de.gamedots.mindlr.mindlrfrontend.Global.METHOD_POST;

/**
 * Created by max on 26.09.15.
 */
public class PostLoader {

    private static final PostLoader LOADER = new PostLoader();
    private int indexCurrent = 0;
    private ArrayList<ViewPost> postList = new ArrayList<>();

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
        new LoadNewPostsTask(LOAD_POSTS_COUNT, fragment).execute();
    }

    /**
     * Sets the index to the next post in post
     * @return TRUE if next post available, FALSE if not
     */
    public boolean next(){
        Log.d(LOG.POSTS, "Next Post");
        //If only 10 posts are in the pipeline or less than 10 in the whole list, load new posts from the DB
        if(postList.size() - 1 - indexCurrent == 10 || postList.size() < 10){
            Log.d(LOG.POSTS, "Condition for loading new posts true (Only 10 Posts remaining)");
            loadNewPosts();
        }

        //If there is at least 1 post remaining, set current post the the next one
        if(indexCurrent < postList.size() - 1) {
            indexCurrent++;
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
        Log.d(LOG.POSTS, "Previous Post");
        if(indexCurrent > 0) {
            indexCurrent--;
            return true;
        } else{
            return false;
        }
    }

    public void loadNewPosts(){
        Log.d(LOG.POSTS, "Load new Posts from Server");
        new LoadNewPostsTask(LOAD_POSTS_COUNT).execute();
    }

    private class LoadNewPostsTask extends AsyncTask<Void, Void, JSONObject> {

        private int numberOfPosts;
        PostViewFragment fragment;

        public LoadNewPostsTask(int numberOfPosts) {
            this.numberOfPosts = numberOfPosts;
        }

        public LoadNewPostsTask(int numberOfPosts, PostViewFragment fragment) {
            this.numberOfPosts = numberOfPosts;
            this.fragment = fragment;
        }

        protected void onPreExecute(){
            //Keine Funktionalität
        }

        protected JSONObject doInBackground(Void... params){
            HashMap<String, String> parameter = new HashMap<>();
            //TODO: Different URLS for different methods
            parameter.put("BRAND", android.os.Build.BRAND);
            parameter.put("MODEL", android.os.Build.MODEL);
            parameter.put("PRODUCT", Build.PRODUCT);
            parameter.put("SDK", "" + Build.VERSION.SDK_INT);
            parameter.put("TIME", "" + Build.TIME);
            parameter.put(BACKEND_METHOD_KEY,BACKEND_METHOD_LOAD_POSTS);
            parameter.put("NUMBER_OF_POSTS", Integer.toString(numberOfPosts));
            Log.d(LOG.JSON, "About to create JSONParser");
            JSONParser parser = new JSONParser();
            Log.d(LOG.CONNECTION, "About to make HTTPRequest");
            return parser.makeHttpRequest(SERVER_URL, METHOD_POST, parameter);
        }

        protected void onPostExecute(JSONObject jsonPosts){
            if(jsonPosts != null){
                try {
                    Iterator<?> keys = jsonPosts.keys();
                    while(keys.hasNext()) {
                        String key = (String) keys.next();
                        String text = jsonPosts.getString(key);
                        Log.d(LOG.JSON, "Key: " + key +  " - Text: " + text);
                        int id = Integer.parseInt(key);
                        ViewPost post = new ViewPost(id,text);
                        Log.d(LOG.POSTS, "Add post to postList");
                        postList.add(post);
                        if(this.fragment != null && postList.size() == 1){
                            fragment.getPostView().setText(post.getContentText());
                        }
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
