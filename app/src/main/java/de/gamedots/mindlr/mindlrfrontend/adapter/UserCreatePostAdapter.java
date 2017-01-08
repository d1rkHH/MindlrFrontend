package de.gamedots.mindlr.mindlrfrontend.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.gamedots.mindlr.mindlrfrontend.R;
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

    public class UserCreatePostAdapterViewHolder extends RecyclerView.ViewHolder {

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

        // TODO: handle content uri
        // read uri from cursor and load into imageview
        Glide.with(_context)
                .load(Uri.parse(_cursor.getString(UserPostsFragment.COLUMN_CONTENT_URI)))
                .fitCenter()
                .into(viewHolder.postContentImage);

        //TODO: utility format date Today, Yesterday, 5. Nov. + string res formatter
        // read date millis from cursor and get day and month using calendar object
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(_cursor.getLong(UserPostsFragment.COLUMN_SUBMIT_DATE));
        SimpleDateFormat month_date = new SimpleDateFormat("dd MMM",
                _context.getResources().getConfiguration().locale);
        String dayWithMonth = month_date.format(cal.getTime());
        viewHolder.submitDate.setText(dayWithMonth);

        // read uppercent from cursor
        viewHolder.upvotes.setText(String.format(_context.getString(R.string.format_vote_percentage),
                _cursor.getFloat(UserPostsFragment.COLUMN_UPVOTES)));

        // read downpercent from cursor
        viewHolder.downvotes.setText(String.format(_context.getString(R.string.format_vote_percentage),
                _cursor.getFloat(UserPostsFragment.COLUMN_DOWNVOTES)));
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
