package com.shaym.leash.ui.home.profile;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.shaym.leash.ui.gear.fragments.GearFragment;

import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.USER_GEAR_POSTS;

public class UserGearPostsFragment extends GearFragment {
    public static final String TAG = "UserGearPostsFragment";

    public UserGearPostsFragment() {}


    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        String uid = getUid();
        return databaseReference.child(USER_GEAR_POSTS).child(uid);
    }

    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }


}

