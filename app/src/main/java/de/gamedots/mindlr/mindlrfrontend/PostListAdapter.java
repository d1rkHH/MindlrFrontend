package de.gamedots.mindlr.mindlrfrontend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Dirk on 05.09.15.
 */
public class PostListAdapter extends ArrayAdapter<Post> {

    private Context context;
    private int layoutResourceId;
    private List<Post> postList;

    public PostListAdapter(Context context, int resource, List<Post> postList) {
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

        Post post = postList.get(position);
        date.setText(post.getCreateDate());
        category.setText(post.getCategory());
        postPrev.setText(post.getPostText());
        upvote.setText("" + post.getUpVotes());
        downvote.setText("" + post.getDownVotes());

        return customView;
    }

    @Override
    public Post getItem(int position) {
        return super.getItem(position);
    }
}
