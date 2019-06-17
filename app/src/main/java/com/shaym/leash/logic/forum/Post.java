package com.shaym.leash.logic.forum;

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
    public String forum;
    public List<String> images;
    public Date date;

    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String key, String forum, String body, Date date, List<String> images, int starCount) {
        this.uid = uid;
        this.key = key;
        this.forum = forum;
        this.date = date;
        this.body = body;
        this.images = images;
        this.starCount = starCount;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("key", key);

        result.put("forum", forum);
        result.put("images", images);
        result.put("body", body);
        result.put("date", date);

        result.put("starCount", starCount);
        result.put("stars", stars);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]