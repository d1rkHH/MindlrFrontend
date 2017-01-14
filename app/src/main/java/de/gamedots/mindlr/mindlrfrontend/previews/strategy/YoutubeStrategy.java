package de.gamedots.mindlr.mindlrfrontend.previews.strategy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.PostViewFragment;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

import static de.gamedots.mindlr.mindlrfrontend.view.fragment.PostViewFragment.FRAGMENT_PLAYER_TAG;


/**
 * Created by max on 14.01.17.
 */

public class YoutubeStrategy implements PreviewStrategy{

    private final static String youtube_regex = "https://youtu\\.be/([\\w\\-_]+)";
    private final static String log_tag = "YouTubePreview";
    private static final String FULLSCREEN_KEY = "fullscreen_key";
    private static final String VIDEO_ID_KEY = "video_id_tag";

    private String _videoID;
    private boolean _fullScreen;
    private PostViewFragment _fragment;
    private YouTubePlayerSupportFragment _youtubePlayerFragment;
    private YouTubePlayer _player;
    private YouTubePlayer.OnInitializedListener _onInitializedListener;


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

    private boolean isInitialized(){
        return _fragment != null && _youtubePlayerFragment != null && _player != null && _onInitializedListener != null;
    }

    @Override
    public void buildPreviewUI(PostViewFragment fragment, Bundle savedInstanceState) {
        if(savedInstanceState != null && savedInstanceState.containsKey(FULLSCREEN_KEY) && savedInstanceState.containsKey(VIDEO_ID_KEY)){
            _fullScreen = savedInstanceState.getBoolean(FULLSCREEN_KEY);
            _videoID = savedInstanceState.getString(VIDEO_ID_KEY);
        }
        if(!isInitialized()){
            _fragment = fragment;
            _youtubePlayerFragment = new YouTubePlayerSupportFragment();
            _onInitializedListener = new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                    _player = youTubePlayer;
                    if (!wasRestored) {
                        _player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                            @Override
                            public void onFullscreen(boolean isFullscreen) {
                                _fullScreen = isFullscreen;
                            }
                        });
                    }
                    _player.setFullscreen(_fullScreen);
                    _player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    _player.cueVideo(_videoID);
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
                    String errorMessage = errorReason.toString();
                    Toast.makeText(_fragment.getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                }
            };
        }
        // dynamically add the fragment to allow nested fragments
        FragmentTransaction transaction = _fragment.getChildFragmentManager().beginTransaction();
        transaction.add(R.id.postview_video_container, _youtubePlayerFragment, FRAGMENT_PLAYER_TAG);
        //transaction.addToBackStack(null);
        transaction.commit();
        _youtubePlayerFragment.initialize(_fragment.getActivity().getString(R.string.youtube_developer_key), _onInitializedListener);
    }

    @Override
    public void saveInstanceState(Bundle outState) {
        outState.putBoolean(FULLSCREEN_KEY, _fullScreen);
        outState.putString(VIDEO_ID_KEY, _videoID);
    }

    public void setVideoID(String videoID){
        _videoID = videoID;
    }

    public boolean isFullScreen() {
        return _fullScreen;
    }


    public YouTubePlayer getPlayer() {
        return _player;
    }

    public YouTubePlayerSupportFragment getYoutubePlayerFragment() {
        return _youtubePlayerFragment;
    }
}
