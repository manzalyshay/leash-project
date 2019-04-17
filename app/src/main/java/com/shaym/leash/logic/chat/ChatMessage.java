package com.shaym.leash.logic.chat;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

// [START comment_class]
@IgnoreExtraProperties
public class ChatMessage {

    public String uid;
    public String text;
    public String author;
    public String timesent;

    public ChatMessage() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public ChatMessage(String uid, String author,  String text, String timesent, boolean isread) {
        this.uid = uid;
        this.author = author;
        this.text = text;
        this.timesent = timesent;
        this.isread = isread;
    }

    public boolean getIsread() {
        return isread;
    }

    public void setIsread(boolean isread) {
        this.isread = isread;
    }

    public boolean isread;
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTimesent() {

        return timesent;
    }

    public void setTimesent(String timesent) {
        this.timesent = timesent;
    }
}
// [END comment_class]