package com.shaym.leash.ui.forum.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import static com.shaym.leash.logic.utils.CONSTANT.GENERAL_POSTS;

public class GeneralFragment extends ForumFragment {
    public static final String TAG = "GeneralFragment";

    public GeneralFragment() {}


    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child(GENERAL_POSTS);
    }


}

