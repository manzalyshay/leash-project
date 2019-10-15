package com.shaym.leash.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Post {

    public String uid;
    public String key;
    public String body;
    public String category;
    public List<String> images;
    public Date date;
    public HashMap<String, Double> postlocation;
    public String postplace;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String key, String category, String body, Date date, List<String> images, HashMap<String, Double> postlocation, String postplace) {
        this.uid = uid;
        this.key = key;
        this.category = category;
        this.date = date;
        this.body = body;
        this.images = images;
        this.postlocation = postlocation;
        this.postplace = postplace;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("key", key);
        result.put("category", category);
        result.put("images", images);
        result.put("body", body);
        result.put("date", date);
        result.put("postlocation", postlocation);
        result.put("postplace", postplace);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]