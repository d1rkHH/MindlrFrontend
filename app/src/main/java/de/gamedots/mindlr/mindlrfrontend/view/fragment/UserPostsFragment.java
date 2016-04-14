package de.gamedots.mindlr.mindlrfrontend.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.adapter.RVAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserPostsFragment extends Fragment {

    private RecyclerView _recyclerView;

    public UserPostsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_posts, container, false);

        _recyclerView  = (RecyclerView) view.findViewById(R.id.recyclerview);

        // a RecyclerView needs a LayoutManager to manage the positioning of its items
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        _recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    /* Called when the fragment's activity has been created
     * and this fragment's view hierarchy instantiated
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<String> items = new ArrayList<String>();
        for (int i = 0; i < 50; i++) {
            items.add("TextView_" + i);
        }

        RVAdapter adapter = new RVAdapter(items);
        _recyclerView.setAdapter(adapter);

    }

}
