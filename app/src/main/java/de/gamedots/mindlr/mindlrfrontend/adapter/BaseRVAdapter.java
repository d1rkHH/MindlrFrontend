package de.gamedots.mindlr.mindlrfrontend.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.adapter.holder.BaseViewHolder;
import de.gamedots.mindlr.mindlrfrontend.model.UserPostCardItem;

/**
 * Created by dirk on 14.04.2016.
 */
public abstract class BaseRVAdapter<T extends BaseViewHolder> extends RecyclerView.Adapter<T>{

    private List<UserPostCardItem> _items;
    private int _itemResourceId;

    public BaseRVAdapter(List<UserPostCardItem> _items, int itemResourceId) {
        this._items = _items;
        this._itemResourceId = itemResourceId;
    }


    /* Get the view at position i to display */
    @Override
    public void onBindViewHolder(T itemViewHolder, int i) {
        final UserPostCardItem model = _items.get(i);
        itemViewHolder.bind(model);
    }

    /*
     * Specify the layout that each item of the RecyclerView will use
     * This is done by inflating the layout using LayoutInflater,
     * passing the output to the constructor of the custom ViewHolder.
     */
    @Override
    public T onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(_itemResourceId, viewGroup, false);
        T itemViewHolder = getViewHolder(view);
        return itemViewHolder;
    }

    public abstract T getViewHolder(View view);


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
