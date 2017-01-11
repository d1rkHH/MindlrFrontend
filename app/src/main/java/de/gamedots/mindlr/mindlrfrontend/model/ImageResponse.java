package de.gamedots.mindlr.mindlrfrontend.model;

/**
 * Created by Dirk on 09.01.17.
 */

public class ImageResponse {

    // imgur base response model
    public UploadedImage data;
    public boolean success;
    public int status;

    public static class UploadedImage {
        public String id;
        public String title;
        public String description;
        public String type;
        public int datetime;
        public boolean animated;
        public int width;
        public int height;
        public int size;
        public int views;
        public int bandwidth;
        public String vote;
        public boolean favorite;
        public String account_url;
        public String name;
        public String link;
    }
}
