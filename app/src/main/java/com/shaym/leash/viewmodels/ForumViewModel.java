package com.shaym.leash.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shaym.leash.data.utils.CONSTANT;
import com.shaym.leash.data.utils.FirebaseQueryLiveData;

import static com.shaym.leash.data.utils.CONSTANT.GENERAL_POSTS;
import static com.shaym.leash.data.utils.CONSTANT.SPOTS_POSTS;
import static com.shaym.leash.data.utils.CONSTANT.TRIPS_POSTS;

public class ForumViewModel extends ViewModel {

    private static final DatabaseReference GENERAL_POSTS_REF =
            FirebaseDatabase.getInstance().getReference().child(CONSTANT.FORUM_POSTS).child(GENERAL_POSTS);
    private static final DatabaseReference SPOTS_POSTS_REF =
            FirebaseDatabase.getInstance().getReference().child(CONSTANT.FORUM_POSTS).child(SPOTS_POSTS);
    private static final DatabaseReference TRIPS_POSTS_REF =
            FirebaseDatabase.getInstance().getReference().child(CONSTANT.FORUM_POSTS).child(TRIPS_POSTS);

    private final FirebaseQueryLiveData generalPostsLiveData = new FirebaseQueryLiveData(GENERAL_POSTS_REF);
    private final FirebaseQueryLiveData spotsPostsLiveData = new FirebaseQueryLiveData(SPOTS_POSTS_REF);
    private final FirebaseQueryLiveData tripsPostsLiveData = new FirebaseQueryLiveData(TRIPS_POSTS_REF);
    private FirebaseQueryLiveData CommentsLiveData;


    public FirebaseQueryLiveData getGeneralPostsLiveData() {
        return generalPostsLiveData;
    }

    public FirebaseQueryLiveData getSpotsPostsLiveData() {
        return spotsPostsLiveData;
    }

    public FirebaseQueryLiveData getTripsPostsLiveData() {
        return tripsPostsLiveData;
    }

    @NonNull
    public FirebaseQueryLiveData getCommentsLiveData() {
        return CommentsLiveData;
    }

    public void setCOMMENTS_LIVE_DATA(DatabaseReference COMMENTS_LIVE_DATA) {
        CommentsLiveData = new FirebaseQueryLiveData(COMMENTS_LIVE_DATA);
    }



}
