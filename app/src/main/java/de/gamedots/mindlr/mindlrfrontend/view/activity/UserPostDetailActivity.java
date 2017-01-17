package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.util.ArrayList;
import java.util.List;

import de.gamedots.mindlr.mindlrfrontend.MindlrApplication;
import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserCreatePostEntry;
import de.gamedots.mindlr.mindlrfrontend.helper.DateFormatHelper;
import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;
import de.gamedots.mindlr.mindlrfrontend.previews.PreviewStrategyMatcher;
import de.gamedots.mindlr.mindlrfrontend.previews.strategy.YoutubeStrategy;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.UserPostsFragment;

import static de.gamedots.mindlr.mindlrfrontend.view.fragment.UserPostsFragment.USERCREATEPOST_COLUMNS;

public class UserPostDetailActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks<Cursor>, YoutubeStrategy.YoutubeHandler{

    public static final int USERPOST_LOADER_ID = 8;
    private static final int RECOVERY_DIALOG_REQUEST = 22;
    private TextView _detailUpvotes;
    private TextView _detailDownVotes;
    private TextView _detailDateView;
    private ImageView _detailImage;

    private TextView _detailContentText;
    private YouTubePlayerFragment _playerFragment;
    private FrameLayout _detailVideoContainer;
    private String _videoID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post_detail);

        setupToolbar();
        bindViews();

        // load usercreatepost from db
        getSupportLoaderManager().initLoader(USERPOST_LOADER_ID, null, this);

        // delay enter transition until image loaded with glide
        supportPostponeEnterTransition();
        _detailImage.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        _detailImage.getViewTreeObserver().removeOnPreDrawListener(this);
                        supportStartPostponedEnterTransition();
                        return true;
                    }
                }
        );
    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.TAP_SELECT_KEY, ProfileActivity.TAP_USERPOST);
        return intent;
    }

    private void setupToolbar() {
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void bindViews() {
        _detailImage = (ImageView)findViewById(R.id.detail_imageview);
        _detailContentText = (TextView)findViewById(R.id.detail_content_text);
        _detailVideoContainer = (FrameLayout)findViewById(R.id.detail_video_container);
        _playerFragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.detail_youtubeplayer_fragment);

        _detailUpvotes = (TextView)findViewById(R.id.usercreatepost_uppercent_textview);
        _detailDownVotes = (TextView)findViewById(R.id.usercreatepost_downpercent_textview);
        _detailDateView = (TextView)findViewById(R.id.usercreatepost_date_textview);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri data = getIntent().getData();
        if (data == null) {
            return null;
        }
        // user_id = ?
        String selection = UserCreatePostEntry.COLUMN_USER_KEY + " = ? ";
        ArrayList<String> selArgs = new ArrayList<>();
        selArgs.add(Long.toString(MindlrApplication.User.getId()));

        selection += " AND " + UserCreatePostEntry.TABLE_NAME + "." + UserCreatePostEntry._ID + " = ? ";
        selArgs.add(UserCreatePostEntry.getIdPathFromUri(data));

        return new CursorLoader(this,
                UserCreatePostEntry.CONTENT_URI,
                USERCREATEPOST_COLUMNS,
                selection,
                selArgs.toArray(new String[selArgs.size()]),
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || !cursor.moveToFirst()) {
            return;
        }

        // read data from cursor and apply to post text content
        _detailContentText.setText(cursor.getString(UserPostsFragment.COLUMN_CONTENT_TEXT));

        // create ViewPost form cursor and determine and apply preview strategy
        List<View> availablePreviewViews = new ArrayList<>();
        availablePreviewViews.add(_detailImage);
        PreviewStrategyMatcher
                .getInstance()
                .matchStrategy(ViewPost.fromCursor(cursor))
                .buildPreviewUI(this, availablePreviewViews);

        // read date millis from cursor and get day and month using calendar object
        long dateMillis = cursor.getLong(UserPostsFragment.COLUMN_SUBMIT_DATE);
        String formatDate = DateFormatHelper.getFullDateString(dateMillis);
        _detailDateView.setText(formatDate);

        // format upvote/downvote percentage
        int upvotes = cursor.getInt(UserPostsFragment.COLUMN_UPVOTES);
        int downvotes = cursor.getInt(UserPostsFragment.COLUMN_DOWNVOTES);
        int total = upvotes + downvotes;
        float uppercent;
        float downpercent;
        if (total == 0){
            uppercent = 0;
            downpercent = 0;
        } else {
            uppercent = (int)((upvotes/(total * 1.0)) * 100);
            downpercent = (int)((downvotes/(total * 1.0)) * 100);
        }

        // read uppercent from cursor
        _detailUpvotes.setText(String.format(getString(R.string.format_vote_percentage), uppercent));
        // read downpercent from cursor
        _detailDownVotes.setText(String.format(getString(R.string.format_vote_percentage), downpercent));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
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
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult
            errorReason) {
        if (errorReason.isUserRecoverableError()) {
            if (!_playerFragment.isHidden()) {
                errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
            }
        } else {
            String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }
}
