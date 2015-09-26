package de.gamedots.mindlr.mindlrfrontend;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import de.gamedots.mindlr.mindlrfrontend.models.ViewPost;

import static de.gamedots.mindlr.mindlrfrontend.Global.BACKEND_METHOD_KEY;
import static de.gamedots.mindlr.mindlrfrontend.Global.BACKEND_METHOD_LOAD_POSTS;
import static de.gamedots.mindlr.mindlrfrontend.Global.LOAD_POSTS_COUNT;
import static de.gamedots.mindlr.mindlrfrontend.Global.LOAD_POSTS_URL;
import static de.gamedots.mindlr.mindlrfrontend.Global.METHOD_POST;

/**
 * Created by max on 26.09.15.
 */
public class PostLoader {

    private int indexCurrent = 0;
    private ArrayList<ViewPost> postList = new ArrayList<>();

    public PostLoader(){
        ViewPost[] posts = {new ViewPost("First"),
                new ViewPost("Second"),
                new ViewPost("Third"),
                new ViewPost("Fourth"),
                new ViewPost("Fifth"),
                new ViewPost("Sixth"),
                new ViewPost("Seventh"),
                new ViewPost("Number 8"),
                new ViewPost("Number 9"),
                new ViewPost("The 10. Post"),
                new ViewPost("The 11. Posts"),
                new ViewPost("The Last / 12. Post")};
        postList.addAll(Arrays.asList(posts));
    }

    public ViewPost getCurrent(){
        return postList.get(indexCurrent);
    }

    public void initialize(){
        //TODO: AysncTask to load posts from DB
    }

    /**
     * Sets the index to the next post in post
     * @return TRUE if next post available, FALSE if not
     */
    public boolean next(){

        //If only 10 posts are in the pipeline, load new posts from the DB
        if(postList.size() - 1 - indexCurrent == 10){
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
        if(indexCurrent > 0) {
            indexCurrent--;
            return true;
        } else{
            return false;
        }
    }

    public void loadNewPosts(){
      //  new LoadNewPostsTask(LOAD_POSTS_COUNT).execute();
    }

    private class LoadNewPostsTask extends AsyncTask<Void, Void, JSONObject> {

        private int numberOfPosts;

        public LoadNewPostsTask(int numberOfPosts) {
            this.numberOfPosts = numberOfPosts;
        }

        protected void onPreExecute(){
            //Keine Funktionalität
        }

        protected JSONObject doInBackground(Void... params){
            HashMap<String, String> parameter = new HashMap<>();
            //TODO: Different URLS for different methods
            parameter.put(BACKEND_METHOD_KEY,BACKEND_METHOD_LOAD_POSTS);
            parameter.put("NUMBER_OF_POSTS", Integer.toString(numberOfPosts));
            JSONParser parser = new JSONParser();
            return parser.makeHttpRequest(LOAD_POSTS_URL, METHOD_POST, parameter);
        }

        protected void onPostExecute(JSONObject jsonPosts){
            try {
                Iterator<?> keys = jsonPosts.keys();
                Log.e("Max", "keys: "+keys.toString());
                while(keys.hasNext()) {
                    String key = (String) keys.next();
                    Log.e("Max", "key: " + key);
                    JSONObject jsonPost = jsonPosts.getJSONObject(key);
                    Log.e("Max", "jObj: " + jsonPost.toString());

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
