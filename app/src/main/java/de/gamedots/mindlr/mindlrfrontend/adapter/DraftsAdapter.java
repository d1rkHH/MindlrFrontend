package de.gamedots.mindlr.mindlrfrontend.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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

            final Dialog dialog = new Dialog(_context);
            dialog.setContentView(R.layout.dialog_drafts_delete_edit);
            TextView edit = (TextView) dialog.findViewById(R.id.dialog_textview_edit);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    DraftsAdapterViewHolder.this.onClick(v);
                }
            });
            TextView delete = (TextView) dialog.findViewById(R.id.dialog_textview_delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _context.getContentResolver().delete(MindlrContract.UserCreatePostEntry.CONTENT_URI,
                            MindlrContract.UserCreatePostEntry._ID + " = ? ",
                            new String[]{Long.toString(_cursor.getLong(DraftsFragment
                                    .COLUMN_USER_CREATE_POST_ID))});
                    dialog.dismiss();
                }
            });

            dialog.show();

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
            //Bitmap bitmap = MediaStore.Images.Media.getBitmap(_context.getContentResolver(), Uri.parse
            //      (_cursor.getString(DraftsFragment.COLUMN_CONTENT_URI)));
            //viewHolder.postContentImage.setImageBitmap(bitmap);
            Glide.with(_context)
                    .loadFromMediaStore(Uri.parse(_cursor.getString(DraftsFragment.COLUMN_CONTENT_URI)))
                    .asBitmap()
                    .into(viewHolder.postContentImage);
        } catch (Exception e) {
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
