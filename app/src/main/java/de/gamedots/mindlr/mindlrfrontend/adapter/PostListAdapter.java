package de.gamedots.mindlr.mindlrfrontend.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.model.post.UserPost;

/**
 * Created by Dirk on 05.09.15.
 */
public class PostListAdapter extends ArrayAdapter<UserPost> {

    private Context context;
    private int layoutResourceId;
    private List<UserPost> postList;

    public PostListAdapter(Context context, int resource, List<UserPost> postList) {
        super(context, resource, postList);

        this.layoutResourceId = resource;
        this.context = context;
        this.postList = postList;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(layoutResourceId, parent, false);

        TextView date = (TextView) customView.findViewById(R.id.postDateTextView);
        TextView category = (TextView) customView.findViewById(R.id.postCategoryTextView);
        TextView postPrev = (TextView) customView.findViewById(R.id.postTextPrev);
        TextView upvote = (TextView) customView.findViewById(R.id.postUpVoteNumber);
        TextView downvote = (TextView) customView.findViewById(R.id.postDownVoteNumber);

        UserPost post = postList.get(position);
        date.setText(post.getSubmitDate().toString());
        category.setText(post.getCategory().getName());
        postPrev.setText(post.getContentText());
        upvote.setText("" + post.getUpvotes());
        downvote.setText("" + post.getDownvotes());

        return customView;
    }

    @Override
    public UserPost getItem(int position) {
        return super.getItem(position);
    }
}
