package de.gamedots.mindlr.mindlrfrontend.view.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.gamedots.mindlr.mindlrfrontend.MindlrApplication;
import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.adapter.DraftsAdapter;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserCreatePostEntry;

/**
 * Created by dirk on 08.11.2016.
 */

public class DraftsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int USER_CREATE_POST_LOADER_ID = 2;

    private RecyclerView _recyclerView;
    private DraftsAdapter _draftsAdapter;
    private TextView _emptyView;

    public static final String[] USER_CREATE_POST_COLUMNS = {
            UserCreatePostEntry.TABLE_NAME + "." + UserCreatePostEntry._ID,
            UserCreatePostEntry.COLUMN_CONTENT_TEXT,
            UserCreatePostEntry.COLUMN_CONTENT_URI
    };

    public static final int COLUMN_USER_CREATE_POST_ID = 0;
    public static final int COLUMN_CONTENT_TEXT = 1;
    public static final int COLUMN_CONTENT_URI = 2;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(USER_CREATE_POST_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_drafts, container, false);

        _emptyView = (TextView) rootView.findViewById(R.id.recyclerview_draft_empty);
        _recyclerView = (RecyclerView) rootView.findViewById(R.id.drafts_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        _recyclerView.setLayoutManager(layoutManager);

        _draftsAdapter = new DraftsAdapter(getActivity(), _emptyView);
        _recyclerView.setAdapter(_draftsAdapter);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // load all user created post that where stored as drafts
        return new CursorLoader(getActivity(),
                UserCreatePostEntry.CONTENT_URI,
                USER_CREATE_POST_COLUMNS,
                UserCreatePostEntry.COLUMN_IS_DRAFT + " = ? AND " +
                UserCreatePostEntry.COLUMN_USER_KEY + " = ? ",
                new String[]{"1", Long.toString(MindlrApplication.User.getId())},
                UserCreatePostEntry.COLUMN_SUBMIT_DATE + " DESC "
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        _draftsAdapter.swapCursor(cursor);
        if (_draftsAdapter.getItemCount() == 0) {
            _emptyView.setText("You have no drafts stored.");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        _draftsAdapter.swapCursor(null);
    }
}
