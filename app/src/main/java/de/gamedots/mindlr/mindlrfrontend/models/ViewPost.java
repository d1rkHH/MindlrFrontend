package de.gamedots.mindlr.mindlrfrontend.models;

/**
 * Created by max on 26.09.15.
 *
 * This version of a Post is used to hold the data how the used interacted with this post
 * such as the time he viewed the post the make sure his vote is credible and the vote (upvote, downvote, neutral)
 */
public class ViewPost {

    public static final int VOTE_NEUTRAL = 0;
    public static final int VOTE_POSITIVE = 1;
    public static final int VOTE_NEGATIVE = -1;

    //Values from the post itself
    private long id;
    private String contentText;

    //Values the reader generated
    long timeViewed;
    boolean favored;
    boolean shared;
    int vote;

    //TODO: Timer who counts how long a post was viewed

    public ViewPost(long id, String contentText){
        this.id = id;
        this.contentText = contentText;
        this.timeViewed = 0;
        this.favored = false;
        this.shared = false;
        this.vote = VOTE_NEUTRAL;

    }

    public ViewPost(String contentText){
        this.contentText = contentText;
    }

    public int getVote() {
        return vote;
    }

    public boolean isShared() {
        return shared;
    }

    public boolean isFavored() {
        return favored;
    }

    public long getTimeViewed() {
        return timeViewed;
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

    public void rateNeutral(){
        this.vote = VOTE_NEUTRAL;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public void setFavored(boolean favored) {
        this.favored = favored;
    }
}
