package de.gamedots.mindlr.mindlrfrontend.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.helper.DateFormatHelper;
import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;
import de.gamedots.mindlr.mindlrfrontend.previews.PreviewStrategyMatcher;
import de.gamedots.mindlr.mindlrfrontend.view.activity.DetailActivity;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.PostFragment;

import static de.gamedots.mindlr.mindlrfrontend.view.activity.DetailActivity.LAUNCHED_FROM_ATTACHEMENT_KEY;
import static de.gamedots.mindlr.mindlrfrontend.view.activity.DetailActivity.LIKED_EXTRA;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostAdapterViewHolder> {

    private Cursor _cursor;
    private TextView _emptyView;
    private final Context _context;

    public interface PostOnClickHandler{
        void onClick(Intent intent);
    }
    private PostOnClickHandler _clickHandler;


    public PostAdapter(Context context, TextView emptyView, PostOnClickHandler handler) {
        _context = context;
        _emptyView = emptyView;
        _clickHandler = handler;
    }

    public class PostAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView postTextView;
        private ImageView postImageView;
        private TextView postLikedDateView;

        public PostAdapterViewHolder(View view) {
            super(view);
            postTextView = (TextView) view.findViewById(R.id.post_preview_text);
            postImageView = (ImageView)view.findViewById(R.id.post_preview_imageview);
            postLikedDateView = (TextView)view.findViewById(R.id.post_preview_liked_date);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            _cursor.moveToPosition(getAdapterPosition());
            ViewPost vp = ViewPost.fromCursor(_cursor);
            Intent intent = new Intent(_context, DetailActivity.class);
            intent.putExtra(LIKED_EXTRA, true);
            intent.putExtra(LAUNCHED_FROM_ATTACHEMENT_KEY, 1);
            intent.putExtra(DetailActivity.POST_EXTRA, vp);
            _clickHandler.onClick(intent);
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
    public void onBindViewHolder(final PostAdapterViewHolder viewHolder, int position) {
        _cursor.moveToPosition(position);

        // read data from cursor and apply to post text content
        viewHolder.postTextView.setText(_cursor.getString(PostFragment.COLUMN_CONTENT_TEXT));

        // handle different content uri types
        // create ViewPost form cursor and determine and apply preview strategy
        List<View> availablePreviewViews = new ArrayList<>();
        availablePreviewViews.add(viewHolder.postImageView);
        PreviewStrategyMatcher
                .getInstance()
                .matchStrategy(ViewPost.fromCursor(_cursor))
                .buildPreviewUI(_context, availablePreviewViews);

        // read date millis from cursor and create formatted string
        long dateMillis = _cursor.getLong(PostFragment.COLUMN_VOTE_DATE);
        String dateFormat = DateFormatHelper.getFormattedDateString(
                _context, dateMillis, DateFormatHelper.LIKE_FORMAT);
        viewHolder.postLikedDateView.setText(dateFormat);
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
