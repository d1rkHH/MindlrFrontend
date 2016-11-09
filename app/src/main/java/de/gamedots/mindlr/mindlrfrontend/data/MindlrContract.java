package de.gamedots.mindlr.mindlrfrontend.data;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MindlrContract {

    // unique name for the content provider
    public static final String CONTENT_AUTHORITY = "de.gamedots.mindlr.mindlrfrontend";

    // base URI to contact the content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Paths (that will be append to base content uri for possible URI´s)
    public static final String PATH_USER = "user";
    public static final String PATH_POST = "post";
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_AUTH_PROVIDER = "auth_provider";
    public static final String PATH_USER_POST = "user_post";
    public static final String PATH_USER_CREATE_POST = "user_created_posts";
    public static final String PATH_USER_CATEGORY = "user_category";


    public static final class UserEntry implements BaseColumns {

        // Main Uri to access User entries
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();

        // Cursor types used to describe the return type of single item or multiple items
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;

        //Table name
        public static final String TABLE_NAME = "user";

        // Server id to identify user on the remote server stored as long
        public static final String COLUMN_SERVER_ID = "server_id";

        /* If the a user signs in using a different auth provider than last time,
         * the email will be used to identify him. In that way the existing user
         * and user related data is used without creating a new user.
         * Stored as string.
         */
        public static final String COLUMN_EMAIL = "email";

        // Flag indicating current active user
        public static final String COLUMN_IS_ACTIVE = "is_active";

        /* A User only have one auth provider at a time. That will be used to
         * authenticate the user in further session (silently) unless he logs out, so the provider
         * will be cleared and he needs to choose provider again */
        public static final String COLUMN_AUTH_PROVIDER_KEY = "auth_provider_id";

        public static Uri buildUserUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class PostEntry implements BaseColumns {

        // Main Uri to access Post entries
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POST).build();

        // Cursor types used to describe the return type of single item or multiple items
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POST;

        //Table name
        public static final String TABLE_NAME = "post";

        // Server id to identify post on the remote server stored as long
        public static final String COLUMN_SERVER_ID = "server_id";

        // Uri describing any additional resource (e.g. Image ) for that post
        public static final String COLUMN_CONTENT_URI = "content_uri";

        // Content text of a post
        public static final String COLUMN_CONTENT_TEXT = "content_text";

        public static Uri buildPostUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class CategoryEntry implements BaseColumns {

        // Main Uri to access Category entries
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();

        // Cursor types used to describe the return type of single item or multiple items
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

        //Table name
        public static final String TABLE_NAME = "category";

        // Unique name of the specific category used for a post. The corresponding
        // string resource will be selected by that name.
        public static final String COLUMN_NAME = "name";

        public static Uri buildCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class AuthProviderEntry implements BaseColumns {

        // Main Uri to access AuthProvider entries
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_AUTH_PROVIDER).build();

        // Cursor types used to describe the return type of single item or multiple items
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AUTH_PROVIDER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AUTH_PROVIDER;

        //Table name
        public static final String TABLE_NAME = "auth_provider";

        // Name of the auth provider like "google", "twitter"
        public static final String COLUMN_NAME = "name";

        public static Uri buildAuthProviderUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class UserPostEntry implements BaseColumns {

        // Main Uri to access UserPost entries
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER_POST).build();

        // Cursor types used to describe the return type of single item or multiple items
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER_POST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER_POST;

        //Table name
        public static final String TABLE_NAME = "userpost";

        // Foreign Key to user table
        public static final String COLUMN_USER_KEY = "user_id";

        // Foreign Key to post table
        public static final String COLUMN_POST_KEY = "post_id";

        // Vote state for the post [LIKED = 1, DISLIKED = 0, UNDEFINED = -1]
        // An entry in that table MUST have a set vote state different than UNDEFINED.
        public static final String COLUMN_VOTE = "vote";

        public static final int VOTE_LIKED = 1;
        public static final int VOTE_DISLIKED = 0;
        public static final int VOTE_UNDEFINED = -1;

        /* Every record that has been synced (needSync=true) and VoteType == DOWNVOTE will be deleted
         * UPVOTED (and NOT favored!) posts get stored to a limit of 100 and then replaced with newer ones.
         * More UPVOTED posts can be loaded on demand and will be cached */

        // Flag indicating the sync state of the post with the remote server.
        public static final String COLUMN_SYNC_FLAG = "synced";

        public static Uri buildUserPostUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildUserPostWithUserId(long userId) {
            // ../user_post/user/[userId]
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_USER)
                    .appendPath(Long.toString(userId))
                    .build();
        }

        public static long getUserIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

    }

    public static final class UserCreatePostEntry implements BaseColumns {

        // Main Uri to access UserCreatePost entries
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER_CREATE_POST).build();

        // Cursor types used to describe the return type of single item or multiple items
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER_CREATE_POST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER_CREATE_POST;

        // Table name
        public static final String TABLE_NAME = "user_created_post";

        // Foreign Key to user table
        public static final String COLUMN_USER_KEY = "user_id";

        // Uri describing any additional resource (e.g. Image ) for that post
        public static final String COLUMN_CONTENT_URI = "content_uri";

        // Content text of a user created post. This information is stored instead a
        // foreign key to a post because the user can create a draft without
        // having the post synced and that would result into a empty Post.SERVER_ID
        public static final String COLUMN_CONTENT_TEXT = "content_text";

        // The user given category during post creation
        public static final String COLUMN_CATEGORY_KEY = "category_id";

        // Date the post was created. It will be first created locally and than
        // updated when successfully synced with server
        public static final String COLUMN_SUBMIT_DATE = "submit_date";

        // Set to true when the WritePostTask fails or the user wants to store a draft, default is false (0)
        public static final String COLUMN_IS_DRAFT = "is_draft";

        // Number of total upvotes for this created post
        public static final String COLUMN_UPVOTES = "upvotes";

        // Number of total downvotes for this created post
        public static final String COLUMN_DOWNVOTES = "downvotes";

        public static Uri buildUserCreatePostUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getIdPathFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static final class UserCategoryEntry implements BaseColumns {

        // Main Uri to access UserCreatePost entries
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER_CATEGORY).build();

        // Cursor types used to describe the return type of single item or multiple items
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER_CATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER_CATEGORY;

        //Table name
        public static final String TABLE_NAME = "user_category";

        // Foreign Key to user table
        public static final String COLUMN_USER_KEY = "user_id";

        // The user chosen category for first setup
        public static final String COLUMN_CATEGORY_KEY = "category_id";

        public static Uri buildUserCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
