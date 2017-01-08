package de.gamedots.mindlr.mindlrfrontend.view.fragment;


import android.database.Cursor;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.gamedots.mindlr.mindlrfrontend.MindlrApplication;
import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.adapter.UserCreatePostAdapter;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.ItemEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserCreatePostEntry;

public class UserPostsFragment extends Fragment implements SearchView.OnQueryTextListener, LoaderManager
        .LoaderCallbacks<Cursor> {

    private RecyclerView _recyclerView;
    private UserCreatePostAdapter _adapter;
    private TextView _emptyText;

    public UserPostsFragment() {
    }

    public static final int USERCREATE_POST_LOADER_ID = 4;

    // region projection and columns
    // define usercreatepost column projection and constants
    public static final String[] USERCREATEPOST_COLUMNS = {
            UserCreatePostEntry.TABLE_NAME + "." + UserCreatePostEntry._ID,
            ItemEntry.COLUMN_CONTENT_TEXT,
            ItemEntry.COLUMN_CONTENT_URI,
            UserCreatePostEntry.COLUMN_SUBMIT_DATE,
            UserCreatePostEntry.COLUMN_UPVOTES,
            UserCreatePostEntry.COLUMN_DOWNVOTES
    };

    // Constant for column indices used by the cursor that are tied to the USERCREATEPOST_COLUMNS
    public static final int COLUMN_USERCREATEPOST_ID = 0;
    public static final int COLUMN_CONTENT_TEXT = 1;
    public static final int COLUMN_CONTENT_URI = 2;
    public static final int COLUMN_SUBMIT_DATE = 3;
    public static final int COLUMN_UPVOTES = 4;
    public static final int COLUMN_DOWNVOTES = 5;
    // endregion

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // start loading data from db
        getLoaderManager().initLoader(USERCREATE_POST_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_posts, container, false);

        _recyclerView = (RecyclerView) view.findViewById(R.id.usercreatepost_recyclerview);
        _emptyText = (TextView) view.findViewById(R.id.recyclerview_usercreatepost_empty);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        _recyclerView.setLayoutManager(layoutManager);

        _adapter = new UserCreatePostAdapter(getActivity(), _emptyText);
        _recyclerView.setAdapter(_adapter);

        return view;
    }

    // region search setup and listener
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
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true; // Return true to expand action view
                    }
                });
    }

    @Override
    public boolean onQueryTextChange(String newSearchText) {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
    //endregion

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // user_id = ?
        String selection = UserCreatePostEntry.COLUMN_USER_KEY + " = ? ";

        return new CursorLoader(getActivity(),
                UserCreatePostEntry.CONTENT_URI,
                USERCREATEPOST_COLUMNS,
                selection,
                new String[]{Long.toString(MindlrApplication.User.getId())},
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        _adapter.swapCursor(cursor);
        if (_adapter.getItemCount() == 0) {
            _emptyText.setText("You have not written any posts yet.");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        _adapter.swapCursor(null);
    }
}
