package de.gamedots.mindlr.mindlrfrontend.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.adapter.holder.UserCardItemHolder;
import de.gamedots.mindlr.mindlrfrontend.model.UserPostCardItem;

/**
 * Created by dirk on 14.04.2016.
 */
public class RVAdapter extends RecyclerView.Adapter<UserCardItemHolder>{

    private List<UserPostCardItem> _items;

    public RVAdapter(List<UserPostCardItem> _items) {
        this._items = _items;
    }



    /* Get the view at position i to display */
    @Override
    public void onBindViewHolder(UserCardItemHolder itemViewHolder, int i) {
        final UserPostCardItem model = _items.get(i);
        itemViewHolder.bind(model);
    }

    /*
     * Specify the layout that each item of the RecyclerView will use
     * This is done by inflating the layout using LayoutInflater,
     * passing the output to the constructor of the custom ViewHolder.
     */
    @Override
    public UserCardItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);
        UserCardItemHolder itemViewHolder = new UserCardItemHolder(view);
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
