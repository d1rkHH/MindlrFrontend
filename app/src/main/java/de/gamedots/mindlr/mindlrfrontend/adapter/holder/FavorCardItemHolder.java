package de.gamedots.mindlr.mindlrfrontend.adapter.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.model.UserPostCardItem;

/**
 * Created by dirk on 16.04.2016.
 */
public class FavorCardItemHolder extends BaseViewHolder implements View.OnClickListener {

    public FavorCardItemHolder(View itemView, Context context) {
        super(itemView, context);

        itemView.setOnClickListener(this);
    }

    @Override
    public void cacheViews() {
        postPreviewText = (TextView) itemView.findViewById(R.id.post_preview_text);
    }

    @Override
    public void bind(UserPostCardItem model) {
        postPreviewText.setText(model.getPreviewText());
    }

    @Override
    public void onClick(View v) {
    }
}
