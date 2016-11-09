package de.gamedots.mindlr.mindlrfrontend.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract;
import de.gamedots.mindlr.mindlrfrontend.view.activity.WritePostActivity;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.DraftsFragment;


public class DraftsAdapter extends RecyclerView.Adapter<DraftsAdapter.DraftsAdapterViewHolder> {

    private Cursor _cursor;
    private TextView _emptyView;
    private final Context _context;


    public DraftsAdapter(Context context, TextView emptyView) {
        _context = context;
        _emptyView = emptyView;
    }

    public class DraftsAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private TextView postContentText;
        private ImageView postContentImage;

        public DraftsAdapterViewHolder(View itemView) {
            super(itemView);

            postContentText = (TextView) itemView.findViewById(R.id.drafts_recyclerview_textview);
            postContentImage = (ImageView) itemView.findViewById(R.id.drafts_recyclerview_imageview);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            _cursor.moveToPosition(adapterPosition);
            Intent intent = new Intent(_context, WritePostActivity.class);

            // build uri with id and launch writepost activity
            long id = _cursor.getLong(DraftsFragment.COLUMN_USER_CREATE_POST_ID);
            Uri uri = MindlrContract.UserCreatePostEntry.buildUserCreatePostUri(id);
            intent.putExtra(WritePostActivity.DRAFT_EXTRA, uri);
            _context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            int adapterPosition = getAdapterPosition();
            _cursor.moveToPosition(adapterPosition);
            Toast.makeText(_context, "LongClick ID: " + _cursor.getLong(DraftsFragment
                    .COLUMN_USER_CREATE_POST_ID), Toast.LENGTH_SHORT).show();

            // click event consumed
            return true;
        }
    }


    @Override
    public DraftsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.recyclerview_draft_item,
                viewGroup,
                false);
        view.setFocusable(true);
        return new DraftsAdapter.DraftsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DraftsAdapterViewHolder viewHolder, int position) {
        _cursor.moveToPosition(position);

        // bind values from cursor
        viewHolder.postContentText.setText(_cursor.getString(DraftsFragment.COLUMN_CONTENT_TEXT));

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(_context.getContentResolver(), Uri.parse
                    (_cursor.getString(DraftsFragment.COLUMN_CONTENT_URI)));
            viewHolder.postContentImage.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            viewHolder.postContentImage.setBackgroundColor(Color.GRAY);

        }

    }

    @Override
    public int getItemCount() {
        if (null == _cursor) {
            return 0;
        }
        return _cursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        if (_cursor != null && _cursor.moveToPosition(position)) {
            return _cursor.getLong(DraftsFragment.COLUMN_USER_CREATE_POST_ID);
        }
        return 0;
    }

    public void swapCursor(Cursor newCursor) {
        _cursor = newCursor;
        notifyDataSetChanged();
        _emptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
