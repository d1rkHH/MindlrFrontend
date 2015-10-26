package de.gamedots.mindlr.mindlrfrontend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.gamedots.mindlr.mindlrfrontend.models.Category;

/**
 * Created by max on 26.09.15.
 */
public final class Global {

    private Global(){}

    public static final PostLoader postLoader = new PostLoader();

    //Server Connection
    public static final String SERVER_URL = "http://backend.mindlr.com:8080/requesthandler/";
    //Methods
    public static final String BACKEND_METHOD_KEY = "BACKEND_METHOD";
    //Load Posts
    public static final String BACKEND_METHOD_LOAD_POSTS = "LOAD_POSTS";
    //Write Post
    public static final String BACKEND_METHOD_WRITE_POST = "WRITE_POST";
    public static final int DEFAULT_USER_ID = 1;
    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";



    public static class Categories {
        //TODO: Dynamicly update categories from database
        public static final Category SPORTS = new Category(1, "Sports");
        public static final Category GAMING = new Category(2, "Gaming");
        public static final Category TECHNOLOGY = new Category(3, "Technology");
        public static final Category SCIENCE_NATURE = new Category(4, "Science & Nature");
        public static final Category ART_CULTURE = new Category(5, "Art & Culture");
        public static final Category POPCULTURE = new Category(6, "Pop Culture");
        public static final Category NEWS = new Category(7, "News");
        public static final Category LIFESTYLE_MALE = new Category(8, "Lifestyle (Male)");
        public static final Category LIFESTYLE_FEMALE = new Category(9, "Lifestyle (Female)");
        public static final Category POLITICS_ECONOMICS = new Category(10, "Politics & Economics");
        public static final Category PERSONAL = new Category(11, "Personal");
        public static final Category FUNNY_FASCINATING = new Category(12, "Funny & Fascinating");
        public static final Category QUOTES_MOTIVATION = new Category(13, "Quotes & Motivation");


        public static final String[] CATEGORIES = {SPORTS.getName(), GAMING.getName(),
                TECHNOLOGY.getName(), SCIENCE_NATURE.getName(), ART_CULTURE.getName(),
                POPCULTURE.getName(), NEWS.getName(), LIFESTYLE_FEMALE.getName(),
                LIFESTYLE_MALE.getName(), POLITICS_ECONOMICS.getName(),PERSONAL.getName(),
                FUNNY_FASCINATING.getName(), QUOTES_MOTIVATION.getName()};
    }
}
