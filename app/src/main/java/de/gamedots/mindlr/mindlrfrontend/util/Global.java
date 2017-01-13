package de.gamedots.mindlr.mindlrfrontend.util;

/**
 * Created by Max Wiechmann on 26.09.15.
 */
public final class Global {

    private Global(){}

    //public static final PostLoader postLoader = new PostLoader();

    //Server Connection
    public static final String SERVER_URL = "https://mindlr.com/api/";
    //public static final String SERVER_URL = "http://192.168.2.160:8000/api/";

    //Methods
    public static final String BACKEND_METHOD_LOAD_POSTS = "request_items";
    public static final String BACKEND_METHOD_WRITE_POST = "write_post";
    public static final String BACKEND_METHOD_SEND_VOTES = "store_feedback";
    public static final String BACKEND_METHOD_DELETE_POST = "delete_post";
    public static final String BACKEND_METHOD_SHOW_PROFILE = "show_profile";
    public static final String BACKEND_METHOD_SIGN_IN = "sign_in";
    public static final String BACKEND_METHOD_SYNC_USERPOSTS = "sync_user_posts";
    public static final String BACKEND_METHOD_GET_CATEGORIES = "get_categories";
    public static final String BACKEND_METHOD_GET_REPORT_OPTIONS = "get_report_options";
}
