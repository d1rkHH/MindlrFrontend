package de.gamedots.mindlr.mindlrfrontend.previews.strategy;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import de.gamedots.mindlr.mindlrfrontend.view.fragment.PostViewFragment;

/**
 * Created by max on 14.01.17.
 */

public class ImageStrategy implements PreviewStrategy {

    private final static String image_regex = "(http|https)://(i\\.imgur\\.com)/[\\w_-]+\\.(png|jpg|jpeg|gif)$";
    private final static String log_tag = "ImagePreview";

    private boolean _machted;
    private String _imageURL;

    private PostViewFragment _fragment;
    private ImageView _postImageView;


    @Override
    public boolean match(String url) {
        Matcher matcher = Pattern.compile(image_regex).matcher(url);
        if(matcher.find()){
            _imageURL = matcher.group(0);
            _machted = true;
            return true;
        }
        _machted = false;
        return false;
    }

    @Override
    public void buildPreviewUI(PostViewFragment fragment) {
        if(_machted){
            _fragment = fragment;
            // TODO: Get view from postViewFragment
            // _postImageView = (ImageView) view.findViewById(R.id.postImageView);
            _postImageView.setVisibility(View.VISIBLE);
            Glide.with(_fragment).load(_imageURL).fitCenter().into(_postImageView);
        } else {
            Log.v(log_tag, "Cannot build preview, no matching image URL");
        }
    }
}
