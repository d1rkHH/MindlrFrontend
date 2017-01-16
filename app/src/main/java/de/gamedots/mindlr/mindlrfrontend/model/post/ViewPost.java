package de.gamedots.mindlr.mindlrfrontend.model.post;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.ItemEntry;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.PostFragment;

/**
 * Created by max on 26.09.15.
 * <p>
 * This version of a Post is used to hold the data how the used interacted with this post
 * such as the time he viewed the post the make sure his vote is credible and the vote (upvote, downvote,
 * neutral)
 */
public class ViewPost implements Parcelable {

    public static final int VOTE_DISLIKE = 0;
    public static final int VOTE_LIKE = 1;

    //Values from the post itself
    private long server_id;
    private String contentText;
    private String contentUri;

    //Values the reader generated
    int vote;

    public ViewPost(long id, String contentText, String contentUri) {
        this.server_id = id;
        this.contentText = contentText;
        this.contentUri = contentUri;
        this.vote = -1;

    }

    public int getVote() {
        return vote;
    }

    public long getServerId() {
        return server_id;
    }

    public String getContentUri() {
        return contentUri;
    }

    public String getContentText() {
        return contentText;
    }

    public void ratePositive() {
        this.vote = VOTE_LIKE;
    }

    public void rateNegative() {
        this.vote = VOTE_DISLIKE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(server_id);
        out.writeString(contentText);
        out.writeString(contentUri);
    }

    public static final Parcelable.Creator<ViewPost> CREATOR = new Parcelable.Creator<ViewPost>() {
        public ViewPost createFromParcel(Parcel in) {
            return new ViewPost(in);
        }

        public ViewPost[] newArray(int size) {
            return new ViewPost[size];
        }
    };

    private ViewPost(Parcel in) {
        server_id = in.readLong();
        contentText = in.readString();
        contentUri = in.readString();
    }

    public static ViewPost fromCursor(Cursor c){
        String contentText = c.getString(c.getColumnIndex(ItemEntry.COLUMN_CONTENT_TEXT));
        String contentUri = c.getString(c.getColumnIndex(ItemEntry.COLUMN_CONTENT_URI));
        long serverId = c.getLong(PostFragment.COLUMN_POST_SERVER_ID);

        return new ViewPost(serverId, contentText, contentUri);
    }
}
