package de.gamedots.mindlr.mindlrfrontend.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.AuthProviderEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.CategoryEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.PostEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserCategoryEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserCreatePostEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserPostEntry;

public class MindlrDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "mindlr.db";

    public MindlrDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqliteDB) {

        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME +
                " (" + UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                UserEntry.COLUMN_SERVER_ID + " INTEGER NOT NULL, " +
                UserEntry.COLUMN_EMAIL + " TEXT NOT NULL, " +
                UserEntry.COLUMN_AUTH_PROVIDER_KEY + " INTEGER NOT NULL, " +
                UserEntry.COLUMN_IS_ACTIVE + " INTEGER NOT NULL, " +

                " UNIQUE (" + UserEntry.COLUMN_EMAIL + ") ON CONFLICT REPLACE, " +

                " FOREIGN KEY (" + UserEntry.COLUMN_AUTH_PROVIDER_KEY + ") REFERENCES " +
                AuthProviderEntry.TABLE_NAME + " (" + AuthProviderEntry._ID + "), " +

                " UNIQUE (" + UserEntry.COLUMN_AUTH_PROVIDER_KEY + ") ON CONFLICT REPLACE);";


        final String SQL_CREATE_POST_TABLE = "CREATE TABLE " + PostEntry.TABLE_NAME +
                " (" + PostEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                PostEntry.COLUMN_SERVER_ID + " INTEGER NOT NULL, " +
                PostEntry.COLUMN_CONTENT_URI + " TEXT NOT NULL, " +
                PostEntry.COLUMN_CONTENT_TEXT + " TEXT NOT NULL, " +

                " UNIQUE (" + PostEntry.COLUMN_SERVER_ID + ") ON CONFLICT IGNORE);";


        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + CategoryEntry.TABLE_NAME +
                " (" + CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                CategoryEntry.COLUMN_NAME + " TEXT NOT NULL, " +

                " UNIQUE (" + CategoryEntry.COLUMN_NAME + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_AUTHPROVIDER_TABLE = "CREATE TABLE " + AuthProviderEntry.TABLE_NAME +
                " (" + AuthProviderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                AuthProviderEntry.COLUMN_NAME + " TEXT NOT NULL, " +

                " UNIQUE (" + AuthProviderEntry.COLUMN_NAME + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_USER_POST_TABLE = "CREATE TABLE " + UserPostEntry.TABLE_NAME +
                " (" + UserPostEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                UserPostEntry.COLUMN_USER_KEY + " INTEGER NOT NULL, " +
                UserPostEntry.COLUMN_POST_KEY + " INTEGER NOT NULL, " +
                UserPostEntry.COLUMN_VOTE + " INTEGER NOT NULL, " +
                UserPostEntry.COLUMN_SYNC_FLAG + " INTEGER DEFAULT 0, " +

                " FOREIGN KEY (" + UserPostEntry.COLUMN_USER_KEY + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry._ID + "), " +

                " FOREIGN KEY (" + UserPostEntry.COLUMN_POST_KEY + ") REFERENCES " +
                PostEntry.TABLE_NAME + " (" + PostEntry._ID + "), " +

                " UNIQUE (" + UserPostEntry.COLUMN_POST_KEY + ", " +
                UserPostEntry.COLUMN_USER_KEY + ") ON CONFLICT IGNORE);";

        final String SQL_CREATE_USER_CREATE_POST_TABLE = "CREATE TABLE " + UserCreatePostEntry.TABLE_NAME +
                " (" + UserCreatePostEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                UserCreatePostEntry.COLUMN_USER_KEY + " INTEGER NOT NULL, " +
                UserCreatePostEntry.COLUMN_CONTENT_URI + " TEXT NOT NULL, " +
                UserCreatePostEntry.COLUMN_CONTENT_TEXT + " TEXT NOT NULL, " +
                UserCreatePostEntry.COLUMN_CATEGORY_KEY + " INTEGER NOT NULL, " +
                UserCreatePostEntry.COLUMN_SUBMIT_DATE + " INTEGER NOT NULL, " +
                UserCreatePostEntry.COLUMN_IS_DRAFT + " INTEGER DEFAULT 0, " +
                UserCreatePostEntry.COLUMN_UPVOTES + " INTEGER DEFAULT 0, " +
                UserCreatePostEntry.COLUMN_DOWNVOTES + " INTEGER DEFAULT 0, " +

                " FOREIGN KEY (" + UserCreatePostEntry.COLUMN_USER_KEY + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry._ID + ")); ";

        final String SQL_CREATE_USER_CATEGORY_TABLE = "CREATE TABLE " + UserCategoryEntry.TABLE_NAME +
                " (" + UserCategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                UserCategoryEntry.COLUMN_USER_KEY + " INTEGER NOT NULL, " +
                UserCategoryEntry.COLUMN_CATEGORY_KEY + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + UserCategoryEntry.COLUMN_USER_KEY + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry._ID + "), " +

                " UNIQUE (" + UserCategoryEntry.COLUMN_USER_KEY + ") ON CONFLICT REPLACE, " +

                " FOREIGN KEY (" + UserCategoryEntry.COLUMN_CATEGORY_KEY + ") REFERENCES " +
                PostEntry.TABLE_NAME + " (" + PostEntry._ID + "), " +

                " UNIQUE (" + UserCategoryEntry.COLUMN_CATEGORY_KEY + ") ON CONFLICT REPLACE);";

        sqliteDB.execSQL(SQL_CREATE_AUTHPROVIDER_TABLE);
        sqliteDB.execSQL(SQL_CREATE_USER_TABLE);
        sqliteDB.execSQL(SQL_CREATE_POST_TABLE);
        sqliteDB.execSQL(SQL_CREATE_CATEGORY_TABLE);
        sqliteDB.execSQL(SQL_CREATE_USER_POST_TABLE);
        sqliteDB.execSQL(SQL_CREATE_USER_CREATE_POST_TABLE);
        sqliteDB.execSQL(SQL_CREATE_USER_CATEGORY_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        // no need to alter before production level, simply drop table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserPostEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserCreatePostEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserCategoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AuthProviderEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PostEntry.TABLE_NAME);

        // and recreate database
        onCreate(sqLiteDatabase);
    }
}
