package de.gamedots.mindlr.mindlrfrontend.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.model.UserPostCardItem;

/**
 * Created by dirk on 14.04.2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ItemViewHolder>{

    private List<UserPostCardItem> _items;

    public RVAdapter(List<UserPostCardItem> _items) {
        this._items = _items;
    }

    /*
     *   A ViewHolder describes an item view and metadata about
     *   its place within the RecyclerView.
     *   It add fields for caching findViewById() results
     */
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.list_item);
        }
    }

    /* Get the view at position i to display */
    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.mTextView.setText(_items.get(i).get_text());
        // final UserPostCardItem model = _itemls.get(i);
        // itemViewHolder.bind(model);
        /* replace for custom bind method to set all views data */
    }

    /*
     * Specify the layout that each item of the RecyclerView will use
     * This is done by inflating the layout using LayoutInflater,
     * passing the output to the constructor of the custom ViewHolder.
     */
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    /*
     * Return number of items present in the data
     * @return item count
     */
    @Override
    public int getItemCount() {
        return _items.size();
    }

    /*
     * Set filtered items to the new items and notify about change
     */
    public void setFilter(List<UserPostCardItem> post) {
        _items = new ArrayList<>();
        _items.addAll(post);
        notifyDataSetChanged();
    }
}
