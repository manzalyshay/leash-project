package com.shaym.leash.logic.user;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Profile implements Serializable {

    private String displayname;
    private String email;
    private boolean isemailhidden;
    private String phonenumber;

    private boolean isphonehidden;
    private String avatarurl;
    private double currentlat;
    private double currentlng;
    private int forumpostsamount;
    private int gearpostsamount;
    private int conversationssmount;
    private int unreadmsgsamount;
    private String uid;

    public String getPushtoken() {
        return pushtoken;
    }

    public void setPushtoken(String pushtoken) {
        this.pushtoken = pushtoken;
    }

    private String pushtoken;


    public boolean isIsemailhidden() {
        return isemailhidden;
    }

    public void setIsemailhidden(boolean isemailhidden) {
        this.isemailhidden = isemailhidden;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public boolean isIsphonehidden() {
        return isphonehidden;
    }

    public void setIsphonehidden(boolean isphonehidden) {
        this.isphonehidden = isphonehidden;
    }


    public  Profile(){
    }

    public Profile(String email, boolean isemailhidden, String phonenumber, boolean isphonehidden, String uid, String displayname, double currentlat, double currentlng, int forumpostsamount, int gearpostsamount, int conversationssmount, int unreadmsgsamount, String avatarurl, String pushtoken) {
        this.email = email;
        this.isemailhidden = isemailhidden;
        this.phonenumber = phonenumber;
        this.isphonehidden = isphonehidden;
        this.uid = uid;
        this.displayname = displayname;
        this.currentlat = currentlat;
        this.currentlng = currentlng;
        this.forumpostsamount = forumpostsamount;
        this.gearpostsamount = gearpostsamount;
        this.conversationssmount = conversationssmount;
        this.unreadmsgsamount = unreadmsgsamount;
        this.avatarurl = avatarurl;
        this.pushtoken = pushtoken;
    }

    public void setDisplayname(String disname) {
        this.displayname = disname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvatarURL(String avatarurl) {
        this.avatarurl = avatarurl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayname() {

        return displayname;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarURL() {
            return avatarurl;
    }

    public double getcurrentlat() {
        return currentlat;
    }

    public void setcurrentlat(double currentlat) {
        this.currentlat = currentlat;
    }

    public double getcurrentlng() {
        return currentlng;
    }

    public void setcurrentlng(double currentlng) {
        this.currentlng = currentlng;
    }

    public void setforumpostsamount(int postsamount) {
        this.forumpostsamount = postsamount;
    }

    public int getForumpostsamount() {
        return forumpostsamount;
    }

    public int getgearpostsamount() {
        return gearpostsamount;
    }

    public void setGearpostsamount(int gearpostsamount) {
        this.gearpostsamount = gearpostsamount;
    }

    public int getConversationssmount() {
        return conversationssmount;
    }

    public void setConversationssmount(int conversationssmount) {
        this.conversationssmount = conversationssmount;
    }

    public int getUnreadmsgsamount() {
        return unreadmsgsamount;
    }

    public void setUnreadmsgsamount(int unreadmsgsamount) {
        this.unreadmsgsamount = unreadmsgsamount;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "displayname='" + displayname + '\'' +
                ", email='" + email + '\'' +
                ", isemailhidden=" + isemailhidden +
                ", phonenumber='" + phonenumber + '\'' +
                ", isphonehidden=" + isphonehidden +
                ", avatarurl='" + avatarurl + '\'' +
                ", currentlat=" + currentlat +
                ", currentlng=" + currentlng +
                ", forumpostsamount=" + forumpostsamount +
                ", gearpostsamount=" + gearpostsamount +
                ", conversationssmount=" + conversationssmount +
                ", unreadmsgsamount=" + unreadmsgsamount +
                ", uid='" + uid + '\'' +
                '}';
    }
}
