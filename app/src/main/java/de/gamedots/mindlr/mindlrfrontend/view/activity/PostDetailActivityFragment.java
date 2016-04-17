package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.gamedots.mindlr.mindlrfrontend.R;

import static de.gamedots.mindlr.mindlrfrontend.view.activity.UserPostsActivity.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class PostDetailActivityFragment extends Fragment {

    public PostDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = null;

        //TODO: create second fragment and inflate the appropriate one using fragment transaction inside the DetailActivity
        Intent intent = getActivity().getIntent();
        if(intent != null){
            if(intent.getIntExtra(EXTRA_LAYOUT, -1) == 0) {
                root = inflater.inflate(R.layout.fragment_post_detail, container, false);
                ((TextView) root.findViewById(R.id.up_percent)).setText(intent.getStringExtra(EXTRA_UPPERCENT));
                ((TextView) root.findViewById(R.id.down_percent)).setText(intent.getStringExtra(EXTRA_DOWNPERCENT));
                ((TextView) root.findViewById(R.id.create_date)).setText(intent.getStringExtra(EXTRA_CREATEDATE));
                ((TextView) root.findViewById(R.id.favorite_number)).setText(intent.getStringExtra(EXTRA_CATEGORY));

            }else{
                root = inflater.inflate(R.layout.fragment_post_detail_favor, container, false);
            }
            ((TextView) root.findViewById(R.id.postTextView)).setText(intent.getStringExtra(EXTRA_PREVIEW));
        }
        return root;
    }
}
