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
import android.support.v7.widget.LinearLayoutManager;
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
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.ItemEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserPostEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrProvider;


public class PostFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // unique id for the loader
    public static final int POST_LOADER_ID = 1;
    public static final String SEARCH_QUERY_KEY = "search_key";

    private RecyclerView _recyclerView;
    private PostAdapter _postAdapter;
    private TextView _emptyView;

    private boolean _isInGridLayout;

    // define post column projection and constants
    public static final String[] POST_COLUMNS = {
            MindlrContract.PostEntry.TABLE_NAME + "." + MindlrContract.PostEntry._ID,
            MindlrContract.PostEntry.COLUMN_SERVER_ID,
            ItemEntry.COLUMN_CONTENT_TEXT,
            ItemEntry.COLUMN_CONTENT_URI,
            UserPostEntry.COLUMN_VOTE_DATE
    };

    // Constant for column indices used by the cursor that are tied to the POST_COLUMNS
    public static final int COLUMN_POST_ID = 0;
    public static final int COLUMN_POST_SERVER_ID = 1;
    public static final int COLUMN_CONTENT_TEXT = 2;
    public static final int COLUMN_CONTENT_URI = 3;
    public static final int COLUMN_VOTE_DATE = 4;


    public PostFragment() {
    }

    // TODO: fill with POJO or query DB
    final SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextChange(String newSearchText) {
            reloadAndApplyData(newSearchText);
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            reloadAndApplyData(query);
            return true;
        }
    };

    private void reloadAndApplyData(String newSearchText) {
        Bundle args = new Bundle();
        args.putString(SEARCH_QUERY_KEY, newSearchText);
        getLoaderManager().restartLoader(POST_LOADER_ID, args, this);
    }

    final MenuItemCompat.OnActionExpandListener menuListener = new MenuItemCompat
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
        _isInGridLayout = true;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_layout:
                return handleLayoutChange();
            case R.id.action_layout_back:
                return handleLayoutChange();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean handleLayoutChange(){
        if (_isInGridLayout) {
            _recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            _isInGridLayout = false;
            getActivity().invalidateOptionsMenu();
        } else{
            _recyclerView.setLayoutManager(
                    new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            _isInGridLayout = true;
            getActivity().invalidateOptionsMenu();

        }
        _recyclerView.setAdapter(_postAdapter);
        return true;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_layout).setVisible(!_isInGridLayout);
        menu.findItem(R.id.action_layout_back).setVisible(_isInGridLayout);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long userId = MindlrApplication.User.getId();
        Uri userPostForUserUri = UserPostEntry.buildUserPostWithUserId(userId);
        String selection = MindlrProvider.sUserPostForIdSelection;
        String[] selArgs = null;
        if (args != null && !args.getString(SEARCH_QUERY_KEY).isEmpty()){
            String query = args.getString(SEARCH_QUERY_KEY);
            selection += " AND " + ItemEntry.COLUMN_CONTENT_TEXT + " LIKE ?";
            selArgs = new String[]{
                    Long.toString(userId),
                    Integer.toString(UserPostEntry.VOTE_LIKED),
                    "%" + query + "%"
            };
        }

        return new CursorLoader(getActivity(),
                userPostForUserUri,
                POST_COLUMNS,
                selection,
                selArgs,
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
