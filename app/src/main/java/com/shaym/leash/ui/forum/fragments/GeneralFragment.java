package com.shaym.leash.ui.forum.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.shaym.leash.R;
import com.shaym.leash.ui.forum.ForumFragment;

import static com.shaym.leash.logic.utils.CONSTANT.FORUM_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GENERAL_POSTS;

public class GeneralFragment extends com.shaym.leash.ui.forum.fragments.ForumFragment {
    public static final String TAG = "GeneralFragment";

    public GeneralFragment() {}

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser){

            if (isAdded()) {
                assert getFragmentManager() != null;
                Fragment myFragment = getFragmentManager().findFragmentById(R.id.root_frame_general);
                if (myFragment != null && myFragment.isVisible() && myFragment instanceof PostFragment) {
                    ForumFragment.mFab.setVisibility(View.INVISIBLE);
                }
                else {
                    ForumFragment.mFab.setVisibility(View.VISIBLE);

                }
            }
        }

    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child(FORUM_POSTS).child(GENERAL_POSTS);
    }


}

