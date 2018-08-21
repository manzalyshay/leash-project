package com.shaym.leash.logic.user;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Profile {

    private String displayname;
    private String email;
    private String avatarurl;
    private double currentlat;
    private double currentlng;
    private int forumpostsamount;
    private int gearpostsamount;
    private int inboxmsgsmount;
    private int outboxmsgsmount;

    private String uid;

    public  Profile(){
    }

    public Profile(String email, String uid, String displayname, double currentlat, double currentlng, int forumpostsamount, int gearpostsamount, int inboxmsgsmount, int outboxmsgsmount, String avatarurl) {
        this.email = email;
        this.uid = uid;
        this.displayname = displayname;
        this.currentlat = currentlat;
        this.currentlng = currentlng;
        this.forumpostsamount = forumpostsamount;
        this.gearpostsamount = gearpostsamount;
        this.inboxmsgsmount = inboxmsgsmount;
        this.outboxmsgsmount = outboxmsgsmount;
        this.avatarurl = avatarurl;
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
        if (displayname.isEmpty() || displayname == null){
            setDisplayname(email.substring(0, email.indexOf("@")));
        }
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

    public int getInboxmsgsmount() {
        return inboxmsgsmount;
    }

    public void setGearpostsamount(int gearpostsamount) {
        this.gearpostsamount = gearpostsamount;
    }

    public void setInboxmsgsmount(int messagesamount) {
        this.inboxmsgsmount = messagesamount;
    }

    public int getOutboxmsgsmount() {
        return outboxmsgsmount;
    }

    public void setOutboxmsgsmount(int outboxmsgsmount) {
        this.outboxmsgsmount = outboxmsgsmount;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "displayname='" + displayname + '\'' +
                ", email='" + email + '\'' +
                ", avatarurl='" + avatarurl + '\'' +
                ", currentlat=" + currentlat +
                ", currentlng=" + currentlng +
                ", forumpostsamount=" + forumpostsamount +
                ", gearpostsamount=" + gearpostsamount +
                ", inboxmsgsmount=" + inboxmsgsmount +
                ", outboxmsgsmount=" + outboxmsgsmount +
                ", uid='" + uid + '\'' +
                '}';
    }
}
