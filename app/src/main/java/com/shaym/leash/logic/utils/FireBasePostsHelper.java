package com.shaym.leash.logic.utils;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.MainApplication;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.gear.BoardGearPost;
import com.shaym.leash.logic.gear.ClothingGearPost;
import com.shaym.leash.logic.gear.FinsGearPost;
import com.shaym.leash.logic.gear.GearPost;
import com.shaym.leash.logic.gear.LeashGearPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.shaym.leash.logic.utils.CONSTANT.ALL_GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.ALL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.CHAT_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.FORUM_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USED_GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USER_POSTS;

public class FireBasePostsHelper {

    private static final String TAG = "FireBaseUsersHelper";

    private final static String BROADCAST_USER_CONVERSATIONS = "FirebaseHelper-userConversations";
    private static FireBasePostsHelper instance = new FireBasePostsHelper();

    private List<String> mUserConversations;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


    private FireBasePostsHelper() {

    }

    public static FireBasePostsHelper getInstance(){
        return instance;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }

    // [START write_fan_out]
    public void writeNewBoardGearPost(String uid, String category, int volume, int height, int width, int year, String model, String manufacturer, String contact, String location, int price, String phonenumber, String description, List<String> picsrefs) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String typekey = mDatabase.child(category).push().getKey();

        BoardGearPost post = new BoardGearPost (uid, category,  volume,  height,  width,  year,  model,  manufacturer,  contact,  location,  price,  phonenumber,  description,  picsrefs);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + ALL_GEAR_POSTS + "/" + typekey, postValues);
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + USER_POSTS + "/" + uid + "/" + typekey, postValues);
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + category + "/" + typekey, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    public void  writeNewClothingGearPost(String uid, String category, String manufacturer, String kind, String size,  String contact, String location, int price, String phonenumber, String description, List<String> picsrefs) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String typekey = mDatabase.child(category).push().getKey();

        ClothingGearPost post = new ClothingGearPost( uid,  category,  manufacturer,  kind,  size,   contact,  location,  price,  phonenumber,  description,  picsrefs) ;
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + ALL_GEAR_POSTS + "/" + typekey, postValues);
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + USER_POSTS + "/" + uid + "/" + typekey, postValues);
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + category + "/" + typekey, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    public void writeNewFinsGearPost(String uid, String category, String manufacturer, String contact, String location, int price, String phonenumber, String description, List<String> picsrefs) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String typekey = mDatabase.child(category).push().getKey();

        FinsGearPost post = new FinsGearPost( uid,  category,  manufacturer,  contact,  location,  price,  phonenumber,  description,  picsrefs) ;
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/"  +ALL_GEAR_POSTS + "/" + typekey, postValues);
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + USER_POSTS + "/" + uid + "/" + typekey, postValues);
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + category + "/" + typekey, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    public void writeNewLeashGearPost(String uid, String category,  String manufacturer, String contact, String location, int price, String phonenumber, String description, List<String> picsrefs) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String typekey = mDatabase.child(category).push().getKey();

        LeashGearPost post = new LeashGearPost( uid,  category,   manufacturer,  contact,  location,  price,  phonenumber,  description,  picsrefs);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + ALL_GEAR_POSTS + "/" + typekey, postValues);
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS +"/" + USER_POSTS + "/" + uid + "/" + typekey, postValues);
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + category + "/" + typekey, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    public void writeNewGearPost(String uid, String category, String contact, String location, int price, String phonenumber, String description, List<String> mgearpicrefs) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String typekey = mDatabase.child(category).push().getKey();

        GearPost post = new GearPost(uid,  category,  contact,  location,  price,  phonenumber,  description,  mgearpicrefs);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + ALL_GEAR_POSTS + "/" + typekey, postValues);
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + USER_POSTS + "/" + uid + "/" + typekey, postValues);
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + category + "/" + typekey, postValues);

        mDatabase.updateChildren(childUpdates);
    }


    // [START write_fan_out]
    public void writeNewPost(final String userId, String username, String title, String body, String forum, String attachment) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String forumkey = mDatabase.child(forum).push().getKey();

        Post post = new Post(userId, forum, username, title, body, attachment, 0);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + FORUM_POSTS+ "/" +ALL_POSTS + "/" + forumkey, postValues);
        childUpdates.put("/" + FORUM_POSTS+ "/" + USER_POSTS + "/" + userId + "/" + forumkey, postValues);
        childUpdates.put("/" + FORUM_POSTS+ "/" + forum + "/" + forumkey, postValues);

        mDatabase.updateChildren(childUpdates);
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

    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    public DatabaseReference getDatabase() {
        return mDatabase;
    }

}