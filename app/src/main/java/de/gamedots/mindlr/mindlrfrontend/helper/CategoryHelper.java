package de.gamedots.mindlr.mindlrfrontend.helper;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dirk on 11.01.17.
 */

public class CategoryHelper {

    private static Set<Long> categories = new HashSet<>();

    public static Set<Long> getCategories(){
        return categories;
    }
}
