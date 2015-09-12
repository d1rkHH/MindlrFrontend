package de.gamedots.mindlr.mindlrfrontend;


import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class PostViewFragment extends Fragment {

    private TextView postView;
    private boolean toggle = false; // TODO: remove toggle for real logic
    private View view;
    private static final int offset = 56;
    private ImageView favorStar;


    public PostViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_post_view, container, false);
        postView = (TextView) view.findViewById(R.id.postTextView);
        favorStar = (ImageView) view.findViewById(R.id.favorizeIcon);
        favorStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavorClick(v);
            }
        });

        if (postView != null) {
            postView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
                public void onSwipeRight() {
                    //TODO: post collection, maximum swipe back
                    //postView.startAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.textview_left_to_right));
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                    fragmentTransaction.replace(R.id.activity_content, new PostViewFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    Toast.makeText(getActivity(), "RIght", Toast.LENGTH_SHORT).show();

                }

                public void onSwipeLeft() {
                    //TODO: post collection, maximum swipe forward -> reloading
                    // postView.startAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.textview_right_to_left));
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                    fragmentTransaction.replace(R.id.activity_content, new PostViewFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    Toast.makeText(getActivity(), "Left", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSwipeTop() {
                    Toast.makeText(getActivity(), "UP", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSwipeBottom() {
                    Toast.makeText(getActivity(), "DOWN", Toast.LENGTH_SHORT).show();

                }
            });
        }

        return view;
    }

    public void onFavorClick(View view) {

        //TODO: real favor function (including animation?)
        if (!toggle) {
            favorStar.setImageResource(R.drawable.star32);
            toggle = !toggle;

            // TODO: clean animation test on all apis, extract for clean code
            Display mdisp = getActivity().getWindowManager().getDefaultDisplay();
            Point mdispSize = new Point();
            mdisp.getSize(mdispSize);
            int maxX = mdispSize.x;

            int[] coords = {0, 0};
            favorStar.getLocationOnScreen(coords);
            int x = coords[0];
            int y = coords[1];
            TranslateAnimation animation = new TranslateAnimation(0, maxX - x - offset, 0, -y + offset);
            animation.setDuration(350);
            favorStar.setAnimation(animation);
            animation.start();
        } else {
            favorStar.setImageResource(R.drawable.outstar32);
            toggle = !toggle;
        }
    }


}
