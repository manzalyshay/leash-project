package com.shaym.leash.ui.gear.fragments;

import android.support.v4.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import static com.shaym.leash.logic.CONSTANT.USED_GEAR_POSTS;

public class UsedGearFragment extends GearFragment {
    private static final String TAG = "UsedGearFragment";


    public UsedGearFragment() {}


    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child(USED_GEAR_POSTS);
    }
}
