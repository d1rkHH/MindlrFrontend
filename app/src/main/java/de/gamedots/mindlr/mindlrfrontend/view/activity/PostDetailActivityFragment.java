package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.gamedots.mindlr.mindlrfrontend.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PostDetailActivityFragment extends Fragment {

    public PostDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();

        return inflater.inflate(R.layout.fragment_post_detail, container, false);
    }
}
