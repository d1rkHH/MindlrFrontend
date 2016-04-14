package de.gamedots.mindlr.mindlrfrontend.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.gamedots.mindlr.mindlrfrontend.R;

/**
 * Created by dirk on 14.04.2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ItemViewHolder>{

    private List<String> _items;

    public RVAdapter(List<String> _items) {
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
        itemViewHolder.mTextView.setText(_items.get(i));
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
}
