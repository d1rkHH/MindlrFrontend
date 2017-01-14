package de.gamedots.mindlr.mindlrfrontend.view.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.controller.PostLoader;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract;
import de.gamedots.mindlr.mindlrfrontend.helper.UriHelper;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;
import de.gamedots.mindlr.mindlrfrontend.previews.PreviewStrategyMatcher;
import de.gamedots.mindlr.mindlrfrontend.previews.strategy.PreviewStrategy;
import de.gamedots.mindlr.mindlrfrontend.util.Utility;

import static de.gamedots.mindlr.mindlrfrontend.util.DebugUtil.toast;

/**
 * This class displays a post to the user. Furthermore it handles the
 * swipe interaction and the fragment transaction together with the PostLoader class.
 */
public class PostViewFragment extends Fragment {

    public static final String DETAIL_EXTRA = "detail_extra";
    public static final String POST_EXTRA = "post_extra";
    public static final String FULLSCREEN_KEY = "fullscreen_key";
    /* Unique identifier for the current player fragment */
    public static final String FRAGMENT_PLAYER_TAG = "de.gamedots.mindlrfrontend.PostViewFragment";
    private static final String VIDEO_ID_KEY = "video_id_tag";

    private TextView _postView;

    private PreviewStrategy _previewStrategy;

    private boolean _fullScreen;
    private String _videoID;


    public PostViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(_previewStrategy != null){
            // Recreate preview
        }

        Log.d(LOG.AUTH, "onCreateView: postviewfragment recreated with " + (savedInstanceState != null));
        // TODO: Move this code to strategies
        // check if recreated due to activity configuration change and read fullscreen information
        if(savedInstanceState != null && savedInstanceState.containsKey(FULLSCREEN_KEY)){
            _fullScreen = savedInstanceState.getBoolean(FULLSCREEN_KEY);
            if (_fullScreen) {
                // create player container fragment
                //_youtubePlayerFragment = new YouTubePlayerSupportFragment();

                // dynamically add the fragment to allow nested fragments
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                //transaction.add(R.id.postview_video_container, _youtubePlayerFragment, FRAGMENT_PLAYER_TAG);
                //transaction.addToBackStack(null);
                transaction.commit();

                _videoID = savedInstanceState.getString(VIDEO_ID_KEY);
                //_youtubePlayerFragment.initialize(getActivity().getString(R.string.youtube_developer_key),this);
            }
        }

        View view;
        boolean isDetail = getArguments() != null && getArguments().containsKey(DETAIL_EXTRA);
        if (isDetail) {
            view = inflater.inflate(R.layout.fragment_detail_post_view, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_post_view, container, false);

        }
        _postView = (TextView) view.findViewById(R.id.postTextView);


        //TODO: remove after testing
        //setViewValues(new ViewPost(5, "hallo", "https://youtu.be/Y7kUG_PiTXc"));

        if (isDetail) {
            Intent launchIntent = getActivity().getIntent();
            if (launchIntent != null && launchIntent.getExtras().containsKey(POST_EXTRA)) {
                ViewPost vp = launchIntent.getParcelableExtra(POST_EXTRA);
                setViewValues(vp);
            }
        } else {
            if (PostLoader.getInstance().isInitialized()) {
                setViewValues(PostLoader.getInstance().getCurrent());
            }
        }

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.post_fragment_container);
        if (linearLayout != null) {
            linearLayout.setOnTouchListener(new OnSwipeTouchListener(getActivity()));
        } else {
            Toast.makeText(getActivity(), "was null", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void setViewValues(ViewPost vp) {
        _previewStrategy = PreviewStrategyMatcher.getInstance().matchStrategy(vp);
        _previewStrategy.buildPreviewUI(this);
        // set post content text
        String postText = vp.getContentText();
        postText = postText.replaceAll(System.getProperty("line.separator"), "");
        _postView.setText(postText);
    }

    public TextView getPostView() {
        return _postView;
    }

    public boolean isPlayerFullscreen(){
        return _fullScreen;
    }

    public void setPlayerFullscreen(boolean fullscreen){
        _fullScreen = fullscreen;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(FULLSCREEN_KEY, _fullScreen);
        outState.putString(VIDEO_ID_KEY, _videoID);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG.AUTH, "postview fragment destroyed");
    }

    // region touch handler
    private class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context ctx) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) >
                                SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                        }
                        result = true;
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) >
                            SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            //onSwipeBottom();
                        } else {
                            //onSwipeTop();
                        }
                    }
                    result = true;

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {
            toast(getActivity(), "Upvote");
            PostLoader.getInstance().getCurrent().ratePositive();
            Utility.updatePostVoteType(getActivity(),
                    PostLoader.getInstance().getCurrent().getServerId(),
                    MindlrContract.UserPostEntry.VOTE_LIKED);

            if (PostLoader.getInstance().next()) {
                fragmentTrans(android.R.anim.fade_in, R.anim.exit_to_right);
            } else {
                toast(getActivity(), "You reached the last post. Swipe again to load new posts.");
            }
        }

        public void onSwipeLeft() {
            toast(getActivity(), "Downvote");
            PostLoader.getInstance().getCurrent().rateNegative();
            Utility.updatePostVoteType(getActivity(),
                    PostLoader.getInstance().getCurrent().getServerId(),
                    MindlrContract.UserPostEntry.VOTE_DISLIKED);

            if (PostLoader.getInstance().next()) {
                fragmentTrans(android.R.anim.fade_in, R.anim.exit_to_left);
            } else {
                toast(getActivity(), "You reached the last post. Swipe again to load new posts.");
            }
        }

        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }
    }

    // endregion

    private void fragmentTrans(int animStart, int animEnd) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.setCustomAnimations(animStart, animEnd);
        fragmentTransaction.replace(R.id.main_content, new PostViewFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public boolean isFullScreen(){
        return _fullScreen;
    }

    public void setFullScreen(boolean fullScreen){
        _fullScreen = fullScreen;
    }
}
