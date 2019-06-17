package com.shaym.leash.logic.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLng;
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
import static com.shaym.leash.logic.utils.CONSTANT.FORUM_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.PUSH_TOKEN;
import static com.shaym.leash.logic.utils.CONSTANT.USERS_TABLE;
import static com.shaym.leash.logic.utils.CONSTANT.USER_BUNDLE;
import static com.shaym.leash.logic.utils.CONSTANT.USER_GEAR_POSTS_AMOUNT;
import static com.shaym.leash.logic.utils.CONSTANT.USER_LAT;
import static com.shaym.leash.logic.utils.CONSTANT.USER_LNG;
import static com.shaym.leash.logic.utils.CONSTANT.USER_OBJ;
import static com.shaym.leash.logic.utils.CONSTANT.USER_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USER_POSTS_AMOUNT;

public class FireBaseUsersHelper {

    private static final String TAG = "FireBaseUsersHelper";
    private static final String UNREAD_COUNTER = "UNREAD_COUNTER";

    private static FireBaseUsersHelper instance = new FireBaseUsersHelper();
    private Profile mUser;
    private Profile mProfileByID;
    private String mToken = "";
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


    private FireBaseUsersHelper() {

    }

    public static FireBaseUsersHelper getInstance(){
        return instance;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }


    public void loadProfileByID(String userid, UsersHelperListener listener) {
            DatabaseReference mProfileByIDRef = mDatabase.child(USERS_TABLE).child(userid);

           mProfileByIDRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        mProfileByID = dataSnapshot.getValue(Profile.class);
                        assert mProfileByID != null;
                        Log.d(TAG, "onDataChange: "+ mProfileByID.toString());
                        listener.onUserByIDLoaded(mProfileByID);
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

    public void updateUserLocation(LatLng loc){
        try{
            mDatabase.child(USERS_TABLE).child(getUid()).child(USER_LAT).setValue(loc.latitude);
            mDatabase.child(USERS_TABLE).child(getUid()).child(USER_LNG).setValue(loc.longitude);
        }
        catch (Exception e){
            Log.d(TAG,  e.toString());
        }
    }

    public void updateUserpushToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "getInstanceId failed", task.getException());
                                return;
                            }

                            try {
                                // Get new Instance ID token
                                mToken = Objects.requireNonNull(task.getResult()).getToken();
                                saveUserPushToken(mToken);

                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                );

    }



    public void saveUserPushToken(String token){
        try{
            mDatabase.child(USERS_TABLE).child(getUid()).child(PUSH_TOKEN).setValue(token);
        }
        catch (Exception e){
            Log.d(TAG,  e.toString());
        }
    }

    private void saveUser(Profile mUser){
        try{
            mDatabase.child(USERS_TABLE).child(getUid()).setValue(mUser);
        }
        catch (Exception e){
            Log.d(TAG,   e.toString());
        }
    }

    public void saveUserByID(String userUid, Profile userprofile){
        try{
            mDatabase.child(USERS_TABLE).child(userUid).setValue(userprofile);
        }
        catch (Exception e){
            Log.d(TAG, "saveUser: " + e.toString());
        }
    }

    public void createUserInDB(String profilepic, String gender, FirebaseUser mCurrentUser) {
        DatabaseReference userNameRef = mDatabase.child(USERS_TABLE).child(mCurrentUser.getUid());

        userNameRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.d(TAG, "onDataChange: User does not exist in DB");

                    mUser = new Profile(mCurrentUser.getDisplayName(), mCurrentUser.getEmail(), gender, profilepic, 0.0, 0.0,  mToken, mCurrentUser.getUid(), 0);
                    userNameRef.setValue(mUser);
                }
                Log.d(TAG, "onDataChange: User already exists in DB");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.toString());
            }
        });
    }

    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    public DatabaseReference getDatabase() {
        return mDatabase;
    }

    public Profile findProfile(String idToFind, List<Profile> mAllUsers) {
        for (int i=0; i<mAllUsers.size(); i++){
            if (mAllUsers.get(i).getUid().equals(idToFind)){
                return  mAllUsers.get(i);
            }
        }
        return null;
    }
}