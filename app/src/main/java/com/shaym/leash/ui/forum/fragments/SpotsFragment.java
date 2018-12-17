package com.shaym.leash.ui.forum.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import static com.shaym.leash.logic.utils.CONSTANT.SPOTS_POSTS;

public class SpotsFragment extends ForumFragment {
    public static final String TAG = "SpotsFragment";

    public SpotsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child(SPOTS_POSTS);
    }
}

