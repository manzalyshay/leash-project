package com.shaym.leash.logic.gear;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class GearPost {

    public String uid;
    public String contact;
    public String location;
    public int price;
    public String phonenumber;

    public String description;
    public String category;

    public List<String> picsref;



    public GearPost() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public GearPost(String uid, String category, String contact, String location, int price, String phonenumber, String description, List<String> picsrefs) {
        this.uid = uid;
        this.category = category;
        this.contact = contact;
        this.location = location;
        this.price = price;
        this.phonenumber = phonenumber;
        this.description = description;
        this.picsref = picsrefs;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("category", category);
        result.put("contact", contact);
        result.put("location", location);
        result.put("price", price);
        result.put("phonenumber", phonenumber);
        result.put("description", description);
        result.put("images", picsref);



        return result;
    }
    // [END post_to_map]

}
// [END post_class]