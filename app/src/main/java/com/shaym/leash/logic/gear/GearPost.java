package com.shaym.leash.logic.gear;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.shaym.leash.logic.forum.Post;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class GearPost extends Post {

    public String uid;
    public String key;
    public String location;
    public int price;
    public String phonenumber;
    public Date publishdate;
    public String body;
    public String category;
    public List<String> images;



    public GearPost() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public GearPost(String uid, String key, String category, String location, int price, String phonenumber, String body, Date publishdate, List<String> images) {
        this.uid = uid;
        this.key = key;
        this.category = category;
        this.location = location;
        this.price = price;
        this.phonenumber = phonenumber;
        this.body = body;
        this.images = images;
        this.publishdate = publishdate;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("key", key);

        result.put("category", category);
        result.put("location", location);
        result.put("price", price);
        result.put("phonenumber", phonenumber);
        result.put("body", body);
        result.put("images", images);
        result.put("publishdate", publishdate);
        
        return result;
    }
    // [END post_to_map]

}
// [END post_class]