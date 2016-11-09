package de.gamedots.mindlr.mindlrfrontend.view.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.controller.PostLoader;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.util.Utility;

import static de.gamedots.mindlr.mindlrfrontend.util.DebugUtil.toast;

/**
 * This class displays a post to the user. Furthermore it handles the
 * swipe interaction and the fragment transaction together with the PostLoader class.
 */
public class PostViewFragment extends Fragment {

    private TextView _postView;


    public PostViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(LOG.AUTH, "onCreateView: postviewfragment recreated with " + (savedInstanceState != null));

        View view = inflater.inflate(R.layout.fragment_post_view, container, false);
        _postView = (TextView) view.findViewById(R.id.postTextView);

        if (PostLoader.getInstance().isInitialized()) {
            String text = PostLoader.getInstance().getCurrent().getContentText();
            text = text.replaceAll(System.getProperty("line.separator"), "");
            _postView.setText(text);
        }

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.post_fragment_container);
        if (linearLayout != null) {
            linearLayout.setOnTouchListener(new OnSwipeTouchListener(getActivity()));
        } else {
            Toast.makeText(getActivity(), "was null", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    public TextView getPostView() {
        return _postView;
    }

    private void fragmentTrans(int animStart, int animEnd) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.setCustomAnimations(animStart, animEnd);
        fragmentTransaction.replace(R.id.main_content, new PostViewFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

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
            //toast(getActivity(), "Upvote");
            PostLoader.getInstance().getCurrent().ratePositive();
            Utility.updatePostVoteType(getActivity(),
                    PostLoader.getInstance().getCurrent().getId(),
                    MindlrContract.UserPostEntry.VOTE_LIKED);

            if (PostLoader.getInstance().next()) {
                fragmentTrans(android.R.anim.fade_in, R.anim.exit_to_right);
            } else {
                toast(getActivity(), "You reached the last post. Swipe again to load new posts.");
            }
        }

        public void onSwipeLeft() {
            // toast(getActivity(), "Downvote");
            PostLoader.getInstance().getCurrent().rateNegative();
            Utility.updatePostVoteType(getActivity(),
                    PostLoader.getInstance().getCurrent().getId(),
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
}
