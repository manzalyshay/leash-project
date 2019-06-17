package com.shaym.leash.ui.forum;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.chat.Conversation;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;
import com.shaym.leash.logic.utils.PostDiffCallback;
import com.shaym.leash.ui.home.profile.ProfileAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.shaym.leash.logic.utils.CONSTANT.GENERAL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.PROFILE_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.SPOTS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.TRIPS_POSTS;

public class ForumAdapter extends RecyclerView.Adapter<PostViewHolder>{


    private static final String TAG = "ForumAdapter";
    private int selected_position = 0; // You have to set this globally in the Adapter class
    private Profile viewerProfile;
    private Fragment mFragment;
    public String mPostType = GENERAL_POSTS;

    private List<Profile> mAllUsers = new ArrayList<>();
    private List<Post> mGeneralPosts = new ArrayList<>();
    private List<Post> mTripsPosts = new ArrayList<>();
    private List<Post> mSpotsPosts = new ArrayList<>();

    public void setViewerProfile(Profile viewerProfile) {
        this.viewerProfile = viewerProfile;
    }

    public void updateUsers(List<Profile> mAllUsers) {
        this.mAllUsers = mAllUsers;
    }



    ForumAdapter( Fragment fragment) {
        Log.d(TAG, "ForumAdapter: ");
        mFragment = fragment;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        Log.d(TAG, "onCreateViewHolder: ");
        return new PostViewHolder(inflater.inflate(R.layout.item_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        switch (mPostType) {
            case GENERAL_POSTS:
                holder.bindToPost(mGeneralPosts.get(position),mFragment, FireBaseUsersHelper.getInstance().findProfile(mGeneralPosts.get(position).uid, mAllUsers));
                break;
            case TRIPS_POSTS:
                holder.bindToPost(mTripsPosts.get(position),  mFragment,  FireBaseUsersHelper.getInstance().findProfile(mTripsPosts.get(position).uid, mAllUsers));
                break;
            case SPOTS_POSTS:
                holder.bindToPost(mSpotsPosts.get(position),mFragment,  FireBaseUsersHelper.getInstance().findProfile(mSpotsPosts.get(position).uid, mAllUsers));
                break;
        }

    }

    @Override
    public int getItemCount() {
        switch (mPostType) {
            case GENERAL_POSTS:
                return mGeneralPosts.size();
            case TRIPS_POSTS:
                mTripsPosts.size();
            case SPOTS_POSTS:
                mSpotsPosts.size();
        }
        return 0;
    }


    public void updateGeneralPostsData(List<Post> generalPosts) {
        if (!mGeneralPosts.isEmpty()) {
            PostDiffCallback postDiffCallback = new PostDiffCallback(mGeneralPosts, generalPosts);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

            mGeneralPosts.clear();
            mGeneralPosts.addAll(generalPosts);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            mGeneralPosts = generalPosts;
        }

        if (mPostType.equals(GENERAL_POSTS))
            notifyDataSetChanged();
    }

    public void updateTripsPostsData(List<Post> tripsPosts) {
        if (!mTripsPosts.isEmpty()) {
            PostDiffCallback postDiffCallback = new PostDiffCallback(mTripsPosts, tripsPosts);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

            mTripsPosts.clear();
            mTripsPosts.addAll(tripsPosts);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            mTripsPosts = tripsPosts;
        }


        if (mPostType.equals(TRIPS_POSTS))
            notifyDataSetChanged();
    }

    public void updateSpotsPostsData(List<Post> spotsPosts) {
        if (!mSpotsPosts.isEmpty()) {
            PostDiffCallback postDiffCallback = new PostDiffCallback(mSpotsPosts, spotsPosts);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

            mSpotsPosts.clear();
            mSpotsPosts.addAll(spotsPosts);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            mSpotsPosts = spotsPosts;
        }


        if (mPostType.equals(SPOTS_POSTS))
            notifyDataSetChanged();
    }









}