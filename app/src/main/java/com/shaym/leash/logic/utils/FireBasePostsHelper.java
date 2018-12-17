package com.shaym.leash.logic.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.MainApplication;
import com.shaym.leash.logic.chat.ChatMessage;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.gear.GearPost;
import com.shaym.leash.logic.user.Profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.ALL_GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.ALL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.CHAT_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.USERS_TABLE;
import static com.shaym.leash.logic.utils.CONSTANT.USER_GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USER_GEAR_POSTS_AMOUNT;
import static com.shaym.leash.logic.utils.CONSTANT.USER_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USER_POSTS_AMOUNT;

public class FireBasePostsHelper {

    private static final String TAG = "FireBaseUsersHelper";
    private final static String BROADCAST_USER = "FirebaseHelper-mUser";
    private final static String BROADCAST_USER_BY_ID = "FirebaseHelper-USERbyID";

    private final static String USER_OBJ = "USEROBJ";

    private final static String BROADCAST_ALL_USERS = "FirebaseHelper-allUsers";
    private final static String BROADCAST_USER_CONVERSATIONS = "FirebaseHelper-userConversations";
    private static FireBasePostsHelper instance = new FireBasePostsHelper();
    private Profile mUser;
    private Profile mProfileByID;

    private List<Profile> mAllUsers;
    private List<String> mUserConversations;
    private String mToken = "";
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private int mUserTotalUnreadMsgs;
    private int mUserTotalConversations;

    private FireBasePostsHelper() {

    }

    public static FireBasePostsHelper getInstance(){
        return instance;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }

    // [START write_fan_out]
    public void writeNewGearPost(String uid, String type, String author, String title, int price, String phonenumber, String description, String imageurl) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String typekey = mDatabase.child(type).push().getKey();

        GearPost post = new GearPost(uid, type, author, title, price, phonenumber, description, imageurl);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + ALL_GEAR_POSTS + "/" + typekey, postValues);
        childUpdates.put("/" + USER_GEAR_POSTS + "/" + uid + "/" + typekey, postValues);
        childUpdates.put("/" + type + "/" + typekey, postValues);

        mDatabase.updateChildren(childUpdates);
    }
    // [START write_fan_out]
    public void writeNewPost(final String userId, String username, String title, String body, String forum, String attachment) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String forumkey = mDatabase.child(forum).push().getKey();

        Post post = new Post(userId, forum, username, title, body, attachment);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + ALL_POSTS + "/" + forumkey, postValues);
        childUpdates.put("/" + USER_POSTS + "/" + userId + "/" + forumkey, postValues);
        childUpdates.put("/" + forum + "/" + forumkey, postValues);


        mDatabase.updateChildren(childUpdates);
    }


    public void loadUserConversations(){
        if (mUserConversations != null && !mUserConversations.isEmpty()) {
            notifyUIwithUserConversations();
        }
        else {
            getCurrentUserConversations();
        }
    }

    private void getCurrentUserConversations() {

        mUserConversations = new ArrayList<>();

        DatabaseReference mConversationsRef = mDatabase.child(CHAT_CONVERSATIONS);
        mConversationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (Objects.requireNonNull(ds.getKey()).contains(getUid())) {
                        mUserConversations.add(ds.getKey());

                    }
                }

                if (!mUserConversations.isEmpty()) {
                    notifyUIwithUserConversations();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void notifyUIwithUserConversations() {
        Log.d("sender", "Broadcasting User Conversations");

        Intent intent1 = new Intent(BROADCAST_USER_CONVERSATIONS);

        LocalBroadcastManager.getInstance(MainApplication.getInstace().getApplicationContext()).sendBroadcast(intent1);
    }



    public List<String> pullUserConversations(){

        return mUserConversations;

    }





    private void loadPostsAmount() {
        DatabaseReference mUserForumPostsRef = mDatabase.child(USER_POSTS).child(getUid());
        ValueEventListener mUserPostsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Posts");
                long amount = dataSnapshot.getChildrenCount();
                int val = (int)amount;
                mUser.setforumpostsamount(val);
                mDatabase.child(USERS_TABLE).child(getUid()).child(USER_POSTS_AMOUNT).setValue(amount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mUserForumPostsRef.addValueEventListener(mUserPostsEventListener);
    }


    private void loadGearPostsAmount() {
        DatabaseReference mUserGearPostsref = mDatabase.child(USER_GEAR_POSTS).child(getUid());
        ValueEventListener mUserGearPostsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: GearPosts");
                long amount = dataSnapshot.getChildrenCount();
                int val = (int)amount;
                mUser.setGearpostsamount(val);
                mDatabase.child(USERS_TABLE).child(getUid()).child(USER_GEAR_POSTS_AMOUNT).setValue(amount);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mUserGearPostsref.addValueEventListener(mUserGearPostsEventListener);
    }


    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    public DatabaseReference getDatabase() {
        return mDatabase;
    }

}