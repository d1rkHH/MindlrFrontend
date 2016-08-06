package de.gamedots.mindlr.mindlrfrontend.logging;

/**
 * Created by max on 27.09.15.
 *
 * Class to store strings to streamline the logging tags to make search and filters easier
 */
public class LOG {

    public static final String CONNECTION = "LOG.CONNECTION"; /* Use this log tag on processes that involve an internet connection */
    public static final String JSON = "LOG.JSON"; /* Use this log for JSON operations */
    public static final String POSTS = "LOG.POSTS"; /* Use this log for operations that involve handling ViewPosts */
    public static final String WRITE = "LOG.WRITE"; /* Use this log for operations that involve sending new posts to the server */
    public static final String VERIFIED = "LOG.VERIFIED"; /* Use this log for whenever a verification process started/finished */
    public static final String AUTH = "LOG.AUTH"; /* Log tag for all processes involving google sign in and authentication */
    public static final String LIFECYCLE = "LOG.LIFECYCLE";
}
