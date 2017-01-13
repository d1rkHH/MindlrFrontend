package de.gamedots.mindlr.mindlrfrontend.helper;

import android.net.Uri;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dirk on 11.01.17.
 */

public class UriHelper {
    private static final String imgur_authority = "i.imgur.com";
    private static final String youtube_authority = "youtu.be";

    public static boolean isImgur(Uri uri){
        return isAuthority(imgur_authority, uri);
    }

    public static boolean isYoutube(Uri uri){
        return isAuthority(youtube_authority, uri);
    }

    public static String extractVideoPathFromYoutubeUrl(String videoURL){
        String videoID = null;
        Matcher matcher = Pattern.compile("https://youtu\\.be/([\\w-_]+)" ).matcher(videoURL);
        if(matcher.find()){
            videoID = matcher.group(1);
        }
        return videoID;
    }

    private static boolean isAuthority(String toCheckAuthority, Uri uri){
        boolean matched = false;
        if (uri != null && !uri.toString().isEmpty()){
            String uriAuthority = uri.getAuthority();
            if (uriAuthority != null){
                matched = uriAuthority.equals(toCheckAuthority);
            }
        }
        return matched;
    }
}
