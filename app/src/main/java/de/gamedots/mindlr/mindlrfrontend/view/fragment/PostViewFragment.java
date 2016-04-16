package de.gamedots.mindlr.mindlrfrontend.view.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import static de.gamedots.mindlr.mindlrfrontend.util.DebugUtil.*;

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

        View view = inflater.inflate(R.layout.fragment_post_view, container, false);
        _postView = (TextView) view.findViewById(R.id.postTextView);

        if (PostLoader.getInstance().isInitialized()) {
            _postView.setText(PostLoader.getInstance().getCurrent().getContentText());
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
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
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
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                        }
                        result = true;
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
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
            Toast.makeText(getActivity(), "right", Toast.LENGTH_SHORT).show();

            // execute previous method and play animation if successful
            if (PostLoader.getInstance().previous()) {
                fragmentTrans(R.anim.enter_from_left, R.anim.exit_to_right);
            } else {
                Toast.makeText(getActivity(), "No older posts available", Toast.LENGTH_SHORT).show();
            }
        }

        public void onSwipeLeft() {
            // execute next method and play animation if successful
            if (PostLoader.getInstance().next()) {
                fragmentTrans(R.anim.enter_from_right, R.anim.exit_to_left);
            } else {
                toast(getActivity(), "You reached the last post. Swipe again to load new posts.");
            }
        }

        public void onSwipeTop() {
            toast(getActivity(), "UP");
            PostLoader.getInstance().getCurrent().ratePositive();
        }

        public void onSwipeBottom() {
            toast(getActivity(), "DOWN");
            PostLoader.getInstance().getCurrent().rateNegative();
        }

        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }
    }
}