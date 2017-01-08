package de.gamedots.mindlr.mindlrfrontend.model.post;

/**
 * Created by max on 26.09.15.
 *
 * This version of a Post is used to hold the data how the used interacted with this post
 * such as the time he viewed the post the make sure his vote is credible and the vote (upvote, downvote, neutral)
 */
public class ViewPost {

    public static final int VOTE_DISLIKE = -1;
    public static final int VOTE_LIKE = 1;

    //Values from the post itself
    private long id;
    private String contentText;
    private String content_uri;

    //Values the reader generated
    int vote;

    public ViewPost(long id, String contentText, String contentUri){
        this.id = id;
        this.contentText = contentText;
        this.content_uri = contentUri;
        this.vote = -1;

    }

    public int getVote() {
        return vote;
    }

    public long getId() {
        return id;
    }

    public String getContentUri(){
        return content_uri;
    }

    public String getContentText() {
        return contentText;
    }

    public void ratePositive(){
        this.vote = VOTE_LIKE;
    }

    public void rateNegative(){
        this.vote = VOTE_DISLIKE;
    }

}
