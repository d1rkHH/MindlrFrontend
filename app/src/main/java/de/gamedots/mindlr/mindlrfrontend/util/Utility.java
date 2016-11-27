package de.gamedots.mindlr.mindlrfrontend.util;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.gamedots.mindlr.mindlrfrontend.MindlrApplication;
import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.controller.PostLoader;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.AuthProviderEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.PostEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserPostEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrDBHelper;
import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;
import de.gamedots.mindlr.mindlrfrontend.view.activity.WritePostActivity;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class Utility {

    public static void addAuthStateToPreference(Context context, boolean authenticated) {
        SharedPreferences prefs = getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        int newState = (authenticated) ?
                R.string.pref_authentication_true
                : R.string.pref_authentication_false;

        editor.putBoolean(context.getString(R.string.pref_authentication_key),
                Boolean.parseBoolean(context.getString(newState)));
        editor.apply();
    }

    public static boolean getAuthStateFromPreference(Context context) {
        return getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.pref_authentication_key),
                        Boolean.parseBoolean(context.getString(R.string.pref_notification_false)));
    }

    public static void createUserEntryIfNotExists(Context context, String email, String provider) {
        String selection = UserEntry.TABLE_NAME + "." + UserEntry.COLUMN_EMAIL + " = ? ";

        // check if user already exists
        Cursor cursor = context.getContentResolver()
                .query(UserEntry.CONTENT_URI, null, selection, new String[]{email}, null);

        // no existing user
        if (cursor == null || !cursor.moveToFirst()) {

            // create auth provider entry for relation to user
            ContentValues cv = new ContentValues();
            cv.put(AuthProviderEntry.COLUMN_NAME, provider);
            Uri resultUri = context.getContentResolver().insert(AuthProviderEntry.CONTENT_URI, cv);

            // no active user, so create local user entry
            ContentValues ucv = new ContentValues();
            ucv.put(UserEntry.COLUMN_EMAIL, email);
            ucv.put(UserEntry.COLUMN_SERVER_ID, 1); //TODO: update when server_id is retrieved
            ucv.put(UserEntry.COLUMN_IS_ACTIVE, 1);
            ucv.put(UserEntry.COLUMN_AUTH_PROVIDER_KEY, ContentUris.parseId(resultUri));
            context.getContentResolver().insert(UserEntry.CONTENT_URI, ucv);

            loadUserFromDB(context);
        } else {
            ContentValues cv = new ContentValues();
            cv.put(UserEntry.COLUMN_IS_ACTIVE, 1);
            // activate user
            context.getContentResolver().update(
                    UserEntry.CONTENT_URI, cv, UserEntry.COLUMN_EMAIL + " = ?", new String[]{email});
            // load activated user into app user
            loadUserFromDB(context);
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    public static void loadUserFromDB(Context context) {
        // a user signIn earlier so we offer him offline access
        // grab the current activated user from database
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String innerJoin = UserEntry.TABLE_NAME + " INNER JOIN " +
                AuthProviderEntry.TABLE_NAME +
                " ON " + UserEntry.TABLE_NAME +
                "." + UserEntry.COLUMN_AUTH_PROVIDER_KEY +
                " = " + AuthProviderEntry.TABLE_NAME +
                "." + AuthProviderEntry._ID;

        queryBuilder.setTables(innerJoin);
        MindlrDBHelper dbHelper = new MindlrDBHelper(context);
        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                null,
                UserEntry.COLUMN_IS_ACTIVE + " = ?",
                new String[]{"1"},
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            // create application user
            long id = cursor.getLong(cursor.getColumnIndex(UserEntry._ID));
            String authProvider = cursor.getString(cursor.getColumnIndex(AuthProviderEntry.COLUMN_NAME));
            MindlrApplication.User.create(id, authProvider);
            cursor.close();

        }
        dbHelper.close();


        Cursor c = context.getContentResolver().query(MindlrContract.UserPostEntry.CONTENT_URI,
                null, null, null, null);
        if (c != null && c.moveToFirst()) {
            Toast.makeText(context, "USerPosts: " + c.getCount() + " first id=" +
                    c.getLong(c.getColumnIndex(UserPostEntry._ID)), Toast.LENGTH_SHORT).show();
            c.close();
        }
    }

    public static boolean isFirstStart(Context context) {
        return getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.pref_first_start_key),
                        Boolean.parseBoolean(context.getString(R.string.pref_first_start_true)));
    }

    public static void invalidateFirstStart(Context context) {
        SharedPreferences prefs = getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(context.getString(R.string.pref_first_start_key),
                Boolean.parseBoolean(context.getString(R.string.pref_first_start_false)));
        editor.commit();
    }

    public static void deactivateCurrentUser(Context context) {
        ContentValues newCv = new ContentValues();
        newCv.put(UserEntry.COLUMN_IS_ACTIVE, 0);

        context.getContentResolver().update(UserEntry.CONTENT_URI,
                newCv,
                UserEntry.COLUMN_IS_ACTIVE + " = ?",
                new String[]{"1"});
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static int loadUnvotedPostsOrNothing(Context context) {
        int postLoaded = 0;

        // load unvoted posts for user
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String innerJoin = PostEntry.TABLE_NAME + " INNER JOIN " +
                UserPostEntry.TABLE_NAME +
                " ON " + PostEntry.TABLE_NAME +
                "." + PostEntry._ID +
                " = " + UserPostEntry.TABLE_NAME +
                "." + UserPostEntry.COLUMN_POST_KEY;

        queryBuilder.setTables(innerJoin);
        MindlrDBHelper dbHelper = new MindlrDBHelper(context);
        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                new String[]{
                        PostEntry.COLUMN_SERVER_ID,
                        PostEntry.COLUMN_CONTENT_TEXT,
                        PostEntry.COLUMN_CONTENT_URI
                },
                UserPostEntry.COLUMN_VOTE + " = ? AND " + UserPostEntry.COLUMN_USER_KEY + " = ?",
                new String[]{Integer.toString(UserPostEntry.VOTE_UNDEFINED), Long.toString
                        (MindlrApplication.User.getId())},
                null,
                null,
                null);

        final int server_id_index = 0;
        final int text_index = 1;
        final int uri_index = 2;
        Toast.makeText(context, "Cursor count: " + cursor.getCount(), Toast.LENGTH_SHORT).show();
        List<ViewPost> toInsertPosts = new LinkedList<>();
        if (cursor != null && cursor.moveToFirst()) {
            postLoaded = cursor.getCount();
            do {
                ViewPost vp = new ViewPost(cursor.getLong(server_id_index), cursor.getString(text_index));
                toInsertPosts.add(vp);
            } while (cursor.moveToNext());
            cursor.close();
        }
        dbHelper.close();

        PostLoader.getInstance().addAllPosts(toInsertPosts);
        return postLoaded;
    }

    public static void updatePostVoteType(Context context, long post_server_id, int vote) {

        Cursor postCursor = context.getContentResolver().query(PostEntry.CONTENT_URI,
                new String[]{PostEntry._ID},
                PostEntry.COLUMN_SERVER_ID + " = ? ",
                new String[]{Long.toString(post_server_id)},
                null
        );
        final int postId_index = 0;

        if (postCursor != null && postCursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(UserPostEntry.COLUMN_VOTE, vote);
            context.getContentResolver().update(UserPostEntry.CONTENT_URI, values,
                    UserPostEntry.COLUMN_POST_KEY + " = ?",
                    new String[]{Long.toString(postCursor.getLong(postId_index))}
            );
            postCursor.close();
        }
    }

    public static void buildUserCreatePostValuesFromJSON(ContentValues cv, boolean isDraft, JSONObject
            content)
            throws JSONException {
        cv.put(MindlrContract.UserCreatePostEntry.COLUMN_USER_KEY, MindlrApplication.User.getId());
        cv.put(MindlrContract.UserCreatePostEntry.COLUMN_CONTENT_TEXT, content.getString(WritePostActivity
                .JSON_CONTENT_TEXT_KEY));
        cv.put(MindlrContract.UserCreatePostEntry.COLUMN_CONTENT_URI, content.getString(WritePostActivity
                .JSON_CONTENT_URI_KEY));
        cv.put(MindlrContract.UserCreatePostEntry.COLUMN_SUBMIT_DATE, System.currentTimeMillis());
        if (isDraft) {
            cv.put(MindlrContract.UserCreatePostEntry.COLUMN_IS_DRAFT, 1);
        }
        cv.put(MindlrContract.UserCreatePostEntry.COLUMN_CATEGORY_KEY, content.getJSONArray(WritePostActivity
                .JSONARR_CONTENT_CATEGORIES_KEY).getLong(0));
    }

    public static void deleteSyncedAndDownvotedPosts(Context context, Set<ViewPost> posts) {
        // Delete synced and downvoted post
        for (ViewPost vp : posts) {
            if (vp.getVote() == UserPostEntry.VOTE_DISLIKED) {
                Cursor postCursor = context.getContentResolver().query(PostEntry.CONTENT_URI,
                        new String[]{PostEntry._ID},
                        PostEntry.COLUMN_SERVER_ID + " = ? ",
                        new String[]{Long.toString(vp.getId())},
                        null
                );

                if (postCursor != null && postCursor.moveToFirst()) {
                    context.getContentResolver().delete(UserPostEntry.CONTENT_URI,
                            UserPostEntry.COLUMN_USER_KEY + " = ? AND " +
                            UserPostEntry.COLUMN_POST_KEY + " = ? AND " +
                            UserPostEntry.COLUMN_SYNC_FLAG + " = ? ",
                            new String[]{
                                    Long.toString(MindlrApplication.User.getId()),
                                    Long.toString(postCursor.getLong(postCursor.getColumnIndex(PostEntry
                                            ._ID))),
                                    UserPostEntry.SYNCED
                            }
                    );
                    postCursor.close();
                }
            }
        }
    }

    public static void markPostsAsSynced(Context context, Set<ViewPost> posts) {
        for (ViewPost vp : posts) {
            Cursor postCursor = context.getContentResolver().query(PostEntry.CONTENT_URI,
                    new String[]{PostEntry._ID},
                    PostEntry.COLUMN_SERVER_ID + " = ? ",
                    new String[]{Long.toString(vp.getId())},
                    null
            );

            ContentValues cv = new ContentValues();
            cv.put(UserPostEntry.COLUMN_SYNC_FLAG, 1);
            if (postCursor != null && postCursor.moveToFirst()) {
                context.getContentResolver().update(UserPostEntry.CONTENT_URI,
                        cv,
                        UserPostEntry.COLUMN_USER_KEY + " = ? AND " + UserPostEntry.COLUMN_POST_KEY + " = ? ",
                        new String[]{
                                Long.toString(MindlrApplication.User.getId()),
                                Long.toString(postCursor.getLong(postCursor.getColumnIndex(PostEntry._ID)))
                        }
                );
                postCursor.close();
            }
        }
    }
}

