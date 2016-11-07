package de.gamedots.mindlr.mindlrfrontend.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.PostFragment;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostAdapterViewHolder> {

    private Cursor _cursor;
    private TextView _emptyView;
    private final Context _context;


    public PostAdapter(Context context, TextView emptyView) {
        _context = context;
        _emptyView = emptyView;
    }

    public class PostAdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView postTextView;

        public PostAdapterViewHolder(View view) {
            super(view);
            postTextView = (TextView) view.findViewById(R.id.post_preview_text);
        }
    }

    @Override
    public PostAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.recyclerview_item_fav_post,
                viewGroup,
                false);
        view.setFocusable(true);
        return new PostAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostAdapterViewHolder viewHolder, int position) {
        _cursor.moveToPosition(position);

        // read data from cursor and apply to post text content
        viewHolder.postTextView.setText(_cursor.getString(PostFragment.COLUMN_CONTENT_TEXT));
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        if (_cursor != null && _cursor.moveToPosition(position)) {
            return _cursor.getLong(PostFragment.COLUMN_POST_ID);
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        if (null == _cursor) {
            return 0;
        }
        return _cursor.getCount();
    }

    public Cursor getCursor() {
        return _cursor;
    }

    public void swapCursor(Cursor newCursor) {
        _cursor = newCursor;
        notifyDataSetChanged();
        _emptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
