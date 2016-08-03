package de.gamedots.mindlr.mindlrfrontend.model.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

/**
 * Standard post object with data information for display purposes.
 * -> "ViewPost" and "LikePost" that can be favored
 */
@Table(name = "posts")
public class Post extends Model {

    @Expose
    @Column(name = "remote_id", unique = true, notNull = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long server_id;

    @Expose
    @Column(name = "content_url")
    public String content_url;

    @Expose
    @Column(name = "content_text", length = 1000)
    public String content_text;

    public Post() {
        super();
    }

    public Post(long server_id, String content_url, String content_text) {
        this.server_id = server_id;
        this.content_url = content_url;
        this.content_text = content_text;
    }
}
