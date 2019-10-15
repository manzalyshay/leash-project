package com.shaym.leash.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class GearPost extends Post {

    public String salelocation;
    public int price;
    public String phonenumber;


    public GearPost() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public GearPost(String uid, String key, String category, String salelocation, int price, String phonenumber, String body, Date publishdate, List<String> images, HashMap<String, Double> postlocation, String postplace) {
        super(uid, key , category, body, publishdate, images, postlocation, postplace);
        this.salelocation = salelocation;
        this.price = price;
        this.phonenumber = phonenumber;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("key", key);
        result.put("category", category);
        result.put("sale-location", salelocation);
        result.put("price", price);
        result.put("phonenumber", phonenumber);
        result.put("body", body);
        result.put("images", images);
        result.put("publishdate", date);
        result.put("postlocation", postlocation);
        result.put("postplace", postplace);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]