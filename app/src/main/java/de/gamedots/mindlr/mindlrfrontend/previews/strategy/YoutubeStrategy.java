package de.gamedots.mindlr.mindlrfrontend.previews.strategy;

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

public class YoutubeStrategy implements PreviewStrategy, YouTubePlayer.OnInitializedListener{

    private final static String youtube_regex = "https://youtu\\.be/([\\w-_]+)";
    private final static String log_tag = "YouTubePreview";


    private String _videoID;
    private boolean _machted;

    private PostViewFragment _fragment;
    private YouTubePlayerSupportFragment _youtubePlayerFragment;
    private YouTubePlayer _player;


    public YoutubeStrategy(){
        _machted = false;
    }

    @Override
    public boolean match(String url) {
        Matcher matcher = Pattern.compile(youtube_regex).matcher(url);
        if(matcher.find()){
            _videoID = matcher.group(1);
            _machted = true;
            return true;
        }
        _machted = false;
        return false;
    }

    @Override
    public void buildPreviewUI(PostViewFragment fragment) {
        _fragment = fragment;
        if(_machted){
            // create player container fragment
            _youtubePlayerFragment = new YouTubePlayerSupportFragment();

            // dynamically add the fragment to allow nested fragments
            FragmentTransaction transaction = _fragment.getChildFragmentManager().beginTransaction();
            transaction.add(R.id.postview_video_container, _youtubePlayerFragment, FRAGMENT_PLAYER_TAG);
            //transaction.addToBackStack(null);
            transaction.commit();

            _youtubePlayerFragment.initialize(_fragment.getActivity().getString(R.string.youtube_developer_key), this);
        } else {
            Log.v(log_tag, "Cannot build preview, no matching youtube ID");
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer youTubePlayer,
                                        boolean wasRestored) {
        _player = youTubePlayer;
        if (!wasRestored) {
            _player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                @Override
                public void onFullscreen(boolean isFullscreen) {
                    _fragment.setFullScreen(isFullscreen);
                    _player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                }
            });
        }
        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        youTubePlayer.cueVideo(_videoID);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        String errorMessage = errorReason.toString();
        Toast.makeText(_fragment.getActivity(), errorMessage, Toast.LENGTH_LONG).show();
    }

}
