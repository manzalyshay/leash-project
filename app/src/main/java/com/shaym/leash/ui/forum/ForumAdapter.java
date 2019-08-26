package com.shaym.leash.ui.forum;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.shaym.leash.R;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;
import com.shaym.leash.logic.utils.PostDiffCallback;
import com.shaym.leash.logic.utils.UserDiffCallback;

import java.util.ArrayList;
import java.util.List;

import static com.shaym.leash.logic.utils.CONSTANT.GENERAL_POSTS;

public class ForumAdapter extends RecyclerView.Adapter<PostViewHolder>{


    private static final String TAG = "ForumAdapter";
    private Fragment mFragment;
    public String mPostType = GENERAL_POSTS;
    private List<Profile> mAllUsers = new ArrayList<>();
    private List<Post> mCurrentData = new ArrayList<>();
    private Profile mUser;

    ForumAdapter( Fragment fragment) {
        Log.d(TAG, "ForumAdapter: ");
        mFragment = fragment;
        setHasStableIds(true);

    }


    public void updateCurrentData(List<Post> currentData){
        if (!mCurrentData.isEmpty()) {
            PostDiffCallback postDiffCallback = new PostDiffCallback(mCurrentData, currentData);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

            mCurrentData.clear();
            mCurrentData.addAll(currentData);
            diffResult.dispatchUpdatesTo(this);
        }
        else {
            mCurrentData.clear();
            mCurrentData.addAll(currentData);
            notifyDataSetChanged();
        }
    }


    public void updateUsers(List<Profile> AllUsers, Profile user) {
        if (!mAllUsers.isEmpty()) {
            UserDiffCallback userDiffCallback = new UserDiffCallback(mAllUsers, AllUsers);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(userDiffCallback);

            mAllUsers.clear();
            mAllUsers.addAll(AllUsers);
            diffResult.dispatchUpdatesTo(this);

        }
        else {
            mAllUsers.addAll(AllUsers);
            notifyDataSetChanged();
        }


        mUser = user;




    }


    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        Log.d(TAG, "onCreateViewHolder: ");
        return new PostViewHolder(inflater.inflate(R.layout.item_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            // Perform a full update
            onBindViewHolder(holder, position);
        } else {
            // Perform a partial update
            for (Object payload : payloads) {
                if (payload instanceof Boolean) {
                    holder.showCommentForm((Boolean)payload);
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + position);
        holder.bindToPost(mCurrentData.get(position),mFragment, mUser, mAllUsers);

    }

    @Override
    public int getItemCount() {
        return mCurrentData.size();
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