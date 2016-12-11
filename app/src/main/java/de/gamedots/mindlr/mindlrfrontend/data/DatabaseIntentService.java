package de.gamedots.mindlr.mindlrfrontend.data;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;
import java.util.Vector;

import de.gamedots.mindlr.mindlrfrontend.MindlrApplication;
import de.gamedots.mindlr.mindlrfrontend.controller.PostLoader;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.CategoryEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserPostEntry;
import de.gamedots.mindlr.mindlrfrontend.model.Category;
import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;
import de.gamedots.mindlr.mindlrfrontend.util.Global;


public class DatabaseIntentService extends IntentService {

    public static final String INSERT_POST_ACTION = "insertposts";
    public static final String INSERT_CATEGORIES_ACTION = "insertcategories";


    public DatabaseIntentService() {
        super("MindlrDbService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(INSERT_POST_ACTION)) {
            storePostsToLocalDb();
        } else if (intent.getAction().equals(INSERT_CATEGORIES_ACTION)) {
            storeCategories();
        }
    }


    private void storePostsToLocalDb() {
        List<ViewPost> posts = PostLoader.getInstance().getPostList();
        Vector<ContentValues> values = new Vector<>(posts.size());
        for (int i = 0; i < posts.size(); i++) {
            ViewPost p = posts.get(i);
            ContentValues cv = new ContentValues();
            cv.put(MindlrContract.PostEntry.COLUMN_CONTENT_TEXT, p.getContentText());
            cv.put(MindlrContract.PostEntry.COLUMN_SERVER_ID, p.getId());
            cv.put(MindlrContract.PostEntry.COLUMN_CONTENT_URI, "");
            values.add(cv);
        }
        ContentValues[] cvArray = new ContentValues[values.size()];
        values.toArray(cvArray);

        SQLiteDatabase db = new MindlrDBHelper(this).getWritableDatabase();
        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                long post_id = db.insert(MindlrContract.PostEntry.TABLE_NAME, null, value);
                // insert post associated with current user to userpost table
                if (post_id != -1) {
                    ContentValues userPostValues = new ContentValues();
                    userPostValues.put(UserPostEntry.COLUMN_USER_KEY, MindlrApplication.User.getId());
                    userPostValues.put(UserPostEntry.COLUMN_POST_KEY, post_id);
                    userPostValues.put(UserPostEntry.COLUMN_VOTE, UserPostEntry.VOTE_UNDEFINED);
                    db.insert(UserPostEntry.TABLE_NAME, null, userPostValues);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
        getContentResolver().notifyChange(MindlrContract.PostEntry.CONTENT_URI, null);
        getContentResolver().notifyChange(UserPostEntry.CONTENT_URI, null);
    }

    private void storeCategories() {
        ContentValues[] categories = new ContentValues[Category.allCategories.size()];
        for (int i = 0; i < Category.allCategories.size(); i++) {
            ContentValues cv = new ContentValues();
            cv.put(CategoryEntry.COLUMN_NAME, Category.allCategories.get(i).getName());
            categories[i] = cv;
        }
        getContentResolver().bulkInsert(CategoryEntry.CONTENT_URI, categories);
    }
}
