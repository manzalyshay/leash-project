package com.shaym.leash.logic.chat;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.shaym.leash.logic.forum.Post;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// [START comment_class]
@IgnoreExtraProperties
public class ChatMessage extends Post {

    public String uid;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String key;

    public String text;
    public String author;
    public Date timesent;
    public boolean isread;

    public ChatMessage() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public ChatMessage(String uid, String key, String author,  String text, Date timesent, boolean isread) {
        this.uid = uid;
        this.key = key;
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

    public Date getTimesent() {

        return timesent;
    }

    public void setTimesent(Date timesent) {
        this.timesent = timesent;
    }


    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("key", key);
        result.put("text", text);
        result.put("author", author);
        result.put("date", timesent);


        return result;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "uid='" + uid + '\'' +
                "key='" + key + '\'' +

                ", text='" + text + '\'' +
                ", author='" + author + '\'' +
                ", timesent=" + timesent +
                ", isread=" + isread +
                '}';
    }
}
// [END comment_class]