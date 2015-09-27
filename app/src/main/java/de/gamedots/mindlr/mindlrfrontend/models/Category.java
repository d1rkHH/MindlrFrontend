package de.gamedots.mindlr.mindlrfrontend.models;

/**
 * Created by max on 27.09.15.
 */
public class Category {

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
}
