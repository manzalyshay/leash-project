package com.shaym.leash.ui.home.profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.shaym.leash.logic.utils.FirebaseQueryLiveData;

import java.util.Objects;

public class ProfileViewModel extends ViewModel {


    private DatabaseReference CONVERSATION_LIVE_DATA;

    private FirebaseQueryLiveData ConversationLiveData;
    private DatabaseReference ALL_USER_POSTS_LIVE_DATA;
    private FirebaseQueryLiveData AllUserPostsLiveData;
    private DatabaseReference ALL_USER_GEARPOSTS_LIVE_DATA;
    private FirebaseQueryLiveData AllUserGearPostsLiveData;
    private DatabaseReference ALL_USER_CONVERSATIONS_LIVE_DATA;
    private FirebaseQueryLiveData AllUserConversationsLiveData;



    @NonNull
    LiveData<DataSnapshot> getConversationLiveData() {
        return ConversationLiveData;
    }

    public static String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    void setCONVERSATION_LIVE_DATA(DatabaseReference CONVERSATION_LIVE_DATA) {
        this.CONVERSATION_LIVE_DATA = CONVERSATION_LIVE_DATA;
        ConversationLiveData = new FirebaseQueryLiveData(CONVERSATION_LIVE_DATA);
    }

    void setALL_USER_POSTS_LIVE_DATA(DatabaseReference ALL_USER_POSTS_LIVE_DATA) {
        this.ALL_USER_POSTS_LIVE_DATA = ALL_USER_POSTS_LIVE_DATA;
        AllUserPostsLiveData = new FirebaseQueryLiveData(ALL_USER_POSTS_LIVE_DATA);

    }

    public FirebaseQueryLiveData getAllUserPostsLiveData() {
        return AllUserPostsLiveData;
    }

    public void setALL_USER_GEARPOSTS_LIVE_DATA(DatabaseReference ALL_USER_GEARPOSTS_LIVE_DATA) {
        this.ALL_USER_GEARPOSTS_LIVE_DATA = ALL_USER_GEARPOSTS_LIVE_DATA;
        AllUserGearPostsLiveData = new FirebaseQueryLiveData(ALL_USER_GEARPOSTS_LIVE_DATA);
    }

    public FirebaseQueryLiveData getAllUserGearPostsLiveData() {
        return AllUserGearPostsLiveData;
    }

    public void setALL_USER_CONVERSATIONS_LIVE_DATA(DatabaseReference ALL_USER_CONVERSATIONS_LIVE_DATA) {
        this.ALL_USER_CONVERSATIONS_LIVE_DATA = ALL_USER_CONVERSATIONS_LIVE_DATA;
        AllUserConversationsLiveData = new FirebaseQueryLiveData(ALL_USER_CONVERSATIONS_LIVE_DATA);
    }

    public FirebaseQueryLiveData getAllUserConversationsLiveData() {
        return AllUserConversationsLiveData;
    }
}