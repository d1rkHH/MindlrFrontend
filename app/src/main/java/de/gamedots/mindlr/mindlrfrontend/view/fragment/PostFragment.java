package de.gamedots.mindlr.mindlrfrontend.view.fragment;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.gamedots.mindlr.mindlrfrontend.MindlrApplication;
import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.adapter.PostAdapter;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserPostEntry;


public class PostFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // unique id for the loader
    public static final int POST_LOADER_ID = 1;

    private RecyclerView _recyclerView;
    private PostAdapter _postAdapter;
    private TextView _emptyView;

    // define post column projection and constants
    public static final String[] POST_COLUMNS = {
            MindlrContract.PostEntry.TABLE_NAME + "." + MindlrContract.PostEntry._ID,
            MindlrContract.ItemEntry.COLUMN_CONTENT_TEXT,
            MindlrContract.ItemEntry.COLUMN_CONTENT_URI
    };

    // Constant for column indices used by the cursor that are tied to the POST_COLUMNS
    public static final int COLUMN_POST_ID = 0;
    public static final int COLUMN_CONTENT_TEXT = 1;
    public static final int COLUMN_CONTENT_URI = 2;


    public PostFragment() {
    }

    // TODO: fill with POJO or query DB
    final static SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextChange(String newSearchText) {
            //final List<UserPostCardItem> filteredPCL = Filter.filter(_items, newSearchText);
            //_rvAdapter.setFilter(filteredPCL);
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }
    };

    final static MenuItemCompat.OnActionExpandListener menuListener = new MenuItemCompat
            .OnActionExpandListener() {
        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            //_rvAdapter.setFilter(_items);
            return true; // Return true to collapse action view
        }

        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            return true; // Return true to expand action view
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // start loading data from db
        getLoaderManager().initLoader(POST_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_post, container, false);
        setHasOptionsMenu(true);

        _recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerviewfavor);

        // set a layout manager
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        _recyclerView.setLayoutManager(layoutManager);
        // set to true when content changes in adapter do not change
        // the layout size of the recycler view to increase performance
        _recyclerView.setHasFixedSize(true);

        _emptyView = (TextView) rootView.findViewById(R.id.recyclerview_post_empty);
        _postAdapter = new PostAdapter(getActivity(), _emptyView);

        // set the adapter onto recycler view
        _recyclerView.setAdapter(_postAdapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_user_posts_fragment, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(searchListener);
        MenuItemCompat.setOnActionExpandListener(item, menuListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long userId = MindlrApplication.User.getId();
        Uri userPostForUserUri = UserPostEntry.buildUserPostWithUserId(userId);

        return new CursorLoader(getActivity(),
                userPostForUserUri,
                POST_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        _postAdapter.swapCursor(cursor);
        if (_postAdapter.getItemCount() == 0) {
            _emptyView.setText("You have not liked any post yet.");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        _postAdapter.swapCursor(null);
    }
}
