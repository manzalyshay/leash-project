package com.shaym.leash.ui.gear;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.shaym.leash.R;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.gear.GearPost;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;
import com.shaym.leash.logic.utils.GearPostDiffCallback;
import com.shaym.leash.logic.utils.UserDiffCallback;
import com.shaym.leash.ui.forum.ForumAdapter;
import com.shaym.leash.ui.forum.PostViewHolder;

import java.util.ArrayList;
import java.util.List;

import static com.shaym.leash.logic.utils.CONSTANT.BOARDS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.CLOTHING_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.FINS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GENERAL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.LEASHES_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.OTHER_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.SPOTS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.TRIPS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USED_GEAR_POSTS;

public class GearAdapter extends RecyclerView.Adapter<GearPostViewHolder> {

    private static final String TAG = "GearAdapter";
    private Fragment mFragment;
    private List<GearPost> mBoardPosts = new ArrayList<>();
    private List<GearPost> mLeashPosts = new ArrayList<>();
    private List<GearPost> mFinsPosts = new ArrayList<>();
    private List<GearPost> mClothingPosts = new ArrayList<>();
    private List<GearPost> mOtherPosts = new ArrayList<>();
    private List<Profile> mAllUsers = new ArrayList<>();
    public String mCurrentCategory = BOARDS_POSTS;
    private Profile viewerProfile;

    GearAdapter( Fragment fragment) {
        Log.d(TAG, "GearAdapter: ");
        mFragment = fragment;
        setHasStableIds(true);

    }

    public void setViewerProfile(Profile viewerProfile) {
        this.viewerProfile = viewerProfile;
    }
    @NonNull
    @Override
    public GearPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        Log.d(TAG, "onCreateViewHolder: ");
        return new GearPostViewHolder(inflater.inflate(R.layout.item_gear_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GearPostViewHolder holder, int position) {


        switch (mCurrentCategory){
            case BOARDS_POSTS:
                holder.bindToPost(mBoardPosts.get(position), mFragment, FireBaseUsersHelper.getInstance().findProfile(mBoardPosts.get(position).uid, mAllUsers));
                break;
            case LEASHES_POSTS:
                holder.bindToPost(mLeashPosts.get(position), mFragment, FireBaseUsersHelper.getInstance().findProfile(mLeashPosts.get(position).uid, mAllUsers));
                break;
            case FINS_POSTS:
                holder.bindToPost(mFinsPosts.get(position), mFragment, FireBaseUsersHelper.getInstance().findProfile(mFinsPosts.get(position).uid, mAllUsers));
                break;
            case CLOTHING_POSTS:
                holder.bindToPost(mClothingPosts.get(position), mFragment, FireBaseUsersHelper.getInstance().findProfile(mClothingPosts.get(position).uid, mAllUsers));
                break;
            case OTHER_POSTS:
                holder.bindToPost(mOtherPosts.get(position), mFragment, FireBaseUsersHelper.getInstance().findProfile(mOtherPosts.get(position).uid, mAllUsers));
                break;
        }


}

    @Override
    public int getItemCount() {
        switch (mCurrentCategory){
            case BOARDS_POSTS:
                return mBoardPosts.size();
            case LEASHES_POSTS:
                return mLeashPosts.size();
            case FINS_POSTS:
                mFinsPosts.size();
            case CLOTHING_POSTS:
                mClothingPosts.size();
            case OTHER_POSTS:
                mOtherPosts.size();
        }

        return 0;
    }

    public void updateBoardPostsData(List<GearPost> boardPosts) {
        if (!mBoardPosts.isEmpty()) {
            GearPostDiffCallback postDiffCallback = new GearPostDiffCallback(mBoardPosts, boardPosts);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

            mBoardPosts.clear();
            mBoardPosts.addAll(boardPosts);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            mBoardPosts = boardPosts;
        }

        if (mCurrentCategory.equals(BOARDS_POSTS))
            notifyDataSetChanged();
    }


    public void updateleashPostsData(List<GearPost> leashPosts) {
        if (!mLeashPosts.isEmpty()) {
            GearPostDiffCallback postDiffCallback = new GearPostDiffCallback(mLeashPosts, leashPosts);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

            mLeashPosts.clear();
            mLeashPosts.addAll(leashPosts);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            mLeashPosts = leashPosts;
        }

        if (mCurrentCategory.equals(LEASHES_POSTS))
            notifyDataSetChanged();
    }

    public void updatefinsPostsData(List<GearPost> finsposts) {
        if (!mFinsPosts.isEmpty()) {
            GearPostDiffCallback postDiffCallback = new GearPostDiffCallback(mFinsPosts, finsposts);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

            mFinsPosts.clear();
            mFinsPosts.addAll(finsposts);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            mFinsPosts = finsposts;
        }

        if (mCurrentCategory.equals(FINS_POSTS))
            notifyDataSetChanged();
    }

    public void updateclothingPostsData(List<GearPost> clothingposts) {
        if (!mClothingPosts.isEmpty()) {
            GearPostDiffCallback postDiffCallback = new GearPostDiffCallback(mClothingPosts, clothingposts);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

            mClothingPosts.clear();
            mClothingPosts.addAll(clothingposts);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            mClothingPosts = clothingposts;
        }

        if (mCurrentCategory.equals(CLOTHING_POSTS))
            notifyDataSetChanged();
    }

    public void updateotherPostsData(List<GearPost> otherposts) {
        if (!mOtherPosts.isEmpty()) {
            GearPostDiffCallback postDiffCallback = new GearPostDiffCallback(mOtherPosts, otherposts);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

            mOtherPosts.clear();
            mOtherPosts.addAll(otherposts);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            mOtherPosts = otherposts;
        }

        if (mCurrentCategory.equals(OTHER_POSTS))
            notifyDataSetChanged();
    }


    public void updateUsers(List<Profile> allusers){
        if (!mAllUsers.isEmpty()) {
            UserDiffCallback userDiffCallback = new UserDiffCallback(mAllUsers, allusers);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(userDiffCallback);

            mAllUsers.clear();
            mAllUsers.addAll(allusers);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            mAllUsers = allusers;
        }

            notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



}





