package com.shaym.leash.logic.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.ui.home.chat.ChatDialog;
import com.shaym.leash.ui.home.profile.ProfileDialog;

import java.util.List;
import java.util.Objects;

import permissions.dispatcher.RuntimePermissions;

import static com.shaym.leash.logic.utils.CONSTANT.PUSH_TOKEN;
import static com.shaym.leash.logic.utils.CONSTANT.ROLE_USER;
import static com.shaym.leash.logic.utils.CONSTANT.USERS_TABLE;
import static com.shaym.leash.logic.utils.CONSTANT.USER_LAT;
import static com.shaym.leash.logic.utils.CONSTANT.USER_LNG;
import static com.shaym.leash.logic.utils.CONSTANT.USER_STATUS;
public class FireBaseUsersHelper {

    private static final String TAG = "FireBaseUsersHelper";

    private static FireBaseUsersHelper instance = new FireBaseUsersHelper();
    private Profile mUser;
    private String mToken = "";
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


    private FireBaseUsersHelper() {

    }

    public static FireBaseUsersHelper getInstance(){
        return instance;
    }

    public void openChatWindow(Fragment fragment, String uid) {
        try {
            FragmentManager fm = Objects.requireNonNull(fragment.getActivity()).getSupportFragmentManager();
            ChatDialog chatDialog= ChatDialog.newInstance(uid);
            chatDialog.show(fm, chatDialog.getTag());
        }
        catch (Exception e){
            e.printStackTrace();
        }
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

    public void showProfilePopup(final Profile mClickedUser, Fragment fragment) {

        try {
            FragmentManager fm = Objects.requireNonNull(fragment.getActivity()).getSupportFragmentManager();
            ProfileDialog profileDialog= ProfileDialog.newInstance(mClickedUser.getUid());
            profileDialog.show(fm, profileDialog.getTag());
        }
        catch (Exception e){
            e.printStackTrace();
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

                    mUser = new Profile(mCurrentUser.getDisplayName(),ROLE_USER, mCurrentUser.getEmail(), gender, profilepic, 0.0, 0.0,  mToken, mCurrentUser.getUid(), 0);
                    userNameRef.setValue(mUser);
                }
                Log.d(TAG, "onDataChange: User already exists in DB");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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

    public void updateUserStatus(boolean isOnline) {
        try{
            mDatabase.child(USERS_TABLE).child(getUid()).child(USER_STATUS).setValue(isOnline);
        }
        catch (Exception e){
            Log.d(TAG,  e.toString());
        }
    }
}