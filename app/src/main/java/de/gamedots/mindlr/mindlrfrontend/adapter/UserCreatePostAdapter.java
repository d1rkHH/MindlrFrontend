package de.gamedots.mindlr.mindlrfrontend.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract;
import de.gamedots.mindlr.mindlrfrontend.helper.DateFormatHelper;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;
import de.gamedots.mindlr.mindlrfrontend.previews.PreviewStrategyMatcher;
import de.gamedots.mindlr.mindlrfrontend.view.activity.DetailActivity;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.UserPostsFragment;

/**
 * Created by dirk on 26.11.2016.
 */

public class UserCreatePostAdapter extends RecyclerView.Adapter<UserCreatePostAdapter
        .UserCreatePostAdapterViewHolder> {

    private Cursor _cursor;
    private TextView _emptyView;
    private final Context _context;

    public UserCreatePostAdapter(Context context, TextView emptyView) {
        _context = context;
        _emptyView = emptyView;
    }

    public class UserCreatePostAdapterViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {

        private TextView postTextView;
        private TextView submitDate;
        private TextView upvotes;
        private TextView downvotes;
        private ImageView postContentImage;

        public UserCreatePostAdapterViewHolder(View view) {
            super(view);
            postTextView = (TextView) view.findViewById(R.id.usercreatepost_content_textview);
            submitDate = (TextView) view.findViewById(R.id.usercreatepost_date_textview);
            upvotes = (TextView) view.findViewById(R.id.usercreatepost_uppercent_textview);
            downvotes = (TextView) view.findViewById(R.id.usercreatepost_downpercent_textview);
            postContentImage = (ImageView) view.findViewById(R.id.usercreatepost_imageview);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            _cursor.moveToPosition(getAdapterPosition());
            long id = _cursor.getLong(UserPostsFragment.COLUMN_USERCREATEPOST_ID);
            Uri uri = MindlrContract.UserCreatePostEntry.buildUserCreatePostUri(id);
            Intent intent = new Intent(_context, DetailActivity.class);
            intent.setData(uri);
            _context.startActivity(intent);
        }
    }

    @Override
    public UserCreatePostAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.recyclerview_usercreatepost_item,
                viewGroup,
                false);
        view.setFocusable(true);
        return new UserCreatePostAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserCreatePostAdapterViewHolder viewHolder, int position) {
        _cursor.moveToPosition(position);

        // read data from cursor and apply to post text content
        viewHolder.postTextView.setText(_cursor.getString(UserPostsFragment.COLUMN_CONTENT_TEXT));

        // create ViewPost form cursor and determine and apply preview strategy
        List<View> availablePreviewViews = new ArrayList<>();
        availablePreviewViews.add(viewHolder.postContentImage);
        PreviewStrategyMatcher
                .getInstance()
                .matchStrategy(ViewPost.fromCursor(_cursor))
                .buildPreviewUI(_context, availablePreviewViews);

        // read date millis from cursor and get day and month using calendar object
        long dateMillis = _cursor.getLong(UserPostsFragment.COLUMN_SUBMIT_DATE);
        String dateFormat = DateFormatHelper.getFormattedDateString(
                _context, dateMillis, DateFormatHelper.WRITE_FORMAT);
        viewHolder.submitDate.setText(dateFormat);

        // format upvote/downvote percentage
        int upvotes = _cursor.getInt(UserPostsFragment.COLUMN_UPVOTES);
        int downvotes = _cursor.getInt(UserPostsFragment.COLUMN_DOWNVOTES);
        Log.v(LOG.AUTH, "UP: " +  upvotes + " DOWN: " + downvotes);
        int total = upvotes + downvotes;
        float uppercent;
        float downpercent;
        if (total == 0){
            uppercent = 0;
            downpercent = 0;
        } else {
            uppercent = (int)((upvotes/(total * 1.0)) * 100);
            downpercent = (int)((downvotes/(total * 1.0)) * 100);
        }
        Log.v(LOG.AUTH, "UP: " +  uppercent + " DOWN: " + downpercent);
        // read uppercent from cursor
        viewHolder.upvotes.setText(String.format(_context.getString(R.string.format_vote_percentage),
                uppercent));

        // read downpercent from cursor
        viewHolder.downvotes.setText(String.format(_context.getString(R.string.format_vote_percentage),
                downpercent));
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        if (_cursor != null && _cursor.moveToPosition(position)) {
            return _cursor.getLong(UserPostsFragment.COLUMN_USERCREATEPOST_ID);
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
