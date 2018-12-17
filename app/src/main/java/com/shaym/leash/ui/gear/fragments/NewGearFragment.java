package com.shaym.leash.ui.gear.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import static com.shaym.leash.logic.utils.CONSTANT.NEW_GEAR_POSTS;

public class NewGearFragment extends GearFragment {

    public NewGearFragment() {}


    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child(NEW_GEAR_POSTS);
    }
}
