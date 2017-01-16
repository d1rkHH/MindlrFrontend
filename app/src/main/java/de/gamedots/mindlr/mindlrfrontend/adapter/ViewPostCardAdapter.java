package de.gamedots.mindlr.mindlrfrontend.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;
import de.gamedots.mindlr.mindlrfrontend.previews.PreviewStrategyMatcher;

/**
 * Created by Dirk on 16.01.17.
 */

public class ViewPostCardAdapter extends ArrayAdapter<ViewPost> {
    private  LinkedList<ViewPost> viewPostsList = new LinkedList<>();
    private  LayoutInflater layoutInflater;

    public ViewPostCardAdapter(Context context, LinkedList<ViewPost> viewPosts) {
        super(context, -1);
        this.viewPostsList = viewPosts;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewPost viewPost = viewPostsList.get(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.main_card_stack_item, parent, false);
        }

        // content handling here
        ((TextView)convertView.findViewById(R.id.viewpost_textview)).setText(viewPost.getContentText());

        ImageView image = (ImageView) convertView.findViewById(R.id.viewpost_imageview);
        List<View> availablePreviewViews = new ArrayList<>();
        availablePreviewViews.add(image);
        PreviewStrategyMatcher
                .getInstance()
                .matchStrategy(viewPost)
                .buildPreviewUI(getContext(), availablePreviewViews);

        return convertView;
    }

    public void addItem(ViewPost post){
        viewPostsList.add(post);
    }

    public void addItems(List<ViewPost> posts){
        viewPostsList.addAll(posts);
        notifyDataSetChanged();
    }

    public void popNotify(){
        viewPostsList.poll();
        notifyDataSetChanged();
    }

    @Override
    public ViewPost getItem(int position) {
        return viewPostsList.get(position);
    }

    @Override
    public int getCount() {
        return viewPostsList.size();
    }
}
