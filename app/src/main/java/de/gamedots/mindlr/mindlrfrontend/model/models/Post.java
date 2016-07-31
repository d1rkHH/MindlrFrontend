package de.gamedots.mindlr.mindlrfrontend.model.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.Date;

@Table(name = "posts")
public class Post extends Model {

    @Expose
    @Column(name = "remote_id", unique = true, notNull = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long server_id;

    @Expose
    @Column(name = "creator_id", notNull = true, onDelete = Column.ForeignKeyAction.RESTRICT)
    User creator;

    @Expose
    @Column(name = "submit_date")
    Date submit_date;

    @Expose
    @Column(name = "content_url")
    String content_url;

    @Expose
    @Column(name = "content_text", length = 1000)
    String content_text;

    //global sync fields
    @Column(name = "upvotes")
    int upvotes = 0;

    @Column(name = "downvotes")
    int downvotes = 0;

    // local fields
    @Column(name = "favorited")
    boolean favorited = false;


    public Post() {
        super();
    }
}
