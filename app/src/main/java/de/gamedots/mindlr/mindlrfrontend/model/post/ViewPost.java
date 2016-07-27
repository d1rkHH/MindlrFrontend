package de.gamedots.mindlr.mindlrfrontend.model.post;

/**
 * Created by max on 26.09.15.
 *
 * This version of a Post is used to hold the data how the used interacted with this post
 * such as the time he viewed the post the make sure his vote is credible and the vote (upvote, downvote, neutral)
 */
public class ViewPost {

    public static final int VOTE_NEGATIVE = 0;
    public static final int VOTE_POSITIVE = 1;

    //Values from the post itself
    private long id;
    private String contentText;

    //Values the reader generated
    int vote;

    public ViewPost(long id, String contentText){
        this.id = id;
        this.contentText = contentText;
        this.vote = -1;

    }

    public int getVote() {
        return vote;
    }

    public long getId() {
        return id;
    }

    public String getContentText() {
        return contentText;
    }

    public void ratePositive(){
        this.vote = VOTE_POSITIVE;
    }

    public void rateNegative(){
        this.vote = VOTE_NEGATIVE;
    }

}
