package com.shaym.leash.logic.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.gear.GearPost;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;

import static com.shaym.leash.logic.utils.CONSTANT.ALL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.CHAT_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.FORUM_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USED_GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USER_POSTS;

public class FireBasePostsHelper {

    private static final String TAG = "FireBaseUsersHelper";

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

    public int getDaysDifference(Date fromDate)
    {
        if(fromDate==null)
            return 0;

        return (int)( (new Date().getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
    }

    public void writeNewGearPost(String uid, String category, String location, int price, String phonenumber, String description, List<String> mgearpicrefs) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String typekey = mDatabase.child(category).push().getKey();

        GearPost post = new GearPost(uid,  category,  location,  price,  phonenumber,  description,  new Date(), mgearpicrefs);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + ALL_POSTS + "/" + typekey, postValues);
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + USER_POSTS + "/" + uid + "/" + typekey, postValues);
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + category + "/" + typekey, postValues);

        mDatabase.updateChildren(childUpdates);
    }


    // [START write_fan_out]
    public void writeNewForumPost(final String userId, String body, String forum, List<String> images) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String forumkey = mDatabase.child(forum).push().getKey();

        Post post = new Post(userId, forum, body,new Date(), images, 0);
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

                    // Deliver conversations to UI
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    public DatabaseReference getDatabase() {
        return mDatabase;
    }

}