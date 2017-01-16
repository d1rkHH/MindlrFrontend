package de.gamedots.mindlr.mindlrfrontend.previews.strategy;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubePlayer;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.gamedots.mindlr.mindlrfrontend.R;


/**
 * Created by max on 14.01.17.
 */

public class YoutubeStrategy implements PreviewStrategy{

    private final static String youtube_regex = "https://youtu\\.be/([\\w\\-_]+)";

    private String _videoID;

    /**
     * Interface to communicate between an Activity that can handle YoutubePlayerEvents.
     */
    public interface YoutubeHandler extends YouTubePlayer.OnInitializedListener{
        YouTubePlayer.Provider getYouTubePlayerProvider();
        void setVideoID(String id);
    }

    public YoutubeStrategy(){
    }

    @Override
    public boolean match(String url) {
        Log.v("Preview", "Match " + url + " to Youtube Link");
        Matcher matcher = Pattern.compile(youtube_regex).matcher(url);
        if(matcher.find()) {
            _videoID = matcher.group(1);
            Log.v("Preview", "YT ID: " + _videoID);
            return true;
        }
        return false;
    }

    @Override
    public PreviewStrategy getCopy() {
        YoutubeStrategy strategy = new YoutubeStrategy();
        strategy.setVideoID(_videoID);
        return strategy;
    }

    @Override
    public void buildPreviewUI(Context context, List<View> views) {
        /*
         * We can only load content for those Activities that handle Player initialization.
         */
        if (context instanceof YoutubeHandler) {
            YoutubeHandler handler = (YoutubeHandler) context;
            handler.setVideoID(_videoID);
            if (handler.getYouTubePlayerProvider() != null) {
                handler.getYouTubePlayerProvider()
                        .initialize(context.getString(R.string.youtube_developer_key), handler);
            }
        } else {
            /**
             * Context does not handle video loading so check if we can display
             * a video thumbnail
             */
            ImageView thumbnail = null;
            for (View view : views){
                if (view instanceof ImageView){
                    thumbnail = (ImageView)view;
                }
            }
            if (thumbnail != null) {
                thumbnail.setVisibility(View.VISIBLE);
                Glide.with(context).load(getThumbnailUrl()).into(thumbnail);
            }
        }
    }

    private void setVideoID(String videoID){
        _videoID = videoID;
    }

    private String getThumbnailUrl(){
        return "https://img.youtube.com/vi/" + getVideoID() + "/0.jpg";
    }

    public String getVideoID(){
        return _videoID;
    }
}
