package de.gamedots.mindlr.mindlrfrontend.previews.strategy;

import android.support.v4.app.Fragment;

/**
 * Created by max on 14.01.17.
 */

public interface PreviewStrategy {

    boolean match(String url);

    void buildUI(Fragment fragment);
}
