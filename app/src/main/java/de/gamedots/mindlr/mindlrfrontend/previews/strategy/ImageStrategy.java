package de.gamedots.mindlr.mindlrfrontend.previews.strategy;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by max on 14.01.17.
 */

public class ImageStrategy implements PreviewStrategy {

    private final static String image_regex = "(http|https)://(i\\.imgur\\.com)/[\\w_-]+\\.(png|jpg|jpeg|gif)$";
    private final static String log_tag = "ImagePreview";

    private String _imageURL;

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
    public void buildPreviewUI(Context context, List<View> views, Bundle savedInstanceState) {
        /**
         * Loop through all available views and take the first view that is
         * of type ImageView and use it to display the preview.
         */
        ImageView imageView = null;
        for (View view : views){
            if (view instanceof ImageView){
                imageView = (ImageView)view;
            }
        }
        if (imageView != null) {
            imageView.setVisibility(View.VISIBLE);
            Glide.with(context).load(_imageURL).into(imageView);
        }
    }

    public void setImageURL(String imageURL) {
        this._imageURL = imageURL;
    }
}
