package com.shaym.leash.ui.home.profile;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.shaym.leash.ui.forum.fragments.ForumFragment;
import com.shaym.leash.ui.gear.fragments.GearFragment;

import static com.shaym.leash.logic.CONSTANT.DIRECT_INCOMING_MESSAGES;
import static com.shaym.leash.logic.CONSTANT.GENERAL_POSTS;
import static com.shaym.leash.logic.CONSTANT.USER_GEAR_POSTS;
import static com.shaym.leash.logic.CONSTANT.USER_POSTS;

public class UserInboxFragment extends ForumFragment {
    private static final String TAG = "UserInboxFragment";
    public UserInboxFragment() {}


    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        String uid = getUid();
        return databaseReference.child(DIRECT_INCOMING_MESSAGES).child(uid);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


}

