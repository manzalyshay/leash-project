package com.shaym.leash.ui.forum.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import static com.shaym.leash.logic.CONSTANT.TRIPS_POSTS;

public class TripsFragment extends ForumFragment {
    public static final String TAG = "TripsFragment";

    public TripsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child(TRIPS_POSTS);
    }
}

