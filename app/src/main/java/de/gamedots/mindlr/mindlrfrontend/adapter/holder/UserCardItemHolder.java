package de.gamedots.mindlr.mindlrfrontend.adapter.holder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.model.UserPostCardItem;
import de.gamedots.mindlr.mindlrfrontend.view.activity.PostDetailActivity;

import static de.gamedots.mindlr.mindlrfrontend.view.activity.UserPostsActivity.EXTRA_CATEGORY;
import static de.gamedots.mindlr.mindlrfrontend.view.activity.UserPostsActivity.EXTRA_CREATEDATE;
import static de.gamedots.mindlr.mindlrfrontend.view.activity.UserPostsActivity.EXTRA_DOWNPERCENT;
import static de.gamedots.mindlr.mindlrfrontend.view.activity.UserPostsActivity.EXTRA_LAYOUT;
import static de.gamedots.mindlr.mindlrfrontend.view.activity.UserPostsActivity.EXTRA_PREVIEW;
import static de.gamedots.mindlr.mindlrfrontend.view.activity.UserPostsActivity.EXTRA_UPPERCENT;

/**
 * Created by dirk on 16.04.2016.
 */
public class UserCardItemHolder extends BaseViewHolder implements View.OnClickListener {

    private TextView upPercentText;
    private TextView downPercentText;

    public UserCardItemHolder(View itemView, Context context) {
        super(itemView, context);

        itemView.setOnClickListener(this);
    }

    @Override
    public void cacheViews() {
        postCategoryText = (TextView) itemView.findViewById(R.id.post_category_text);
        postPreviewText =  (TextView) itemView.findViewById(R.id.post_preview_text);
        upPercentText =  (TextView) itemView.findViewById(R.id.upvote_percent_text);
        downPercentText =  (TextView) itemView.findViewById(R.id.down_vote_percent_text);
        createDateText =  (TextView) itemView.findViewById(R.id.post_create_date_text);
    }
    @Override
    public void bind(UserPostCardItem model){
        postCategoryText.setText(model.getCategoryText());
        postPreviewText.setText(model.getPreviewText());
        upPercentText.setText(model.getUpPercentText());
        downPercentText.setText(model.getDownPercentText());
        createDateText.setText(model.getCreateDateText());
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, PostDetailActivity.class);
        intent.putExtra(EXTRA_LAYOUT, 0);
        intent.putExtra(EXTRA_CATEGORY, postCategoryText.getText());
        intent.putExtra(EXTRA_PREVIEW, postPreviewText.getText());
        intent.putExtra(EXTRA_UPPERCENT, upPercentText.getText());
        intent.putExtra(EXTRA_DOWNPERCENT, downPercentText.getText());
        intent.putExtra(EXTRA_CREATEDATE, createDateText.getText());
        context.startActivity(intent);
    }
}
