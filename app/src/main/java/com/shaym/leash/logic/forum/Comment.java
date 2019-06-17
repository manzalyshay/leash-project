package com.shaym.leash.logic.forum;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

// [START comment_class]
@IgnoreExtraProperties
public class Comment {

    public String uid;
    public Date date;
    public String text;
    public String key;

    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Comment(String uid, String key, Date date, String text) {
        this.key = key;
        this.uid = uid;
        this.date = date;
        this.text = text;
    }

}
// [END comment_class]