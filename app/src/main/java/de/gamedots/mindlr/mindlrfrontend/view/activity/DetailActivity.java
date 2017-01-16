package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.model.ImageUploadResult;
import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;
import de.gamedots.mindlr.mindlrfrontend.previews.PreviewStrategyMatcher;
import de.gamedots.mindlr.mindlrfrontend.previews.strategy.PreviewStrategy;
import de.gamedots.mindlr.mindlrfrontend.previews.strategy.YoutubeStrategy;
import de.gamedots.mindlr.mindlrfrontend.util.Utility;

import static de.gamedots.mindlr.mindlrfrontend.view.fragment.PostFragment.LAYOUT_KEY;

/**
 * Created by Dirk on 08.01.17.
 */

public class DetailActivity extends AppCompatActivity implements YoutubeStrategy.YoutubeHandler {

    public static final String LIKED_EXTRA = "liked_extra";
    public static final String LAUNCHED_FROM_ATTACHEMENT_KEY = "launched_from_extra";
    public static final String POST_EXTRA = "post_extra";
    private static final int RECOVERY_DIALOG_REQUEST = 140;
    private int tapSelectionOnNavigation;
    private boolean profileWasInGridLayout;
    private Class upDestination;

    /* Strategy to find and prepare preview content to display */
    private PreviewStrategy _previewStrategy;

    private ImageView _detailImage;
    private TextView _detailContentText;
    private YouTubePlayerFragment _playerFragment;
    private FrameLayout _detailVideoContainer;
    private String _videoID;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setupToolbar();
        bindViews();

        switch (getIntent().getIntExtra(LAUNCHED_FROM_ATTACHEMENT_KEY, 0)){
            case 0:
                upDestination = MainActivity.class;
                break;
            case 1:
                upDestination = ProfileActivity.class;
                tapSelectionOnNavigation = ProfileActivity.TAP_LIKED;
                profileWasInGridLayout = getIntent().getBooleanExtra(LAYOUT_KEY, false);
                break;
        }

        Intent launchIntent = getIntent();
        if (launchIntent != null && launchIntent.getExtras().containsKey(POST_EXTRA)) {
            ViewPost vp = launchIntent.getParcelableExtra(POST_EXTRA);
            initializeViews(vp);
        }
    }

    private void bindViews() {
        _detailImage = (ImageView)findViewById(R.id.detail_imageview);
        _detailContentText = (TextView)findViewById(R.id.detail_content_text);
        _detailVideoContainer = (FrameLayout)findViewById(R.id.detail_video_container);
        _playerFragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.detail_youtubeplayer_fragment);
    }

    private void initializeViews(ViewPost vp) {
        if (vp != null){
            /**
             * Set Content Text from post
             */
            if (vp.getContentText() != null) {
                _detailContentText.setText(vp.getContentText());
            }

            /**
             * Find Preview Strategy to display additional content.
             * Collect all views that are candidates for the preview
             * and pass the to the build method of the matched preview strategy.
             */
            List<View> availableViews = new ArrayList<>();
            availableViews.add(_detailImage);
            availableViews.add(_detailContentText);

            if(_previewStrategy != null){
                _previewStrategy.buildPreviewUI(this, availableViews);
            } else {
                _previewStrategy = PreviewStrategyMatcher.getInstance().matchStrategy(vp).getCopy();
                _previewStrategy.buildPreviewUI(this, availableViews);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImageUploadResultEvent(ImageUploadResult event) {
        Utility.handleImageResult(event, this);
    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        Intent intent = new Intent(this, upDestination);
        intent.putExtra(ProfileActivity.TAP_SELECT_KEY, tapSelectionOnNavigation);
        intent.putExtra(LAYOUT_KEY, profileWasInGridLayout);
        return intent;
    }

    private void setupToolbar(){
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer,
                                        boolean wasRestored) {
        if (!wasRestored){
            if (_videoID!= null && youTubePlayer != null) {
                youTubePlayer.cueVideo(_videoID);
            }
        } else {
            youTubePlayer.play();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            if (!_playerFragment.isHidden()) {
                errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
            }
        } else {
            String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            _playerFragment.initialize(DetailActivity.this.getString(R.string.youtube_developer_key), this);
        }
    }

    @Override
    public YouTubePlayer.Provider getYouTubePlayerProvider() {
        return _playerFragment;
    }

    @Override
    public void setVideoID(String id) {
        _detailVideoContainer.setVisibility(View.VISIBLE);
        _videoID = id;
    }
}
