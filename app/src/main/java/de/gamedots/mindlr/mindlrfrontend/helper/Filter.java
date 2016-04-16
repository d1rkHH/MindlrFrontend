package de.gamedots.mindlr.mindlrfrontend.helper;

import java.util.ArrayList;
import java.util.List;

import de.gamedots.mindlr.mindlrfrontend.model.UserPostCardItem;

/**
 * Created by dirk on 17.04.2016.
 */
public class Filter {

    /**
     * Basic filter going through all current items in the recycler view and filter those cards
     * that overlaps with the category-,preview- or date text
     */
    public static List<UserPostCardItem> filter(List<UserPostCardItem> models, String query) {
        query = query.toLowerCase();

        final List<UserPostCardItem> filteredModelList = new ArrayList<>();
        for (UserPostCardItem model : models) {
            final String text = model.getCategoryText().toLowerCase();
            final String text2 = model.getPreviewText().toLowerCase();
            final String text3 = model.getCreateDateText().toLowerCase();
            if (text.contains(query) || text2.contains(query) || text3.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
