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
import com.shaym.leash.logic.utils.PostDiffCallback;
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
    private List<GearPost> mCurrentData = new ArrayList<>();
    private List<Profile> mAllUsers = new ArrayList<>();
    public String mCurrentCategory = BOARDS_POSTS;
    private Profile mUser;

    GearAdapter(Fragment fragment) {
        Log.d(TAG, "GearAdapter: ");
        mFragment = fragment;

        setHasStableIds(true);

    }

    @Override
    public void onBindViewHolder(@NonNull GearPostViewHolder holder, int position, @NonNull List<Object> payloads) {
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

    @NonNull
    @Override
    public GearPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        Log.d(TAG, "onCreateViewHolder: ");
        return new GearPostViewHolder(inflater.inflate(R.layout.item_gear_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GearPostViewHolder holder, int position) {
        holder.bindToPost(mCurrentData.get(position), mFragment , FireBaseUsersHelper.getInstance().findProfile(mCurrentData.get(position).uid, mAllUsers),mUser,  mAllUsers);

    }

    @Override
    public int getItemCount() {
        return mCurrentData.size();
    }

    public void updateCurrentData(List<GearPost> currentData){
        if (!mCurrentData.isEmpty()) {
            GearPostDiffCallback gearPostDiffCallback = new GearPostDiffCallback(mCurrentData, currentData);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(gearPostDiffCallback);

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


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void filter(String filter) {
    }
}





