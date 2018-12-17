package com.shaym.leash.ui.gear.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import static com.shaym.leash.logic.utils.CONSTANT.USED_GEAR_POSTS;

public class UsedGearFragment extends GearFragment {


    public UsedGearFragment() {}


    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child(USED_GEAR_POSTS);
    }
}
