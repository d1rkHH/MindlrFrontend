package de.gamedots.mindlr.mindlrfrontend.model.post;

import java.util.Date;

import de.gamedots.mindlr.mindlrfrontend.model.Category;

/**
 * Created by max on 26.09.15.
 * Type of Post that is used to display the posts of the user
 */
public class UserPost {

    private long userID;
    private Date submitDate;
    private String contentText;
    private Category category;
    private int seen;
    private int upvotes;
    private int downvotes;
    private int quality;

    public UserPost(long userID, Date submitDate, String contentText, Category category){
        this.userID = userID;
        this.submitDate = submitDate;
        this.contentText = contentText;
        this.category = category;
    }

    public UserPost(long userID, Date submitDate, String contentText, Category category, int quality, int seen, int upvotes, int downvotes) {
        this.userID = userID;
        this.submitDate = submitDate;
        this.contentText = contentText;
        this.category = category;
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

    public Category getCategory() {
        return category;
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
