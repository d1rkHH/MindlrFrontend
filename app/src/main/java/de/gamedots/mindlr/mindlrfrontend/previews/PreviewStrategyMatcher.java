package de.gamedots.mindlr.mindlrfrontend.previews;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;
import de.gamedots.mindlr.mindlrfrontend.previews.strategy.ImageStrategy;
import de.gamedots.mindlr.mindlrfrontend.previews.strategy.PreviewStrategy;
import de.gamedots.mindlr.mindlrfrontend.previews.strategy.YoutubeStrategy;

/**
 * This singleton holds a list of all supported preview stategies. When a post gets displayed,
 * this objects matchStrategy method should be called to try to display a preview.
 * Created by Max Wiechmnann on 14.01.17.
 */

public class PreviewStrategyMatcher {
    // Structure of this regex: First ground = protocol, then any number of subdomains/the domain,
    // after that any number of sites or get parameter
    private static final String url_regex = "(http|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?";

    private List<PreviewStrategy> _previewStrategies;
    private static PreviewStrategyMatcher _instance = null;

    private PreviewStrategyMatcher(){
        _previewStrategies = new ArrayList<>();
    }

    public static PreviewStrategyMatcher getInstance(){
        if(_instance == null){
            _instance = new PreviewStrategyMatcher();
            _instance.registerStrategy(new YoutubeStrategy());
            _instance.registerStrategy(new ImageStrategy());
        }
        return _instance;
    }

    public void registerStrategy(PreviewStrategy strategy){
        _previewStrategies.add(strategy);
    }


    /**
     * For a given viewPost, find a strategy to preview a special type of content, e.g. image,
     * youtube, general URL etc. - If no match is found, there will be no preview created
     * @param viewPost
     * @return the first matching strategy or a NoPreviewStrategy
     */
    public PreviewStrategy matchStrategy(ViewPost viewPost){
        Log.v("Preview", "match strategy");
        if(viewPost.getContentUri() != null && !viewPost.getContentUri().isEmpty()){
            Log.v("Preview", "ContentURL not empty");
            for(PreviewStrategy strategy : _previewStrategies){
                if(strategy.match(viewPost.getContentUri())){
                    return strategy;
                }
            }
        } else {
            Log.v("Preview", "ContentURL empty");
            for(String url : findURLs(viewPost.getContentText())){
                Log.v("Preview", "Found URL: " + url);
                for(PreviewStrategy strategy : _previewStrategies){
                    if(strategy.match(url)){
                        return strategy;
                    }
                }
            }
        }
        return new NoPreviewStrategy();
    }

    /**
     * For a given String, find all URLs inside this string
     * @param contentText
     * @return a list of found URL strings
     */
    private List<String> findURLs(String contentText){
        List<String> foundURLs = new ArrayList<>();
        Matcher matcher = Pattern.compile(url_regex).matcher(contentText);
        while(matcher.find()){
            foundURLs.add(matcher.group(0));
        }
        return foundURLs;
    }

    /**
     * Default strategy if no URL is found that could be previewed
     */
    private class NoPreviewStrategy implements PreviewStrategy{

        @Override
        public boolean match(String url) {
            return false;
        }

        @Override
        public PreviewStrategy getCopy() {
            return this;
        }

        @Override
        public void buildPreviewUI(Context context, List<View> views, Bundle savedInstanceState) {

        }
    }
}
