package de.gamedots.mindlr.mindlrfrontend.model.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by Dirk on 24.04.16.
 */
@Table(name = "posts")
public class Post extends Model {

    @Column(name = "remote_id", unique = true, notNull = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long server_id;

    @Column(name = "creator_id", notNull = true)
    User creator;

    @Column(name = "submit_date")
    Date submit_date;

    @Column(name = "content_url")
    String content_url;

    @Column(name = "content_text", length = 1000)
    String content_text;

    @Column(name = "favorited")
    boolean favorited;











}
