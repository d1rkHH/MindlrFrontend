package de.gamedots.mindlr.mindlrfrontend.previews;

import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.gamedots.mindlr.mindlrfrontend.model.post.ViewPost;
import de.gamedots.mindlr.mindlrfrontend.previews.strategy.PreviewStrategy;
import de.gamedots.mindlr.mindlrfrontend.view.fragment.PostViewFragment;

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
    private static final PreviewStrategyMatcher _instance = new PreviewStrategyMatcher();

    private PreviewStrategyMatcher(){
        _previewStrategies = new ArrayList<>();
    }

    public static PreviewStrategyMatcher getInstance(){
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
        if(viewPost.getContentUri() != null && !viewPost.getContentUri().isEmpty()){
            for(PreviewStrategy strategy : _previewStrategies){
                if(strategy.match(viewPost.getContentUri())){
                    return strategy;
                }
            }
        } else {
            for(String url : findURLs(viewPost.getContentText())){
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
        public void buildPreviewUI(PostViewFragment fragment) {

        }
    }
}
