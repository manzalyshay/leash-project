package com.shaym.leash.ui.gear;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.shaym.leash.R;
import com.shaym.leash.models.GearPost;
import com.shaym.leash.models.Profile;
import com.shaym.leash.data.utils.FireBaseUsersHelper;
import com.shaym.leash.data.utils.GearPostDiffCallback;
import com.shaym.leash.data.utils.UserDiffCallback;

import java.util.ArrayList;
import java.util.List;

import static com.shaym.leash.data.utils.CONSTANT.BOARDS_POSTS;
import static com.shaym.leash.data.utils.CONSTANT.FCS_POSTS;
import static com.shaym.leash.data.utils.CONSTANT.INTERSURF_POSTS;
import static com.shaym.leash.data.utils.CONSTANT.ROLE_STORE_FCS;
import static com.shaym.leash.data.utils.CONSTANT.ROLE_STORE_INTERSURF;

public class GearAdapter extends RecyclerView.Adapter<GearPostViewHolder> {

    private static final String TAG = "GearAdapter";
    private Fragment mFragment;
    private List<GearPost> mCurrentData = new ArrayList<>();
    private List<Profile> mAllUsers = new ArrayList<>();
    String mCurrentCategory = BOARDS_POSTS;
    private Profile mUser;
    private RelativeLayout mContainer;

    GearAdapter(Fragment fragment, RelativeLayout container) {
        Log.d(TAG, "GearAdapter: ");
        mFragment = fragment;
        mContainer = container;
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
        holder.bindToPost(mCurrentData.get(position), mFragment , FireBaseUsersHelper.getInstance().findProfile(mCurrentData.get(position).uid, mAllUsers),mUser,  mAllUsers, mContainer);

    }

    @Override
    public int getItemCount() {
        return mCurrentData.size();
    }

    void updateCurrentData(List<GearPost> currentData){
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


    void updateUsers(List<Profile> AllUsers, Profile user) {
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


    void filter(String filter) {
        String role;
        List<GearPost> filteredposts = new ArrayList<>();

        if (filter.equals(INTERSURF_POSTS)){
            role = ROLE_STORE_INTERSURF;
        }
        else if (filter.equals(FCS_POSTS)) {
            role = ROLE_STORE_FCS;
        }
        else {
            role ="";
        }

        for (int i=0; i<mAllUsers.size(); i++){
            if (mAllUsers.get(i).getRole().equals(role)){
                filteredposts = getPostsBy(mAllUsers.get(i));
            }
        }

        mCurrentData.clear();
        mCurrentData.addAll(filteredposts);
    }



    private List<GearPost> getPostsBy(Profile profile) {

        List<GearPost> list = new ArrayList<>();

        for (int i=0; i<mCurrentData.size(); i++){
            if (mCurrentData.get(i).uid.equals(profile.getUid())){
                list.add(mCurrentData.get(i));
            }
        }

        return list;
    }
}





