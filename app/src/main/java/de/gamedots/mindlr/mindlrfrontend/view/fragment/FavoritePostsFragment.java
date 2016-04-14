package de.gamedots.mindlr.mindlrfrontend.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.gamedots.mindlr.mindlrfrontend.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritePostsFragment extends Fragment {


    public FavoritePostsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_posts, container, false);
    }

}
