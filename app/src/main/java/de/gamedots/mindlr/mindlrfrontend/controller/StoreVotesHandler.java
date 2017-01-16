package de.gamedots.mindlr.mindlrfrontend.controller;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import de.gamedots.mindlr.mindlrfrontend.MindlrApplication;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserPostEntry;
import de.gamedots.mindlr.mindlrfrontend.jobs.StoreVotesTask;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;
import de.gamedots.mindlr.mindlrfrontend.util.Utility;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.PostFragment;

/**
 * Created by Max Wiechmann on 18.08.16.
 */
public class StoreVotesHandler {

    private static final StoreVotesHandler HANDLER = new StoreVotesHandler();
    private int voteCounter;
    private final static int SEND_THRESHOLD = 5;

    private StoreVotesHandler() {
    }

    public static StoreVotesHandler getInstance() {
        return HANDLER;
    }

    private final Set<ViewPost> _postsInSending = new HashSet<>();

    public void sendingSuccess(Context context) {
        Utility.markPostsAsSynced(context, _postsInSending);
        Utility.deleteSyncedAndDownvotedPosts(context, _postsInSending);
        _postsInSending.clear();
    }

    void increaseSendThreshold() {
        voteCounter++;
        triggerSending();
    }

    private void triggerSending() {
        if (voteCounter >= SEND_THRESHOLD) {
            voteCounter = 0;
            prepareAndStartSending();
        }
    }

    private void prepareAndStartSending(){
        /* Get all unsynced user posts that have been voted */
        long userId = MindlrApplication.User.getId();
        Uri userPostForUserUri = UserPostEntry.buildUserPostWithUserId(userId);

        String selection = UserPostEntry.TABLE_NAME + "." + UserPostEntry.COLUMN_USER_KEY + " = ? " +
                " AND " + UserPostEntry.COLUMN_VOTE + " != ? AND " +
                UserPostEntry.COLUMN_SYNC_FLAG + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(userId),
                String.valueOf(UserPostEntry.VOTE_UNDEFINED),
                UserPostEntry.UNSYNCED
        };

        Cursor unsyncedPostsCursor = MindlrApplication.getInstance().getContentResolver()
                .query(userPostForUserUri, PostFragment.POST_COLUMNS,
                        selection, selectionArgs, null);

        Set<ViewPost> postsToSync = new HashSet<>();
        if (unsyncedPostsCursor != null){
            while (unsyncedPostsCursor.moveToNext()){
                postsToSync.add(ViewPost.fromCursor(unsyncedPostsCursor));
            }
        }

        _postsInSending.clear();
        _postsInSending.addAll(postsToSync);

        /* Prepare data to sync with server */
        JSONObject content = new JSONObject();
        JSONArray feedback = new JSONArray();

        for (ViewPost post : _postsInSending) {
            JSONObject item_fb = new JSONObject();
            try {
                item_fb.put("item_id", post.getServerId());
                item_fb.put("vote", post.getVote());
                //TODO: Add / count time viewed
                item_fb.put("time_viewed", 0);
                //TODO: Check if detail view was used, store here
                item_fb.put("used_detail_view", false);
                //TODO: Add report option here if item was reported
                item_fb.put("local_date", System.currentTimeMillis());
            } catch (JSONException e) {
                Log.e(LOG.POSTS, "JSON Exception while sending feedback", e);
            }
        }
        try {
            content.put("feedback", feedback);
            Log.d(LOG.POSTS, "Sending votes to server");
            new StoreVotesTask(MindlrApplication.getInstance(), content).execute();
        } catch (JSONException e) {
            Log.e(LOG.JSON, "Could not send votes to server!");
        }
    }
}
