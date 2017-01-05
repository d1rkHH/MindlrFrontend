package de.gamedots.mindlr.mindlrfrontend.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by max on 27.09.15.
 */
public class Category {

    public static List<Category> allCategories = new ArrayList<>();

    private int id;
    private String name; //Use this name as a key for strings.xml


    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
