package de.gamedots.mindlr.mindlrfrontend.adapter.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.gamedots.mindlr.mindlrfrontend.model.UserPostCardItem;

/**
 * Created by dirk on 16.04.2016.
 */
public abstract class BaseViewHolder extends RecyclerView.ViewHolder{

    /*
    *   A ViewHolder describes an item view and metadata about
    *   its place within the RecyclerView.
    *   It add fields for caching findViewById() results
    */

    protected TextView postCategoryText;
    protected TextView postPreviewText;
    protected TextView createDateText;
    protected Context context;


    public BaseViewHolder(View itemView, Context context) {
        super(itemView);

        this.context = context;
        cacheViews();
    }

    /*
     * Get view by id and cache them in the fields
     */
    public abstract void cacheViews();


    /*
     * Set model data to the bound views
     */
    public abstract void bind(UserPostCardItem model) ;
}
