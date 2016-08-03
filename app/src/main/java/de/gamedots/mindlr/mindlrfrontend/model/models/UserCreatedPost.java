package de.gamedots.mindlr.mindlrfrontend.model.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.Date;

/**
 * Special post object that has been created by a app user. It will be stored
 * permanently on the device like favored posts.
 * -> "UserPost"
 */
@Table(name = "usercreatedposts")
public class UserCreatedPost extends Model {

    @Expose
    @Column(name = "post_id", notNull = true)
    public Post post;

    @Expose
    @Column(name = "creator_id", notNull = true, onDelete = Column.ForeignKeyAction.RESTRICT)
    public User creator;

    @Expose
    @Column(name = "submit_date")
    public Date submit_date;

    /* Set to true when the WritePostTask fails or the user wants to store a draft */
    @Column(name = "isdraft")
    public boolean isDraft = false;

    //global sync fields
    @Column(name = "upvotes")
    public int upvotes = 0;

    @Column(name = "downvotes")
    public int downvotes = 0;


    public UserCreatedPost() {
    }

    public UserCreatedPost(Post post, User creator, Date submit_date) {
        this.post = post;
        this.creator = creator;
        this.submit_date = submit_date;
    }
}
