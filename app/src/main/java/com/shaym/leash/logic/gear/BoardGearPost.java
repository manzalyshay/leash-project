package com.shaym.leash.logic.gear;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties

public class BoardGearPost extends GearPost  {
    public String uid;
    public String contact;
    public String location;
    public int price;
    public String phonenumber;
    public String description;
    public String category;
    public int volume;
    public int height;
    public int width;
    public int year;
    public String model;
    public String manufacturer;
    public List<String> picsref;

    public BoardGearPost(){
    }

    public BoardGearPost(String uid, String category, int volume, int height, int width, int year, String model, String manufacturer, String contact, String location, int price, String phonenumber, String description, List<String> picsrefs) {
        this.uid = uid;
        this.category = category;
        this.volume = volume;
        this.height = height;
        this.width = width;
        this.year = year;
        this.model = model;
        this.manufacturer = manufacturer;
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
        result.put("volume", volume);
        result.put("height", height);
        result.put("width", width);
        result.put("year", year);

        result.put("model", model);
        result.put("manufacturer", manufacturer);
        result.put("contact", contact);
        result.put("location", location);
        result.put("price", price);
        result.put("phonenumber", phonenumber);
        result.put("description", description);
        result.put("images", picsref);

        return result;
    }
}
