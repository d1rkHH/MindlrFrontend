package de.gamedots.mindlr.mindlrfrontend.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.gamedots.mindlr.mindlrfrontend.MindlrApplication;
import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.auth.ProviderFactory;
import de.gamedots.mindlr.mindlrfrontend.controller.PostLoader;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.AuthProviderEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.CategoryEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.DraftEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.ItemCategoryEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.ItemEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.PostEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserCategoryEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserCreatePostEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserPostEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrDBHelper;
import de.gamedots.mindlr.mindlrfrontend.helper.CategoryHelper;
import de.gamedots.mindlr.mindlrfrontend.helper.DateFormatHelper;
import de.gamedots.mindlr.mindlrfrontend.jobs.SendInitialCategoriesTask;
import de.gamedots.mindlr.mindlrfrontend.jobs.WritePostTask;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.ImageUploadResult;
import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;
import de.gamedots.mindlr.mindlrfrontend.view.activity.WritePostActivity;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserCategoryEntry.COLUMN_CATEGORY_KEY;
import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserCategoryEntry.CONTENT_URI;
import static de.gamedots.mindlr.mindlrfrontend.view.activity.WritePostActivity.JSON_CONTENT_URI_KEY;

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

    public static void createUserEntryIfNotExists(
            Context context, long userServerId, String email, String provider) {

        String selection = UserEntry.COLUMN_SERVER_ID + " = ?";

        // check if user already exists
        Cursor cursor = context.getContentResolver()
                .query(UserEntry.CONTENT_URI,
                        null,
                        selection,
                        new String[]{String.valueOf(userServerId)},
                        null
                );

        // no existing user
        if (cursor == null || !cursor.moveToFirst()) {

            // get auth provider entry by name for relation to user
            Cursor authproviderCursor = context.getContentResolver().query(AuthProviderEntry.CONTENT_URI,
                    null, AuthProviderEntry.COLUMN_NAME + " = ?",
                    new String[]{provider},null);

            if (authproviderCursor != null && authproviderCursor.moveToFirst()) {

                long authprovider_key = authproviderCursor.getLong(authproviderCursor.getColumnIndex(AuthProviderEntry
                        .TABLE_NAME + "." + AuthProviderEntry._ID));
                authproviderCursor.close();

                // no active user, so create local user entry
                ContentValues ucv = new ContentValues();
                ucv.put(UserEntry.COLUMN_EMAIL, email);
                ucv.put(UserEntry.COLUMN_SERVER_ID, userServerId);
                ucv.put(UserEntry.COLUMN_IS_ACTIVE, 1);
                ucv.put(UserEntry.COLUMN_AUTH_PROVIDER_KEY, authprovider_key);
                context.getContentResolver().insert(UserEntry.CONTENT_URI, ucv);

                loadUserFromDB(context);
            }
        } else {
            ContentValues cv = new ContentValues();
            cv.put(UserEntry.COLUMN_IS_ACTIVE, 1);
            // activate user
            context.getContentResolver().update(
                    UserEntry.CONTENT_URI, cv, UserEntry.COLUMN_SERVER_ID + " = ? ",
                    new String[]{String.valueOf(userServerId)});
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
            MindlrApplication.User.create(id, ProviderFactory.getInstance(authProvider, context));
            cursor.close();

        }
        dbHelper.close();


        Cursor c = context.getContentResolver().query(MindlrContract.UserPostEntry.CONTENT_URI,
                null, null, null, null);
        if (c != null && c.moveToFirst()) {
            //Toast.makeText(context, "USerPosts: " + c.getCount() + " first id=" +
            //        c.getLong(c.getColumnIndex(UserPostEntry._ID)), Toast.LENGTH_SHORT).show();
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
                "." + UserPostEntry.COLUMN_POST_KEY + " INNER JOIN " +
                ItemEntry.TABLE_NAME +
                " ON " + PostEntry.TABLE_NAME +
                "." + PostEntry.COLUMN_ITEM_KEY +
                " = " + ItemEntry.TABLE_NAME +
                "." + ItemEntry._ID;

        queryBuilder.setTables(innerJoin);
        MindlrDBHelper dbHelper = new MindlrDBHelper(context);
        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                new String[]{
                        PostEntry.COLUMN_SERVER_ID,
                        ItemEntry.COLUMN_CONTENT_TEXT,
                        ItemEntry.COLUMN_CONTENT_URI
                },
                UserPostEntry.COLUMN_VOTE + " = ? AND " + UserPostEntry.COLUMN_USER_KEY + " = ?" + " AND " +
                UserPostEntry.COLUMN_SYNC_FLAG + " = ? ",
                new String[]{Integer.toString(UserPostEntry.VOTE_UNDEFINED), Long.toString
                        (MindlrApplication.User.getId()), UserPostEntry.UNSYNCED},
                null,
                null,
                null);

        final int server_id_index = 0;
        final int text_index = 1;
        final int uri_index = 2;
        List<ViewPost> toInsertPosts = new LinkedList<>();
        if (cursor != null && cursor.moveToFirst()) {
            //Toast.makeText(context, "Cursor count: " + cursor.getCount(), Toast.LENGTH_SHORT).show();
            postLoaded = cursor.getCount();
            do {
                ViewPost vp = new ViewPost(
                        cursor.getLong(server_id_index),
                        cursor.getString(text_index),
                        cursor.getString(uri_index));
                toInsertPosts.add(vp);
            } while (cursor.moveToNext());
            cursor.close();
        }
        dbHelper.close();
        Log.v(LOG.AUTH, "habe geladen " + postLoaded);
        PostLoader.getInstance().addAllPosts(toInsertPosts);
        return postLoaded;
    }

    public static void updatePostVoteType(Context context, long post_server_id, int vote) {
        Log.v(LOG.AUTH, "STORE " +  PostLoader.getInstance().getCurrent().getContentText());

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
            values.put(UserPostEntry.COLUMN_VOTE_DATE, System.currentTimeMillis());

            context.getContentResolver().update(UserPostEntry.CONTENT_URI, values,
                    UserPostEntry.COLUMN_POST_KEY + " = ? AND " + UserPostEntry.COLUMN_USER_KEY + " = ?",
                    new String[]{Long.toString(postCursor.getLong(postId_index)),
                    String.valueOf(MindlrApplication.User.getId())}
            );
            postCursor.close();
        }
    }

    public static void storeUserCreatePostFromJSON(Uri draftUri, boolean isDraft, JSONObject content, Context
            context)
            throws JSONException {

        Log.v(LOG.AUTH, content.toString());

        // 1. create and insert item
        ContentValues cv = new ContentValues();
        cv.put(ItemEntry.COLUMN_CONTENT_URI, content.getString(JSON_CONTENT_URI_KEY));
        cv.put(ItemEntry.COLUMN_CONTENT_TEXT, content.getString(WritePostActivity.JSON_CONTENT_TEXT_KEY));
        long itemId = -1;
        if (!isDraft) {
            // no items exist so insert it
            itemId = ItemEntry.getLongIdFromUri(context.getContentResolver().insert(ItemEntry
                    .CONTENT_URI, cv));
        } else {
            // loaded from drafts and synced so update item information
            Cursor draftCursor = getDraftFromUri(draftUri, context);

            if (draftCursor != null && draftCursor.moveToFirst()) {
                itemId = draftCursor.getLong(draftCursor.getColumnIndex(DraftEntry.COLUMN_ITEM_KEY));
                context.getContentResolver()
                        .update(ItemEntry.CONTENT_URI, cv,
                                ItemEntry._ID + " = ? ",
                                new String[]{String.valueOf(itemId)}
                        );

                // delete draft
                context.getContentResolver()
                        .delete(DraftEntry.CONTENT_URI,
                                DraftEntry._ID + " = ? ",
                                new String[]{String.valueOf(
                                        draftCursor.getLong(draftCursor.getColumnIndex(DraftEntry._ID)))});

                draftCursor.close();
            }
        }

        // 2. insert createdpostentry
        if (itemId > 0) {
            if (!isDraft) {
                JSONArray categories = content.getJSONArray(WritePostActivity.JSONARR_CONTENT_CATEGORIES_KEY);
                for (int i = 0; i < categories.length(); ++i) {
                    // TODO: item x category with ID, NAME ?
                    String cat_name = categories.get(i).toString();
                    Cursor categoryCursor = context.getContentResolver().query(CategoryEntry.CONTENT_URI,
                            null, CategoryEntry.COLUMN_NAME + " = ? ",
                            new String[]{cat_name}, null);
                    if (categoryCursor != null && categoryCursor.moveToFirst()) {
                        cv = new ContentValues();
                        cv.put(ItemCategoryEntry.COLUMN_ITEM_KEY, itemId);
                        cv.put(ItemCategoryEntry.COLUMN_CATEGORY_KEY, categoryCursor.getLong(categoryCursor
                                .getColumnIndex(CategoryEntry._ID)));
                        context.getContentResolver().insert(ItemCategoryEntry.CONTENT_URI, cv);
                        categoryCursor.close();
                    }
                }
            }

            // 3. insert usercreatepost
            cv = new ContentValues();
            cv.put(UserCreatePostEntry.COLUMN_USER_KEY, MindlrApplication.User.getId());
            cv.put(UserCreatePostEntry.COLUMN_ITEM_KEY, itemId);
            cv.put(UserCreatePostEntry.COLUMN_SERVER_ID,
                    content.getLong(WritePostActivity.JSON_CONTENT_SERVER_ID_KEY));

            long dateMillis = DateFormatHelper.getLongMillisFromDateString(
                    content.getString(WritePostActivity.JSON_CONTENT_USER_CREATE_POST_SUBMIT_DATE));

            cv.put(UserCreatePostEntry.COLUMN_SUBMIT_DATE, dateMillis);
            context.getContentResolver().insert(UserCreatePostEntry.CONTENT_URI, cv);
        }
    }

    public static void updateOrCreateDraft(JSONObject content, Uri draftUri, Context context )
            throws JSONException {

        boolean shouldCreateDraft = draftUri == null || draftUri.toString().isEmpty();

        ContentValues cv = new ContentValues();
        cv.put(ItemEntry.COLUMN_CONTENT_URI, content.getString(JSON_CONTENT_URI_KEY));
        cv.put(ItemEntry.COLUMN_CONTENT_TEXT, content.getString(WritePostActivity.JSON_CONTENT_TEXT_KEY));
        long itemId = -1;
        if (shouldCreateDraft) {
            itemId = ItemEntry.getLongIdFromUri(context.getContentResolver().insert(ItemEntry
                    .CONTENT_URI, cv));
            if (itemId > 0){
                ContentValues draftCv = new ContentValues();
                draftCv.put(DraftEntry.COLUMN_ITEM_KEY, itemId);
                draftCv.put(DraftEntry.COLUMN_USER_KEY, MindlrApplication.User.getId());
                draftCv.put(DraftEntry.COLUMN_SUBMIT_DATE, System.currentTimeMillis());

                context.getContentResolver().insert(DraftEntry.CONTENT_URI, draftCv);
            }
        } else {
            Cursor draftCursor = getDraftFromUri(draftUri, context);

            if (draftCursor != null && draftCursor.moveToFirst()) {
                itemId = draftCursor.getLong(draftCursor.getColumnIndex(DraftEntry.COLUMN_ITEM_KEY));
                if (itemId >= 0) {
                    context.getContentResolver()
                            .update(ItemEntry.CONTENT_URI, cv,
                                    ItemEntry.TABLE_NAME + "." + ItemEntry._ID + " = ? ",
                                    new String[]{String.valueOf(itemId)}
                            );
                }
                draftCursor.close();
            }
        }
    }

    public static void handleImageResult(ImageUploadResult event, Context context){
        try {
            if (event.successful) {
                event.content.put(JSON_CONTENT_URI_KEY, event.link);
                Toast.makeText(context, "Result OK SEND", Toast.LENGTH_SHORT).show();
                new WritePostTask(context, event.content, event.draftUri).execute();

            } else {
                Utility.updateOrCreateDraft(event.content, event.draftUri, context);
                Toast.makeText(context, context.getString(R.string.post_send_failure_msg), Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static Cursor getDraftFromUri(Uri uri, Context context){
        return context.getContentResolver()
                .query(DraftEntry.CONTENT_URI,
                        new String[]{DraftEntry.TABLE_NAME + "." + DraftEntry._ID,
                                DraftEntry.COLUMN_ITEM_KEY},
                        DraftEntry.TABLE_NAME + "." + DraftEntry._ID + " = ? ",
                        new String[]{DraftEntry.getIdPathFromUri(uri)},
                        null
                );
    }

    public static void deleteSyncedAndDownvotedPosts(Context context, Set<ViewPost> posts) {
        // Delete synced and downvoted post
        for (ViewPost vp : posts) {
            if (vp.getVote() == UserPostEntry.VOTE_DISLIKED) {
                Cursor postCursor = context.getContentResolver().query(PostEntry.CONTENT_URI,
                        new String[]{PostEntry._ID},
                        PostEntry.COLUMN_SERVER_ID + " = ? ",
                        new String[]{Long.toString(vp.getServerId())},
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
                    new String[]{Long.toString(vp.getServerId())},
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

    public static void insertUserCategories(Context context) {
        long userId = MindlrApplication.User.getId();

        ContentValues cv = new ContentValues();
        for(long categoryId : CategoryHelper.getCategories()){
            Log.v(LOG.AUTH, ""+categoryId);
            cv.clear();
            cv.put(UserCategoryEntry.COLUMN_USER_KEY, userId);
            cv.put(UserCategoryEntry.COLUMN_CATEGORY_KEY, categoryId);

            context.getContentResolver().insert(UserCategoryEntry.CONTENT_URI, cv);
        }
    }

    public static void sendInitialUserCategories(Context context) {
        Cursor query = context.getContentResolver().query(CONTENT_URI,
                new String[]{COLUMN_CATEGORY_KEY},
                MindlrContract.UserCategoryEntry.COLUMN_USER_KEY + " = ?",
                new String[]{String.valueOf(MindlrApplication.User.getId())},
                null);

        List<String> ratedCategories = new ArrayList<>();
        List<String> unratedCategories = new ArrayList<>();
        if(query != null){
            while (query.moveToNext()){
                Cursor cursor = context.getContentResolver().query(CategoryEntry.CONTENT_URI,
                        null,
                        CategoryEntry.TABLE_NAME + "." + CategoryEntry._ID + " = ? ",
                        new String[]{String.valueOf(
                                query.getLong(query.getColumnIndex(COLUMN_CATEGORY_KEY)))},
                        null
                );
                if (cursor != null && cursor.moveToFirst()){
                    ratedCategories.add(cursor.getString(cursor.getColumnIndex(CategoryEntry.COLUMN_NAME)));
                    cursor.close();
                }
            }
            query.close();

            String selection = "";
            for (int i = 0; i < ratedCategories.size(); ++i){
                if (i==0){
                    selection += CategoryEntry.COLUMN_NAME + "!= '" + ratedCategories.get(i) + "'";
                } else {
                    selection += " AND " + CategoryEntry.COLUMN_NAME + "!= '" + ratedCategories.get(i) + "'";
                }
            }
            Cursor unratedCursor = context.getContentResolver().query(CategoryEntry.CONTENT_URI,
                    new String[]{CategoryEntry.COLUMN_NAME},
                    selection,
                    null,
                    null
            );
            if (unratedCursor != null){
                while (unratedCursor.moveToNext()){
                    unratedCategories.add(unratedCursor.getString(unratedCursor.getColumnIndex
                            (CategoryEntry.COLUMN_NAME)));
                }
                unratedCursor.close();
            }

        }

        JSONObject content = new JSONObject();
        JSONArray categories = new JSONArray();
        try {
            for (String rated : ratedCategories) {
                JSONObject ratedObj = new JSONObject();
                ratedObj.put("name", rated);
                ratedObj.put("vote", 1);
                categories.put(ratedObj);
            }
            for (String urated : unratedCategories) {
                JSONObject unratedObj = new JSONObject();
                unratedObj.put("name", urated);
                unratedObj.put("vote", -1);
                categories.put(unratedObj);
            }
            content.put("categories", categories);

            new SendInitialCategoriesTask(null, content, true).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

