package com.shaym.leash.ui.forum.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.shaym.leash.R;

import static com.shaym.leash.logic.utils.CONSTANT.FORUM_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.TRIPS_POSTS;

public class TripsFragment extends com.shaym.leash.ui.forum.fragments.ForumFragment {
    public static final String TAG = "TripsFragment";

    public TripsFragment() {}


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser){

            if (isAdded()) {
                assert getFragmentManager() != null;
                Fragment myFragment = getFragmentManager().findFragmentById(R.id.root_frame_trips);
                if (myFragment != null && myFragment.isVisible() && myFragment instanceof PostFragment) {
                    com.shaym.leash.ui.forum.ForumFragment.mFab.setVisibility(View.INVISIBLE);
                }
                else {
                    com.shaym.leash.ui.forum.ForumFragment.mFab.setVisibility(View.VISIBLE);

                }
            }
        }

    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child(FORUM_POSTS).child(TRIPS_POSTS);
    }
}

