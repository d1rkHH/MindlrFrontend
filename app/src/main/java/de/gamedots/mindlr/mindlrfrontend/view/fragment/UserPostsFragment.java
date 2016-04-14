package de.gamedots.mindlr.mindlrfrontend.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.adapter.RVAdapter;
import de.gamedots.mindlr.mindlrfrontend.model.UserPostCardItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserPostsFragment extends Fragment implements SearchView.OnQueryTextListener{

    private RecyclerView _recyclerView;
    private RVAdapter _rvAdapter;
    private List<UserPostCardItem> _items;

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    /* Called when the fragment's activity has been created
         * and this fragment's view hierarchy instantiated
          */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<UserPostCardItem> items = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            items.add(new UserPostCardItem("TextView_" + i));
        }
        _items = items;

        _rvAdapter = new RVAdapter(items);
        _recyclerView.setAdapter(_rvAdapter);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_user_posts_fragment, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        _rvAdapter.setFilter(_items);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
    }

    @Override
    public boolean onQueryTextChange(String newSearchText) {
        final List<UserPostCardItem> filteredPCL = filter(_items, newSearchText);
        _rvAdapter.setFilter(filteredPCL);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    private List<UserPostCardItem> filter(List<UserPostCardItem> models, String query) {
        query = query.toLowerCase();

        final List<UserPostCardItem> filteredModelList = new ArrayList<>();
        for (UserPostCardItem model : models) {
            final String text = model.get_text().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
