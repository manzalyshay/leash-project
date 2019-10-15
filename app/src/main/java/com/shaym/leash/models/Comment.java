package com.shaym.leash.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("key", key);

        result.put("text", text);
        result.put("date", date);


        return result;
    }

}
// [END comment_class]