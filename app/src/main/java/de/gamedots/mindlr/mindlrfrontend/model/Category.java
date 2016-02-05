package de.gamedots.mindlr.mindlrfrontend.model;

import java.util.ArrayList;
import java.util.List;
import static de.gamedots.mindlr.mindlrfrontend.util.Global.Categories.*;

/**
 * Created by max on 27.09.15.
 */
public class Category {

    public static List<Category> allCategories = new ArrayList<>();

    private int id;
    private String name;
    //TODO: Change name to variable-name to point into XML file for different languages


    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public static int getCategoryIDForName(String name){
        //TODO: REMOVE
        allCategories.add(SPORTS);
        allCategories.add(GAMING);
        allCategories.add(TECHNOLOGY);
        allCategories.add(SCIENCE_NATURE);
        allCategories.add(ART_CULTURE);
        allCategories.add(POPCULTURE);
        allCategories.add(NEWS);
        allCategories.add(LIFESTYLE_MALE);
        allCategories.add(LIFESTYLE_FEMALE);
        allCategories.add(POLITICS_ECONOMICS);
        allCategories.add(PERSONAL);
        allCategories.add(FUNNY_FASCINATING);
        allCategories.add(QUOTES_MOTIVATION);

        for(Category c : allCategories){
            if(c.getName().equals(name)) {
                return c.getId();
            }
        }
        return -1;
    }
}
