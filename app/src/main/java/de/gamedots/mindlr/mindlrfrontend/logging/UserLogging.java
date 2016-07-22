package de.gamedots.mindlr.mindlrfrontend.logging;

import java.util.Date;

/**
 * Created by max on 28.09.15.
 * Class to represent the user Logging table in the DB
 * TODO: Insert timer etc. in this class and change setter to add()-Methods/start()/stop()-methods if better
 */
public class UserLogging {

    private int userID;
    private String ipAddress;
    private String loginType;
    private String deviceLanguage;
    private Date clientTime;
    private Date clientEndTime; // Server-side: Calculate start & end session from clientEndTime-clientTime and current time of server
    private int swipeUp;
    private int swipeDown;
    private int swipeLeft;
    private int swipeRight;
    private int shared;
    private int posted;
    private int saved;
    private int achievementsEarned;
    private long standbyTime;
    private long activeTime;
    private long portraitTime;
    private long landscapeTime;
    private long timeInProfile;
    private long timeInWritePost;
    private long timeInAchievements;
    private long timeInFavorites;
    private long timeInOwnPosts;
    private long timeInSettings;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getDeviceLanguage() {
        return deviceLanguage;
    }

    public void setDeviceLanguage(String deviceLanguage) {
        this.deviceLanguage = deviceLanguage;
    }

    public Date getClientTime() {
        return clientTime;
    }

    public void setClientTime(Date clientTime) {
        this.clientTime = clientTime;
    }

    public Date getClientEndTime() {
        return clientEndTime;
    }

    public void setClientEndTime(Date clientEndTime) {
        this.clientEndTime = clientEndTime;
    }

    public int getSwipeUp() {
        return swipeUp;
    }

    public void setSwipeUp(int swipeUp) {
        this.swipeUp = swipeUp;
    }

    public int getSwipeLeft() {
        return swipeLeft;
    }

    public void setSwipeLeft(int swipeLeft) {
        this.swipeLeft = swipeLeft;
    }

    public int getSwipeDown() {
        return swipeDown;
    }

    public void setSwipeDown(int swipeDown) {
        this.swipeDown = swipeDown;
    }

    public int getSwipeRight() {
        return swipeRight;
    }

    public void setSwipeRight(int swipeRight) {
        this.swipeRight = swipeRight;
    }

    public int getShared() {
        return shared;
    }

    public void setShared(int shared) {
        this.shared = shared;
    }

    public int getPosted() {
        return posted;
    }

    public void setPosted(int posted) {
        this.posted = posted;
    }

    public int getSaved() {
        return saved;
    }

    public void setSaved(int saved) {
        this.saved = saved;
    }

    public long getStandbyTime() {
        return standbyTime;
    }

    public void setStandbyTime(long standbyTime) {
        this.standbyTime = standbyTime;
    }

    public int getAchievementsEarned() {
        return achievementsEarned;
    }

    public void setAchievementsEarned(int achievementsEarned) {
        this.achievementsEarned = achievementsEarned;
    }

    public long getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(long activeTime) {
        this.activeTime = activeTime;
    }

    public long getPortraitTime() {
        return portraitTime;
    }

    public void setPortraitTime(long portraitTime) {
        this.portraitTime = portraitTime;
    }

    public long getLandscapeTime() {
        return landscapeTime;
    }

    public void setLandscapeTime(long landscapeTime) {
        this.landscapeTime = landscapeTime;
    }

    public long getTimeInProfile() {
        return timeInProfile;
    }

    public void setTimeInProfile(long timeInProfile) {
        this.timeInProfile = timeInProfile;
    }

    public long getTimeInWritePost() {
        return timeInWritePost;
    }

    public void setTimeInWritePost(long timeInWritePost) {
        this.timeInWritePost = timeInWritePost;
    }

    public long getTimeInAchievements() {
        return timeInAchievements;
    }

    public void setTimeInAchievements(long timeInAchievements) {
        this.timeInAchievements = timeInAchievements;
    }

    public long getTimeInFavorites() {
        return timeInFavorites;
    }

    public void setTimeInFavorites(long timeInFavorites) {
        this.timeInFavorites = timeInFavorites;
    }

    public long getTimeInSettings() {
        return timeInSettings;
    }

    public void setTimeInSettings(long timeInSettings) {
        this.timeInSettings = timeInSettings;
    }

    public long getTimeInOwnPosts() {
        return timeInOwnPosts;
    }

    public void setTimeInOwnPosts(long timeInOwnPosts) {
        this.timeInOwnPosts = timeInOwnPosts;
    }
}




