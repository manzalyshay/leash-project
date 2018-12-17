package com.shaym.leash.logic.utils;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
import com.shaym.leash.logic.aroundme.CircleTransform;
import com.shaym.leash.logic.chat.ChatMessage;
import com.shaym.leash.logic.user.Profile;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.CHAT_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.USERS_TABLE;
import static com.shaym.leash.logic.utils.CONSTANT.USER_GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USER_GEAR_POSTS_AMOUNT;
import static com.shaym.leash.logic.utils.CONSTANT.USER_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USER_POSTS_AMOUNT;

public class FireBaseUsersHelper {

    private static final String TAG = "FireBaseUsersHelper";
    public final static String BROADCAST_USER = "FirebaseHelper-mUser";
    public final static String BROADCAST_USER_BY_ID = "FirebaseHelper-USERbyID";

    private final static String USER_OBJ = "USEROBJ";

    public final static String BROADCAST_ALL_USERS = "FirebaseHelper-allUsers";
    public final static String BROADCAST_USER_CONVERSATIONS = "FirebaseHelper-userConversations";
    private static FireBaseUsersHelper instance = new FireBaseUsersHelper();
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

    private FireBaseUsersHelper() {

    }

    public static FireBaseUsersHelper getInstance(){
        return instance;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }

    public void loadCurrentUserProfile(){
        if (mUser != null && mUser.getUid().equals(getUid())) {
            notifyUIwithCurrentUser();
        }
        else {
            getCurrentUserProfile();

        }
    }

    public void loadUserProfileData(){
        loadInboxData();
        loadPostsAmount();
        loadGearPostsAmount();
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


    public void loadAllUserProfiles(){
        if (mAllUsers != null && !mAllUsers.isEmpty()) {
            notifyUIwithAllUsers();
        }
        else {
            getAllUsersProfiles();
        }
    }

    public void attachProfilePic(String userpicref){
        mUser.setAvatarURL(userpicref);
        mDatabase.child(USERS_TABLE).child(getUid()).setValue(mUser)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: User Profile pic updated"))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: User Profile  update failed"));
    }


    private void getAllUsersProfiles(){
        mAllUsers = new ArrayList<>();

        DatabaseReference mAllUsersRef = mDatabase.child(USERS_TABLE);
        ValueEventListener mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Profile user = ds.getValue(Profile.class);
                    mAllUsers.add(user);
                }

                if (!mAllUsers.isEmpty()){
                    notifyUIwithAllUsers();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+ databaseError);
            }
        };
        mAllUsersRef.addListenerForSingleValueEvent(mValueEventListener);

    }



    public void LoadUserPic(String url, ImageView pic, ProgressBar progressBar, int size) {
        Log.d(TAG, "LoadUserPic: ");
        if (!mUser.getAvatarURL().isEmpty()) {

            if (mUser.getAvatarURL().charAt(0) == 'p') {
                FireBaseUsersHelper.getInstance().getStorageReference().child(mUser.getAvatarURL()).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).resize(size, size).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new CircleTransform()).into(pic, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(uri).resize(size, size).centerCrop().transform(new CircleTransform()).into(pic, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                                //Try again online if cache failed

                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        });

                    }

                }));
            }
            else {
                Picasso.get().load(Uri.parse(mUser.getAvatarURL())).resize(size, size).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new CircleTransform()).into(pic, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(Uri.parse(mUser.getAvatarURL())).resize(size, size).centerCrop().transform(new CircleTransform()).into(pic, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                                //Try again online if cache failed

                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        });

                    }

                });
            }
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);

        }

    }



    public void loadProfileByID(String userid) {
            DatabaseReference mProfileByIDRef = mDatabase.child(USERS_TABLE).child(userid);

           mProfileByIDRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        mProfileByID = dataSnapshot.getValue(Profile.class);
                        assert mProfileByID != null;
                        Log.d(TAG, "onDataChange: "+ mProfileByID.toString());
                        notifyUIwithUserByID();

                    }
                    else {
                        Log.d(TAG, "onDataChange: User does not exists in DB");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: "+ databaseError);
                }
            });

    }

    private void notifyUIwithUserByID() {
        Log.d("sender", "Broadcasting user by id ");

        Intent intent1 = new Intent(BROADCAST_USER_BY_ID);
        Bundle args = new Bundle();

        // You can also include some extra data.
        args.putSerializable(USER_OBJ,mProfileByID);
        intent1.putExtra("DATA",args);

        LocalBroadcastManager.getInstance(MainApplication.getInstace().getApplicationContext()).sendBroadcast(intent1);
    }

    private void getCurrentUserProfile(){
        if (mUser == null || !mUser.getUid().equals(getUid())) {

            DatabaseReference mUserNameRef = mDatabase.child(USERS_TABLE).child(getUid());
            ValueEventListener mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        mUser = dataSnapshot.getValue(Profile.class);
                        assert mUser != null;
                        Log.d(TAG, "onDataChange: "+ mUser.toString());

                        FirebaseInstanceId.getInstance().getInstanceId()
                                .addOnCompleteListener(task -> {
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "getInstanceId failed", task.getException());
                                        return;
                                    }

                                    try {
                                        // Get new Instance ID token
                                        mToken = Objects.requireNonNull(task.getResult()).getToken();
                                        updateUserPushToken(mToken);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                });
                        notifyUIwithCurrentUser();
                        saveUser();

                    }
                    else {
                        Log.d(TAG, "onDataChange: User does not exists in DB");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: "+ databaseError);
                }
            };
            mUserNameRef.addListenerForSingleValueEvent(mValueEventListener);
        }
        else {
            notifyUIwithCurrentUser();

        }

    }

    private void notifyUIwithCurrentUser() {
        Log.d("sender", "Broadcasting mUser ");

        Intent intent1 = new Intent(BROADCAST_USER);
        Bundle args = new Bundle();

        // You can also include some extra data.
        args.putSerializable(USER_OBJ,mUser);
        intent1.putExtra("DATA",args);

        LocalBroadcastManager.getInstance(MainApplication.getInstace().getApplicationContext()).sendBroadcast(intent1);
    }

    public List<Profile> pullUsers(){

        return mAllUsers;

    }

    public List<String> pullUserConversations(){

        return mUserConversations;

    }

    private void notifyUIwithAllUsers() {
        Log.d("sender", "Broadcasting all users");

        Intent intent1 = new Intent(BROADCAST_ALL_USERS);

        LocalBroadcastManager.getInstance(MainApplication.getInstace().getApplicationContext()).sendBroadcast(intent1);
    }


    public void updateUserLocation(double lat, double lng){

        if (mUser != null) {
            mUser.setcurrentlat(lat);
            mUser.setcurrentlng(lng);
            saveUser();
        }
    }

    public void updateUserPushToken(String token){
        if (mUser != null) {
            mUser.setPushtoken(token);
            saveUser();
        }
    }

    private void saveUser(){
        try{
            mDatabase.child(USERS_TABLE).child(getUid()).setValue(mUser);
            notifyUIwithCurrentUser();
        }
        catch (Exception e){
            Log.d(TAG, "saveUser: " + e.toString());
        }
    }

    public void createUserInDB(String profilepic) {
        DatabaseReference userNameRef = mDatabase.child(USERS_TABLE).child(getUid());
        final FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.d(TAG, "onDataChange: User does not exist in DB");

                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                mToken = Objects.requireNonNull(task.getResult()).getToken();

                            });

                    assert mCurrentUser != null;
                    mUser = new Profile(mCurrentUser.getEmail(), false, "000-0000000", true, mCurrentUser.getUid(), mCurrentUser.getDisplayName(), 0.0, 0.0,
                            0, 0, 0, 0, profilepic, mToken);

                    saveUser();
                }
                Log.d(TAG, "onDataChange: User already exists in DB");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.toString());
            }
        };
        userNameRef.addListenerForSingleValueEvent(eventListener);
    }

    private void loadInboxData(){
        mDatabase.child(CHAT_CONVERSATIONS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserTotalUnreadMsgs = 0;
                mUserTotalConversations = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (Objects.requireNonNull(snapshot.getKey()).contains(getUid())) {
                        mUserTotalConversations++;
                        mDatabase.child(CHAT_CONVERSATIONS).child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            int unread = 0;

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    ChatMessage message = snapshot.getValue(ChatMessage.class);
                                    assert message != null;
                                    if (!message.getIsread() && (!message.getUid().equals(getUid()))){
                                        unread++;
                                    }

                                }

                                mUserTotalUnreadMsgs += unread;
                                mUser.setConversationssmount(mUserTotalConversations);
                                mUser.setUnreadmsgsamount(mUserTotalUnreadMsgs);
                                saveUser();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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
                saveUser();
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