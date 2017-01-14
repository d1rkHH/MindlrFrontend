package de.gamedots.mindlr.mindlrfrontend.previews.strategy;

import android.os.Bundle;

import de.gamedots.mindlr.mindlrfrontend.view.fragment.PostViewFragment;

/**
 * Created by max on 14.01.17.
 */

public interface PreviewStrategy {

    boolean match(String url);

    PreviewStrategy getCopy();

    void buildPreviewUI(PostViewFragment fragment, Bundle savedInstanceState);

    void saveInstanceState(Bundle outState);
}
