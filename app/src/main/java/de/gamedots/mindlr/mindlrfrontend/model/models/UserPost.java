package de.gamedots.mindlr.mindlrfrontend.model.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import static de.gamedots.mindlr.mindlrfrontend.model.models.UserPost.VoteType.*;


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

    @Column(name = "user", notNull = true)
    User user;

    @Column(name = "post", notNull = true)
    Post post;

    @Column(name = "vote")
    VoteType vote = NOT_VOTED_YET;

    public UserPost() {
        super();
    }

    public UserPost(User user, Post post, VoteType vote) {
        this.user = user;
        this.post = post;
        this.vote = vote;
    }
}
