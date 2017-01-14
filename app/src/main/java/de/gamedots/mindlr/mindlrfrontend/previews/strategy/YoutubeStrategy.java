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


/**
 * Created by max on 14.01.17.
 */

public class YoutubeStrategy implements PreviewStrategy{

    private final static String youtube_regex = "https://youtu\\.be/([\\w\\-_]+)";
    private final static String log_tag = "YouTubePreview";
    private static final String FULLSCREEN_KEY = "fullscreen_key";
    private static final String VIDEO_ID_KEY = "video_id_tag";
    private static final String CURRENT_MILLIS_KEY = "current_millis_key";
    private static final String FRAGMENT_PLAYER_TAG = "de.gamedots.mindlrfrontend.PostViewFragment";

    private String _videoID;
    private boolean _fullScreen;
    private int _currentTimeMillis;
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
        Log.v("Preview", "Build YT UI");
        if(savedInstanceState != null && savedInstanceState.containsKey(FULLSCREEN_KEY) && savedInstanceState.containsKey(VIDEO_ID_KEY)){
            Log.v("Preview", "Load saveInstanceStates");
            _fullScreen = savedInstanceState.getBoolean(FULLSCREEN_KEY);
            _videoID = savedInstanceState.getString(VIDEO_ID_KEY);
            _currentTimeMillis = savedInstanceState.getInt(CURRENT_MILLIS_KEY);
        }
        if(!isInitialized()){
            _fragment = fragment;
            _youtubePlayerFragment = new YouTubePlayerSupportFragment();
            _onInitializedListener = new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                    Log.v("Preview", "On Init Success");
                    _player = youTubePlayer;
                    if (!wasRestored) {
                        Log.v("Preview", "Was not restored");

                        _player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                            @Override
                            public void onFullscreen(boolean isFullscreen) {
                                _fullScreen = isFullscreen;
                                _currentTimeMillis = _player.getCurrentTimeMillis();
                                Log.v("YouTube", "Current Time Millis: " + _currentTimeMillis);
                            }
                        });
                    }
                    _player.setFullscreen(_fullScreen);
                    _player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    _player.cueVideo(_videoID, _currentTimeMillis);
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
                    Log.v("Preview", "YT failure");
                    String errorMessage = errorReason.toString();
                    Toast.makeText(_fragment.getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                }
            };
        } else {
            Log.v("Preview", "Was initialized already");
        }
        // dynamically add the fragment to allow nested fragments
        Log.v("Preview", "Start transaction");
        FragmentTransaction transaction = _fragment.getChildFragmentManager().beginTransaction();
        transaction.add(R.id.postview_video_container, _youtubePlayerFragment, FRAGMENT_PLAYER_TAG);
        //transaction.addToBackStack(null);
        transaction.commit();
        Log.v("Preview", "Init YT Player Fragment");
        _youtubePlayerFragment.initialize(_fragment.getActivity().getString(R.string.youtube_developer_key), _onInitializedListener);
    }

    @Override
    public void saveInstanceState(Bundle outState) {
        outState.putBoolean(FULLSCREEN_KEY, _fullScreen);
        outState.putString(VIDEO_ID_KEY, _videoID);
        outState.putInt(CURRENT_MILLIS_KEY, _currentTimeMillis);
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
