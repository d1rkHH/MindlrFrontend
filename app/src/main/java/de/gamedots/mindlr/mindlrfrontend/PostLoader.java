package de.gamedots.mindlr.mindlrfrontend;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by max on 26.09.15.
 */
public class PostLoader {

    private int indexCurrent = 0;
    private ArrayList<Post> postList = new ArrayList<>();

    public PostLoader(){
        Post[] posts = {new Post("First", "Sport", "15.1.15"),
                new Post("Second", "Sport", "15.1.15"),
                new Post("Third", "Sport", "15.1.15"),
                new Post("Fourth", "Sport", "15.1.15"),
                new Post("Fifth", "Sport", "15.1.15"),
                new Post("Sixth", "Sport", "15.1.15"),
                new Post("Seventh", "Sport", "15.1.15"),
                new Post("Number 8", "Sport", "15.1.15"),
                new Post("Number 9", "Sport", "15.1.15"),
                new Post("The 10. Post", "Sport", "15.1.15"),
                new Post("The 11. Posts", "Sport", "15.1.15"),
                new Post("The Last / 12. Post", "Sport", "15.1.15")};
        postList.addAll(Arrays.asList(posts));
    }

    public Post getCurrent(){
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

    }
}
