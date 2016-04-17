package de.gamedots.mindlr.mindlrfrontend.adapter.holder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.model.UserPostCardItem;
import de.gamedots.mindlr.mindlrfrontend.view.activity.PostDetailActivity;

/**
 * Created by dirk on 16.04.2016.
 */
public class FavorCardItemHolder extends BaseViewHolder implements  View.OnClickListener{

    public FavorCardItemHolder(View itemView, Context context) {
        super(itemView, context);

        itemView.setOnClickListener(this);
    }

    @Override
    public void cacheViews() {
        postCategoryText = (TextView) itemView.findViewById(R.id.post_category_text);
        postPreviewText =  (TextView) itemView.findViewById(R.id.post_preview_text);
        createDateText =  (TextView) itemView.findViewById(R.id.post_create_date_text);
    }

    @Override
    public void bind(UserPostCardItem model){
        postCategoryText.setText(model.getCategoryText());
        postPreviewText.setText(model.getPreviewText());
        createDateText.setText(model.getCreateDateText());
    }

    @Override
    public void onClick(View v) {
        context.startActivity(new Intent(context, PostDetailActivity.class));
    }
}
