package com.shaym.leash.logic.gear;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class GearPost {

    public String uid;
    public String author;
    public String title;
    public int price;
    public String phonenumber;
    public String imageurl;
    public String description;
    public String type;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public GearPost() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public GearPost(String uid, String type, String author, String title, int price, String phonenumber, String description, String imageurl) {
        this.uid = uid;
        this.type = type;
        this.author = author;
        this.title = title;
        this.price = price;
        this.phonenumber = phonenumber;
        this.description = description;
        this.imageurl = imageurl;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("type", type);
        result.put("author", author);
        result.put("title", title);
        result.put("price", price);
        result.put("phonenumber", phonenumber);
        result.put("description", description);
        result.put("imageurl", imageurl);
        result.put("starCount", starCount);
        result.put("stars", stars);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]