package com.shaym.leash.ui.home.profile;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.shaym.leash.ui.forum.fragments.ForumFragment;

import static com.shaym.leash.logic.CONSTANT.DIRECT_INCOMING_MESSAGES;
import static com.shaym.leash.logic.CONSTANT.DIRECT_OUTGOING_MESSAGES;

public class UserOutboxFragment extends ForumFragment {
    private static final String TAG = "UserOutboxFragment";
    public UserOutboxFragment() {}


    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        String uid = getUid();
        return databaseReference.child(DIRECT_OUTGOING_MESSAGES).child(uid);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


}

