package de.gamedots.mindlr.mindlrfrontend.model.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import static de.gamedots.mindlr.mindlrfrontend.model.models.UserPost.VoteType.NOT_VOTED_YET;

/**
 * Through table for user and posts. A record will be inserted if a user
 * votes a "ViewPost". This table will be used to
 */
@Table(name = "user_posts")
public class UserPost extends Model {

    public enum VoteType {
        UPVOTE(1), DOWNVOTE(0), NOT_VOTED_YET(-1);

        private int vote;

        VoteType(int vote) {
            this.vote = vote;
        }

        public int getVote() {
            return vote;
        }
    }

    @Column(name = "user_id", notNull = true)
    public User user;

    @Column(name = "post_id", notNull = true)
    public Post post;

    @Column(name = "vote")
    public VoteType vote = NOT_VOTED_YET;

    /* Field to differ between liked posts and favored posts */
    @Column(name = "favored")
    public boolean favored = false;

    /* Every record that has been synced (needSync=true) and VoteType == DOWNVOTE will be deleted
     * UPVOTED (and NOT favored!) posts get stored to a limit of 100 and then replaced with newer ones.
     * More UPVOTED posts can be loaded on demand and will be cached */

    /* Was post synchronized (successful send to server)?*/
    @Column(name = "needsync")
    public boolean needSync = false;


    public UserPost() {
        super();
    }

    public UserPost(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
