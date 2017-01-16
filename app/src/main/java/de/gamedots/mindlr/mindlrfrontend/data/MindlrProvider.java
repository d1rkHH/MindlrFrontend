package de.gamedots.mindlr.mindlrfrontend.data;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.DraftEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.ItemCategoryEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.ItemEntry;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;

import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.AuthProviderEntry;
import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.AuthProviderEntry.buildAuthProviderUri;
import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.CONTENT_AUTHORITY;
import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.CategoryEntry;
import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.PATH_AUTH_PROVIDER;
import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.PATH_CATEGORY;
import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.PATH_DRAFT;
import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.PATH_ITEM;
import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.PATH_ITEM_CATEGORY;
import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.PATH_POST;
import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.PATH_USER;
import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.PATH_USER_CATEGORY;
import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.PATH_USER_CREATE_POST;
import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.PATH_USER_POST;
import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.PostEntry;
import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserCategoryEntry;
import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserCreatePostEntry;
import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserEntry;
import static de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserPostEntry;

public class MindlrProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MindlrDBHelper _dbOpenHelper;

    // Constants that will be matched against the content URI´s
    public static final int USER = 100;
    public static final int POST = 200;
    public static final int CATEGORY = 300;
    public static final int AUTH_PROVIDER = 400;
    public static final int USER_POST = 500;
    public static final int USER_CREATE_POST = 600;
    public static final int USER_CATEGORY = 700;
    public static final int POSTS_FOR_USER = 800;

    public static final int ITEM = 900;
    public static final int DRAFT = 1000;
    public static final int LOADED_DRAFT = 1100;
    public static final int ITEM_CATEGORY = 1200;

    private static final SQLiteQueryBuilder sPostsForUserQueryBuilder;
    private static final SQLiteQueryBuilder sDraftsQueryBuilder;
    private static final SQLiteQueryBuilder sUserCreatedPostsBuilder;

    static {
        sPostsForUserQueryBuilder = new SQLiteQueryBuilder();
        // userpost INNER JOIN post ON userpost.post_id = post._id
        //          INNER JOIN item ON post.item_id = item.id
        sPostsForUserQueryBuilder.setTables(
                UserPostEntry.TABLE_NAME + " INNER JOIN " +
                        PostEntry.TABLE_NAME +
                        " ON " + UserPostEntry.TABLE_NAME +
                        "." + UserPostEntry.COLUMN_POST_KEY
                        + " = " + PostEntry.TABLE_NAME +
                        "." + PostEntry._ID + " INNER JOIN " +
                        ItemEntry.TABLE_NAME +
                        " ON " + PostEntry.TABLE_NAME +
                        "." + PostEntry.COLUMN_ITEM_KEY +
                        " = " + ItemEntry.TABLE_NAME +
                        "." + ItemEntry._ID
        );

        sDraftsQueryBuilder = new SQLiteQueryBuilder();
        // draft INNER JOIN item ON draft.item_id = item._id
        sDraftsQueryBuilder.setTables(
                DraftEntry.TABLE_NAME + " INNER JOIN " +
                        ItemEntry.TABLE_NAME +
                        " ON " + DraftEntry.TABLE_NAME +
                        "." + DraftEntry.COLUMN_ITEM_KEY +
                        " = " + ItemEntry.TABLE_NAME +
                        "." + ItemEntry._ID

        );

        sUserCreatedPostsBuilder = new SQLiteQueryBuilder();
        sUserCreatedPostsBuilder.setTables(
                UserCreatePostEntry.TABLE_NAME + " INNER JOIN " +
                ItemEntry.TABLE_NAME +
                " ON " + UserCreatePostEntry.TABLE_NAME +
                "." + UserCreatePostEntry.COLUMN_ITEM_KEY +
                " = " + ItemEntry.TABLE_NAME +
                "." + ItemEntry._ID
        );
    }

    // userpost.user_id = ? AND vote = ?
    public static final String sUserPostForIdSelection =
            UserPostEntry.TABLE_NAME + "." + UserPostEntry.COLUMN_USER_KEY + " = ? " + " AND " +
                    UserPostEntry.COLUMN_VOTE + " = ? ";

    //extract userId from uri and perform inner join to get all user related posts
    private
    Cursor
    getPostsByUser(Uri uri, String selection, String[] selArgs, String[] projection, String sortOrder) {

        long userId = UserPostEntry.getUserIdFromUri(uri);
        String sel = (selection == null) ? sUserPostForIdSelection : selection;
        String[] args = (selArgs == null) ?
                new String[]{Long.toString(userId), Integer.toString(UserPostEntry.VOTE_LIKED)}
                    : selArgs;

        return sPostsForUserQueryBuilder.query(_dbOpenHelper.getReadableDatabase(),
                projection,
                sel,
                args,
                null,
                null,
                sortOrder);
    }

    public static final String sDraftForIdSelection = DraftEntry.TABLE_NAME + "." + DraftEntry._ID + " = ? ";

    private Cursor getSingleDraftWithJoinedContent(Uri uri, String[] projection, String sortOrder) {
        return sDraftsQueryBuilder.query(_dbOpenHelper.getReadableDatabase(),
                projection,
                sDraftForIdSelection,
                new String[]{DraftEntry.getIdPathFromUri(uri)},
                null,
                null,
                sortOrder);
    }

    private Cursor getDraftsWithJoinedContent(String[] projection, String selection, String[] selArgs, String
                                              sortOrder) {
        return sDraftsQueryBuilder.query(_dbOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getUserCreatedPosts(String[] projection, String selection, String[] args, String
            sortOrder) {
        return sUserCreatedPostsBuilder.query(_dbOpenHelper.getReadableDatabase(),
                projection,
                selection,
                args,
                null,
                null,
                sortOrder);
    }

    // build a UriMatcher that match each URI to the USER, POST, CATEGORY.. constants
    private static UriMatcher buildUriMatcher() {

        // create the uri matcher; root URI do not match
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        String authority = CONTENT_AUTHORITY;
        uriMatcher.addURI(authority, PATH_USER, USER);
        uriMatcher.addURI(authority, PATH_POST, POST);
        uriMatcher.addURI(authority, PATH_ITEM, ITEM);
        uriMatcher.addURI(authority, PATH_DRAFT, DRAFT);
        uriMatcher.addURI(authority, PATH_DRAFT + "/#", LOADED_DRAFT);
        uriMatcher.addURI(authority, PATH_ITEM_CATEGORY, ITEM_CATEGORY);
        uriMatcher.addURI(authority, PATH_CATEGORY, CATEGORY);
        uriMatcher.addURI(authority, PATH_AUTH_PROVIDER, AUTH_PROVIDER);
        uriMatcher.addURI(authority, PATH_USER_POST, USER_POST);
        uriMatcher.addURI(authority, PATH_USER_CREATE_POST, USER_CREATE_POST);
        uriMatcher.addURI(authority, PATH_USER_CATEGORY, USER_CATEGORY);
        uriMatcher.addURI(authority, PATH_USER_POST + "/" + PATH_USER + "/#", POSTS_FOR_USER);


        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        _dbOpenHelper = new MindlrDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String
            sortOrder) {

        Cursor resultCursor;
        switch (sUriMatcher.match(uri)) {
            case USER:
                resultCursor = _dbOpenHelper.getReadableDatabase().query(UserEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case POST:
                resultCursor = _dbOpenHelper.getReadableDatabase().query(PostEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CATEGORY:
                resultCursor = _dbOpenHelper.getReadableDatabase().query(CategoryEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case AUTH_PROVIDER:
                resultCursor = _dbOpenHelper.getReadableDatabase().query(AuthProviderEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case USER_POST:
                resultCursor = _dbOpenHelper.getReadableDatabase().query(UserPostEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case POSTS_FOR_USER:
                resultCursor = getPostsByUser(uri, selection, selectionArgs, projection, sortOrder);
                break;
            case USER_CREATE_POST:
                resultCursor = getUserCreatedPosts(projection, selection, selectionArgs, sortOrder);
                break;
            case USER_CATEGORY:
                resultCursor = _dbOpenHelper.getReadableDatabase().query(UserCategoryEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ITEM:
                resultCursor = _dbOpenHelper.getReadableDatabase().query(ItemEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case DRAFT:
                resultCursor = getDraftsWithJoinedContent(projection,selection, selectionArgs, sortOrder);
                break;
            case LOADED_DRAFT:
                resultCursor = getSingleDraftWithJoinedContent(uri, projection, sortOrder);
                break;
            case ITEM_CATEGORY:
                resultCursor = _dbOpenHelper.getReadableDatabase().query(ItemCategoryEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // register content observer for cursor to wathc for changes that happen
        // to the uri
        resultCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return resultCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USER:
                return UserEntry.CONTENT_TYPE;
            case POST:
                return PostEntry.CONTENT_TYPE;
            case CATEGORY:
                return CategoryEntry.CONTENT_TYPE;
            case AUTH_PROVIDER:
                return AuthProviderEntry.CONTENT_TYPE;
            case USER_POST:
                return UserPostEntry.CONTENT_TYPE;
            case USER_CREATE_POST:
                return UserCreatePostEntry.CONTENT_TYPE;
            case USER_CATEGORY:
                return UserCategoryEntry.CONTENT_TYPE;
            case POSTS_FOR_USER:
                return UserPostEntry.CONTENT_TYPE;
            case ITEM:
                return ItemEntry.CONTENT_TYPE;
            case DRAFT:
                return DraftEntry.CONTENT_TYPE;
            case ITEM_CATEGORY:
                return ItemCategoryEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = _dbOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case USER: {
                long _id = db.insert(UserEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = UserEntry.buildUserUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            case POST: {
                long _id = db.insert(PostEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = PostEntry.buildPostUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            case CATEGORY: {
                long _id = db.insert(CategoryEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = CategoryEntry.buildCategoryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case AUTH_PROVIDER: {
                long _id = db.insert(AuthProviderEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = buildAuthProviderUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case USER_POST: {
                long _id = db.insert(UserPostEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = UserPostEntry.buildUserPostUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case USER_CREATE_POST: {
                long _id = db.insert(UserCreatePostEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = UserCreatePostEntry.buildUserCreatePostUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case USER_CATEGORY: {
                long _id = db.insert(UserCategoryEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = UserCategoryEntry.buildUserCategoryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case ITEM: {
                long _id = db.insert(ItemEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ItemEntry.buildItemUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case DRAFT: {
                long _id = db.insert(DraftEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = DraftEntry.buildDraftUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case ITEM_CATEGORY: {
                long _id = db.insert(ItemCategoryEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ItemCategoryEntry.buildItemCategoryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = _dbOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowsDeleted;
        //To remove all rows and get a count pass "1" as the whereClause.
        if (selection == null) selection = "1";

        switch (match) {
            case USER: {
                rowsDeleted = db.delete(UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case POST: {
                rowsDeleted = db.delete(PostEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case CATEGORY: {
                rowsDeleted = db.delete(CategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case AUTH_PROVIDER: {
                rowsDeleted = db.delete(AuthProviderEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case USER_POST: {
                rowsDeleted = db.delete(UserPostEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case USER_CREATE_POST: {
                rowsDeleted = db.delete(UserCreatePostEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case USER_CATEGORY: {
                rowsDeleted = db.delete(UserCategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case ITEM: {
                rowsDeleted = db.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case DRAFT: {
                rowsDeleted = db.delete(DraftEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case ITEM_CATEGORY: {
                rowsDeleted = db.delete(ItemCategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = _dbOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case USER: {
                rowsUpdated = db.update(UserEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case POST: {
                rowsUpdated = db.update(PostEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case CATEGORY: {
                rowsUpdated = db.update(CategoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case AUTH_PROVIDER: {
                rowsUpdated = db.update(AuthProviderEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case USER_POST: {
                rowsUpdated = db.update(UserPostEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case USER_CREATE_POST: {
                rowsUpdated = db.update(UserCreatePostEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case USER_CATEGORY: {
                rowsUpdated = db.update(UserCategoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case ITEM: {
                rowsUpdated = db.update(ItemEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case DRAFT: {
                rowsUpdated = db.update(DraftEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case ITEM_CATEGORY: {
                rowsUpdated = db.update(ItemCategoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = _dbOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case USER:
                return insertWithTransaction(UserEntry.TABLE_NAME, db, values, uri);
            case POST:
                return insertWithTransaction(PostEntry.TABLE_NAME, db, values, uri);
            case CATEGORY:
                return insertWithTransaction(CategoryEntry.TABLE_NAME, db, values, uri);
            case AUTH_PROVIDER:
                return insertWithTransaction(AuthProviderEntry.TABLE_NAME, db, values, uri);
            case USER_POST:
                return insertWithTransaction(UserPostEntry.TABLE_NAME, db, values, uri);
            case USER_CREATE_POST:
                return insertWithTransaction(UserCreatePostEntry.TABLE_NAME, db, values, uri);
            case USER_CATEGORY:
                return insertWithTransaction(UserCategoryEntry.TABLE_NAME, db, values, uri);
            case ITEM:
                return insertWithTransaction(ItemEntry.TABLE_NAME, db, values, uri);
            case DRAFT:
                return insertWithTransaction(DraftEntry.TABLE_NAME, db, values, uri);
            case ITEM_CATEGORY:
                return insertWithTransaction(ItemCategoryEntry.TABLE_NAME, db, values, uri);

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    public int insertWithTransaction(String table, SQLiteDatabase db, ContentValues[] values, Uri uri) {

        // open transaction for fast and memory efficient insert
        db.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values) {
                long _id = db.insert(table, null, value);
                if (_id != -1) {
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        Log.v(LOG.AUTH, "-------------insert post on startup " + returnCount);
        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
    }
}
