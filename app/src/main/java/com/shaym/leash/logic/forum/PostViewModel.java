package com.shaym.leash.logic.forum;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.shaym.leash.logic.utils.FirebaseQueryLiveData;

public class PostViewModel extends ViewModel {


    private DatabaseReference POST_LIVE_DATA;
    private FirebaseQueryLiveData PostLiveData;
    private DatabaseReference COMMENTS_LIVE_DATA;
    private FirebaseQueryLiveData CommentsLiveData;


    @NonNull
    public LiveData<DataSnapshot> getPostLiveData() {
        return PostLiveData;
    }

    public void setPOST_LIVE_DATA(DatabaseReference POST_LIVE_DATA) {
        this.POST_LIVE_DATA = POST_LIVE_DATA;
        PostLiveData = new FirebaseQueryLiveData(POST_LIVE_DATA);
    }

    @NonNull
    public FirebaseQueryLiveData getCommentsLiveData() {
        return CommentsLiveData;
    }

    public void setCOMMENTS_LIVE_DATA(DatabaseReference COMMENTS_LIVE_DATA) {
        this.COMMENTS_LIVE_DATA = COMMENTS_LIVE_DATA;
        CommentsLiveData = new FirebaseQueryLiveData(COMMENTS_LIVE_DATA);
    }



}



