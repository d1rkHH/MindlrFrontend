package de.gamedots.mindlr.mindlrfrontend.model;


/**
 * Created by dirk on 14.04.2016.
 * Normal Bean to store the values for the card item hold by the viewholder
 * in the recyclerview
 */
public class UserPostCardItem {

    private String categoryText;
    private String previewText;
    private String upPercentText;
    private String downPercentText;
    private String createDateText;

    public UserPostCardItem() {
    }

    private UserPostCardItem(Builder builder){
        categoryText = builder.categoryText;
        previewText = builder.previewText;
        upPercentText = builder.upPercentText;
        downPercentText = builder.downPercentText;
        createDateText = builder.createDateText;
    }

    public String getCategoryText() {
        return categoryText;
    }


    public void setCategoryText(String categoryText) {
        this.categoryText = categoryText;
    }

    public String getPreviewText() {
        return previewText;
    }

    public void setPreviewText(String previewText) {
        this.previewText = previewText;
    }

    public String getUpPercentText() {
        return upPercentText;
    }

    public void setUpPercentText(String upPercentText) {
        this.upPercentText = upPercentText;
    }

    public String getDownPercentText() {
        return downPercentText;
    }

    public void setDownPercentText(String downPercentText) {
        this.downPercentText = downPercentText;
    }

    public String getCreateDateText() {
        return createDateText;
    }

    public void setCreateDateText(String createDateText) {
        this.createDateText = createDateText;
    }

    public static class Builder{
        private String categoryText;
        private String previewText;
        private String createDateText;

        private String upPercentText = "50%";
        private String downPercentText = "50%";

        public Builder(String categoryText, String previewText, String createDateText){
            this.categoryText = categoryText;
            this.previewText = previewText;
            this.createDateText = createDateText;
        }

        public Builder upVotes(String upvote){
            this.upPercentText = upvote;
            return this;
        }

        public Builder downVotes(String downvote){
            this.downPercentText = downvote;
            return this;
        }

        public UserPostCardItem build(){
            return new UserPostCardItem(this);
        }
    }
}
