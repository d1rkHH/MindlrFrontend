package de.gamedots.mindlr.mindlrfrontend;

import java.util.Date;

/**
 * Created by Dirk on 05.09.15.
 */
public class Post {

    private String createDate;
    private String postText;
    private String category;
    private int upVotes;
    private int downVotes;
    private int test;


    public Post(String postText, String category, String date) {
        this.postText = postText;
        this.createDate = date;
        this.category = category;
    }

    public Post(String postText, String category, String createDate, int upVotes, int downVotes) {
        this.createDate = createDate;
        this.postText = postText;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.category = category;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(int upVotes) {
        this.upVotes = upVotes;
    }

    public int getDownVotes() {
        return downVotes;
    }

    public void setDownVotes(int downVotes) {
        this.downVotes = downVotes;
    }
}
