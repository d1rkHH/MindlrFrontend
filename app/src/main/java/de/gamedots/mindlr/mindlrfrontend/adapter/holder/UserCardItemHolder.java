package de.gamedots.mindlr.mindlrfrontend.adapter.holder;

import android.view.View;
import android.widget.TextView;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.model.UserPostCardItem;

/**
 * Created by dirk on 16.04.2016.
 */
public class UserCardItemHolder extends AbstractViewHolder {

    private TextView upPercentText;
    private TextView downPercentText;

    public UserCardItemHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void cacheViews() {
        postCategoryText = (TextView) itemView.findViewById(R.id.post_category_text);
        postPreviewText =  (TextView) itemView.findViewById(R.id.post_preview_text);
        upPercentText =  (TextView) itemView.findViewById(R.id.upvote_percent_text);
        downPercentText =  (TextView) itemView.findViewById(R.id.down_vote_percent_text);
        createDateText =  (TextView) itemView.findViewById(R.id.post_create_date_text);
    }

    public void bind(UserPostCardItem model){
        postCategoryText.setText(model.getCategoryText());
        postPreviewText.setText(model.getPreviewText());
        upPercentText.setText(model.getUpPercentText());
        downPercentText.setText(model.getDownPercentText());
        createDateText.setText(model.getCreateDateText());
    }
}