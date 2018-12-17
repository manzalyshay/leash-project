package com.shaym.leash.ui.home.profile;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.shaym.leash.ui.forum.fragments.ForumFragment;

import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.USER_POSTS;

public class UserPostsFragment extends ForumFragment {
    public static final String TAG = "UserPostsFragment";

    public UserPostsFragment() {}


    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        String uid = getUid();
        return databaseReference.child(USER_POSTS).child(uid);
    }

    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }


}

