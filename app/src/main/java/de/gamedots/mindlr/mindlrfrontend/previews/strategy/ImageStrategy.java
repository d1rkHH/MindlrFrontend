package de.gamedots.mindlr.mindlrfrontend.previews.strategy;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.PostViewFragment;

/**
 * Created by max on 14.01.17.
 */

public class ImageStrategy implements PreviewStrategy {

    private final static String image_regex = "(http|https)://(i\\.imgur\\.com)/[\\w_-]+\\.(png|jpg|jpeg|gif)$";
    private final static String log_tag = "ImagePreview";

    private String _imageURL;

    private PostViewFragment _fragment;
    private ImageView _postImageView;


    @Override
    public boolean match(String url) {
        Log.v("Preview", "Match " + url + " to Image Link");
        Matcher matcher = Pattern.compile(image_regex).matcher(url);
        if(matcher.find()){
            _imageURL = matcher.group(0);
            Log.v("Preview", "Image URL: " + _imageURL);
            return true;
        }
        return false;
    }

    @Override
    public PreviewStrategy getCopy() {
        ImageStrategy strategy = new ImageStrategy();
        strategy.setImageURL(_imageURL);
        return strategy;
    }

    @Override
    public void buildPreviewUI(PostViewFragment fragment, Bundle savedInstanceState) {
        _fragment = fragment;
        _postImageView = (ImageView) _fragment.getView().findViewById(R.id.postImageView);
        _postImageView.setVisibility(View.VISIBLE);
        Glide.with(_fragment).load(_imageURL).into(_postImageView);
    }

    @Override
    public void saveInstanceState(Bundle outState) {

    }

    public void setImageURL(String imageURL) {
        this._imageURL = imageURL;
    }
}
