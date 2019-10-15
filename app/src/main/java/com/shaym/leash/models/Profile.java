package com.shaym.leash.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;

@IgnoreExtraProperties
public class Profile implements Serializable {

    private String displayname;
    private String email;
    private String gender;
    private String avatarurl;
    private HashMap<String, Double> currentlocation;
    private String pushtoken;
    private String uid;
    private String role;
    private boolean online;

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int getUnreadcounter() {
        return unreadcounter;
    }

    public void setUnreadcounter(int unreadcounter) {
        this.unreadcounter = unreadcounter;
    }

    private int unreadcounter;

    @Override
    public String toString() {
        return "Profile{" +
                "displayname='" + displayname + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", avatarurl='" + avatarurl + '\'' +
                ", currentlocation=" + currentlocation +
                ", pushtoken='" + pushtoken + '\'' +
                ", uid='" + uid + '\'' +
                ", unreadcounter='" + unreadcounter + '\'' +
                ", role='" + role + '\'' +
                ", online='" + online + '\'' +

                '}';
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Profile(String displayname, String role, String email, String gender, String avatarurl, HashMap<String, Double> currentlocation,  String pushtoken, String uid, int unreadcounter) {
        this.displayname = displayname;
        this.online = true;
        this.role = role;
        this.email = email;
        this.gender = gender;
        this.avatarurl = avatarurl;
        this.currentlocation = currentlocation;
        this.pushtoken = pushtoken;
        this.uid = uid;
        this.unreadcounter = unreadcounter;
    }

    public  Profile(){
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAvatarurl(String avatarurl) {
        this.avatarurl = avatarurl;
    }



    public void setPushtoken(String pushtoken) {
        this.pushtoken = pushtoken;
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

    public String getGender() {
        return gender;
    }

    public String getAvatarurl() {
        return avatarurl;
    }


    public String getPushtoken() {
        return pushtoken;
    }

    public String getUid() {
        return uid;
    }


    public HashMap<String, Double> getCurrentlocation() {
        return currentlocation;
    }


    public void setCurrentlocation(HashMap<String, Double> currentlocation) {
        this.currentlocation = currentlocation;

    }
}
