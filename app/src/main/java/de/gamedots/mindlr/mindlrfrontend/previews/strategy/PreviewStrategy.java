package de.gamedots.mindlr.mindlrfrontend.previews.strategy;

import android.content.Context;
import android.view.View;

import java.util.List;

/**
 * Created by max on 14.01.17.
 */

public interface PreviewStrategy {

    boolean match(String url);

    PreviewStrategy getCopy();

    void buildPreviewUI(Context context, List<View> availableVies);
}
