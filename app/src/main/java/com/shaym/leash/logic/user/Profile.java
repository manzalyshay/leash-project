package com.shaym.leash.logic.user;

import android.net.Uri;

import com.google.firebase.database.IgnoreExtraProperties;

import java.net.URI;

@IgnoreExtraProperties
public class Profile {

    private String displayname;
    private String email;
    private String avatarurl;
    private double currentlat;
    private double currentlng;
    private int postsamount;
    private int gearpostsamount;
    private int messagesamount;

    public  Profile(){
    }

    public Profile(String email, String displayname, double currentlat, double currentlng, int postsamount, int gearpostsamount, int messagesamount, String avatarurl) {
        this.email = email;
        this.displayname = displayname;
        this.currentlat = currentlat;
        this.currentlng = currentlng;
        this.postsamount = postsamount;
        this.gearpostsamount = gearpostsamount;
        this.messagesamount = messagesamount;
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

    @Override
    public String toString() {
        return "Profile{" +
                "displayname='" + displayname + '\'' +
                ", email='" + email + '\'' +
                ", avatarurl='" + avatarurl + '\'' +
                ", currentlat=" + currentlat +
                ", currentlng=" + currentlng +
                ", postsamount=" + postsamount +
                ", gearpostsamount=" + gearpostsamount +
                ", messagesamount=" + messagesamount +
                '}';
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

    public void setpostsamount(int postsamount) {
        this.postsamount = postsamount;
    }

    public int getpostsamount() {
        return postsamount;
    }

    public int getGearpostsamount() {
        return gearpostsamount;
    }

    public int getMessagesamount() {
        return messagesamount;
    }

    public void setGearpostsamount(int gearpostsamount) {
        this.gearpostsamount = gearpostsamount;
    }

    public void setMessagesamount(int messagesamount) {
        this.messagesamount = messagesamount;
    }
}
