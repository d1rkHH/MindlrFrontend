package de.gamedots.mindlr.mindlrfrontend.data;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import de.gamedots.mindlr.mindlrfrontend.MindlrApplication;
import de.gamedots.mindlr.mindlrfrontend.controller.PostLoader;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.CategoryEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.ItemEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.PostEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserCreatePostEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserPostEntry;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.Category;
import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;


public class DatabaseIntentService extends IntentService {

    public static final String INSERT_POST_ACTION = "insertposts";
    public static final String INSERT_CATEGORIES_ACTION = "insertcategories";
    public static final String UPDATE_USERPOSTS_VOTES_ACTION = "updateuserposts";

    public static final String JSON_EXTRA = "json_extra";


    public DatabaseIntentService() {
        super("MindlrDbService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(INSERT_POST_ACTION)) {
            storePostsToLocalDb();
        } else if (intent.getAction().equals(INSERT_CATEGORIES_ACTION)) {
            storeCategories();
        } else if (intent.getAction().equals(UPDATE_USERPOSTS_VOTES_ACTION)){
            updateUserPostsVotes(intent.getStringExtra(JSON_EXTRA));
        }
    }

    private void storePostsToLocalDb() {
        SQLiteDatabase db = new MindlrDBHelper(this).getWritableDatabase();

        List<ViewPost> posts = PostLoader.getInstance().getPostList();
        Vector<ContentValues> values = new Vector<>(posts.size());
        List<Long> existingPostIDs = new ArrayList<>();

        for (int i = 0; i < posts.size(); i++) {
            ViewPost p = posts.get(i);

            Cursor dbPost = getContentResolver().query(PostEntry.CONTENT_URI,
                    null,
                    PostEntry.COLUMN_SERVER_ID + " = ?",
                    new String[]{String.valueOf(p.getServerId())},
                    null);
            // insert item entry if post with server id not exists
            if (dbPost == null || !dbPost.moveToFirst()) {
                // 1. create and insert item
                ContentValues cv = new ContentValues();
                cv.put(ItemEntry.COLUMN_CONTENT_TEXT, p.getContentText());
                cv.put(ItemEntry.COLUMN_CONTENT_URI, p.getContentUri());

                long itemId = db.insert(ItemEntry.TABLE_NAME, null, cv);

                // 2. add post content values using received item id
                if (itemId > 0) {
                    ContentValues postcv = new ContentValues();
                    postcv.put(PostEntry.COLUMN_ITEM_KEY, itemId);
                    postcv.put(PostEntry.COLUMN_SERVER_ID, p.getServerId());
                    values.add(postcv);
                }
            } else {
                if (dbPost.moveToFirst()){
                    existingPostIDs.add(dbPost.getLong(dbPost.getColumnIndex(PostEntry._ID)));
                }
            }
            if (dbPost != null) {
                dbPost.close();
            }
        }

        String exisitinglog = "GOT EXISTING: [";
        for (long id : existingPostIDs){
            exisitinglog += id + ", ";
        }
        exisitinglog+= "]";
        Log.v(LOG.AUTH, exisitinglog);
        ContentValues[] cvArray = new ContentValues[values.size()];
        values.toArray(cvArray);

        db.beginTransaction();
        try {
            // insert new posts and add user post relation
            ContentValues userPostValues = new ContentValues();
            for (ContentValues value : cvArray) {
                long post_id = db.insert(PostEntry.TABLE_NAME, null, value);
                // insert post associated with current user to userpost table
                if (post_id != -1) {
                    userPostValues.clear();
                    userPostValues.put(UserPostEntry.COLUMN_USER_KEY, MindlrApplication.User.getId());
                    userPostValues.put(UserPostEntry.COLUMN_POST_KEY, post_id);
                    userPostValues.put(UserPostEntry.COLUMN_VOTE, UserPostEntry.VOTE_UNDEFINED);
                    db.insert(UserPostEntry.TABLE_NAME, null, userPostValues);
                }
            }

            // insert user post relation for existing posts, but new user
            for (long postID : existingPostIDs){
                userPostValues.clear();
                userPostValues.put(UserPostEntry.COLUMN_USER_KEY, MindlrApplication.User.getId());
                userPostValues.put(UserPostEntry.COLUMN_POST_KEY, postID);
                userPostValues.put(UserPostEntry.COLUMN_VOTE, UserPostEntry.VOTE_UNDEFINED);
                db.insert(UserPostEntry.TABLE_NAME, null, userPostValues);
            }
            // set successful or transaction will not be closed
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
        getContentResolver().notifyChange(PostEntry.CONTENT_URI, null);
        getContentResolver().notifyChange(UserPostEntry.CONTENT_URI, null);
    }

    private void storeCategories() {
        ContentValues[] categories = new ContentValues[Category.allCategories.size()];
        for (int i = 0; i < Category.allCategories.size(); i++) {
            ContentValues cv = new ContentValues();
            cv.put(CategoryEntry.COLUMN_NAME, Category.allCategories.get(i).getName());
            cv.put(CategoryEntry.COLUMN_DISPLAY_NAME, getStringResourceByName(
                    Category.allCategories.get(i).getName()));
            categories[i] = cv;
        }
        getContentResolver().bulkInsert(CategoryEntry.CONTENT_URI, categories);
    }

    private String getStringResourceByName(String aString) {
        String result = "";
        try {
            String packageName = getPackageName();
            int resId = getResources().getIdentifier(aString, "string", packageName);
            result = getString(resId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void updateUserPostsVotes(String jsonString) {
        try {
            JSONObject result = new JSONObject(jsonString);
            JSONArray posts = result.getJSONArray("user_items");

            ContentValues cv = new ContentValues();
            for (int i = 0; i < posts.length(); ++i) {
                cv.clear();

                JSONObject post = posts.getJSONObject(i);
                long server_id = post.getLong("item_id");
                int upvotes = post.getInt("upvotes");
                int downvotes = post.getInt("downvotes");

                cv.put(UserCreatePostEntry.COLUMN_UPVOTES, upvotes);
                cv.put(UserCreatePostEntry.COLUMN_DOWNVOTES, downvotes);

                getContentResolver().update(UserCreatePostEntry.CONTENT_URI,
                        cv,
                        UserCreatePostEntry.COLUMN_SERVER_ID + " = ? ",
                        new String[]{String.valueOf(server_id)});

            }
        } catch (JSONException jex) {
            jex.printStackTrace();
        }
    }
}
