package de.gamedots.mindlr.mindlrfrontend.previews.strategy;

import android.support.v4.app.Fragment;

/**
 * Created by max on 14.01.17.
 */

public class YoutubeStrategy implements PreviewStrategy {

    @Override
    public boolean match(String url) {
        return false;
    }

    @Override
    public void buildUI(Fragment fragment) {

    }
}
