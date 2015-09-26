package de.gamedots.mindlr.mindlrfrontend.models;

import java.util.Date;

/**
 * Created by max on 26.09.15.
 * Type of Post that is used to display the posts of the user
 */
public class UserPost {

    private long userID;
    private Date submitDate;
    private String contentText;
    private int categoryID; //TODO: Create Category Class to represent DB table
    private int seen;
    private int upvotes;
    private int downvotes;
    private int quality;

    public UserPost(long userID, Date submitDate, String contentText, int categoryID){
        this.userID = userID;
        this.submitDate = submitDate;
        this.contentText = contentText;
        this.categoryID = categoryID;
    }

    public UserPost(long userID, Date submitDate, String contentText, int categoryID, int quality, int seen, int upvotes, int downvotes) {
        this.userID = userID;
        this.submitDate = submitDate;
        this.contentText = contentText;
        this.categoryID = categoryID;
        this.quality = quality;
        this.seen = seen;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public String getContentText() {
        return contentText;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public long getUserID() {
        return userID;
    }

    public int getSeen() {
        return seen;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public int getQuality() {
        return quality;
    }
}
