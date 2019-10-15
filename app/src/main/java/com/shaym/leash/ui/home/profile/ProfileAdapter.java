package com.shaym.leash.ui.home.profile;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.shaym.leash.R;
import com.shaym.leash.models.Conversation;
import com.shaym.leash.models.Post;
import com.shaym.leash.models.GearPost;
import com.shaym.leash.models.Profile;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.shaym.leash.data.utils.CONSTANT.PROFILE_CONVERSATIONS;
import static com.shaym.leash.data.utils.CONSTANT.PROFILE_FORUM_POSTS;
import static com.shaym.leash.data.utils.CONSTANT.PROFILE_GEAR_POSTS;

public class ProfileAdapter extends RecyclerView.Adapter {
    private static final String TAG = "ProfileAdapter";

    public String mViewType = PROFILE_FORUM_POSTS;

    private List<Profile> mAllUsers = new ArrayList<>();
    private List<Post> mAllPosts = new ArrayList<>();
    private List<GearPost> mAllGearPosts = new ArrayList<>();
    private List<Conversation> mAllConversations = new ArrayList<>();
    private Profile mUser;
    private ProfileViewModel mProfileViewModel;
    private int selected_position = 0; // You have to set this globally in the Adapter class
    private Fragment mFragment;

    private static final int VIEW_TYPE_FORUM = 1;
    private static final int VIEW_TYPE_GEAR = 2;
    private static final int VIEW_TYPE_CONVERSATION = 3;


    ProfileAdapter( ProfileViewModel profileViewModel, Fragment fragment) {
        mFragment = fragment;
        mProfileViewModel = profileViewModel;
    }



    public void setAllUsers(List<Profile> mAllUsers) {
        this.mAllUsers = mAllUsers;
        notifyDataSetChanged();

    }


    public void setUser(Profile mUser) {
        this.mUser = mUser;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        Log.d(TAG, "onCreateViewHolder: ");


        switch (viewType){
            case VIEW_TYPE_FORUM:
                return new ProfileForumPostViewHolder(inflater.inflate(R.layout.item_post_profile, parent, false));
            case VIEW_TYPE_GEAR:
                return new ProfileGearPostViewHolder(inflater.inflate(R.layout.item_gearpost_profile, parent, false));
            case VIEW_TYPE_CONVERSATION:
                return new ProfileConversationViewHolder(inflater.inflate(R.layout.item_post, parent, false));

        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        switch (mViewType){
            case PROFILE_FORUM_POSTS:
                return VIEW_TYPE_FORUM;
            case PROFILE_GEAR_POSTS:
                return VIEW_TYPE_GEAR;
            case PROFILE_CONVERSATIONS:
                return VIEW_TYPE_CONVERSATION;
        }

        return -1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (mViewType){
            case PROFILE_FORUM_POSTS:
                ((ProfileForumPostViewHolder)(holder)).bindToPost(mAllPosts.get(position));
                break;
            case PROFILE_GEAR_POSTS:
                ((ProfileGearPostViewHolder)(holder)).bindToPost(mAllGearPosts.get(position), mFragment);
                break;
            case PROFILE_CONVERSATIONS:
                if (!mAllUsers.isEmpty() && mUser !=null){
                Profile conversationPartner = getConversationPartnerProfile(mAllUsers, mAllConversations.get(position));
                ProfileConversationViewHolder viewholder = (ProfileConversationViewHolder)holder;
                viewholder.bindToPost(mAllConversations.get(position), conversationPartner, mProfileViewModel, mFragment);}
                break;

        }

        if (selected_position == position){
//            holder.itemView.setBackgroundResource(R.drawable.underline_cameras);
        }
        else {
            holder.itemView.setBackgroundResource(0);

        }
    }


    @Override
    public int getItemCount() {
        switch (mViewType){
            case PROFILE_FORUM_POSTS:
                return mAllPosts.size();
            case PROFILE_GEAR_POSTS:
                return mAllGearPosts.size();
            case PROFILE_CONVERSATIONS:
                return mAllConversations.size();


        }
        return 0;
    }



    private Profile getConversationPartnerProfile(List<Profile> mAllUsers, Conversation conversation) {
        String idToFind;

        if (mUser.getUid().equals(conversation.initiatorUID)){
            idToFind = conversation.receiverUID;
        }
        else{
            idToFind = conversation.initiatorUID;
        }

        for (int i=0; i<mAllUsers.size(); i++){
            if (mAllUsers.get(i).getUid().equals(idToFind)){
                return  mAllUsers.get(i);
            }
        }
        return null;
    }


    public void updateForumData(List<Post> AllPosts) {
        if (mAllPosts != null) {
            PostDiffCallback postDiffCallback = new PostDiffCallback(mAllPosts, AllPosts);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

            mAllPosts.clear();
            mAllPosts.addAll(AllPosts);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            mAllPosts = AllPosts;
        }
        notifyDataSetChanged();

    }

    void updateGearData(List<GearPost> AllGearPosts) {
        if (mAllGearPosts != null) {
            GearPostDiffCallback postDiffCallback = new GearPostDiffCallback(mAllGearPosts, AllGearPosts);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

            mAllGearPosts.clear();
            mAllGearPosts.addAll(AllGearPosts);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            mAllGearPosts = AllGearPosts;
        }
        notifyDataSetChanged();

    }

    public void updateConversationsData(List<Conversation> AllConversations) {
        if (mAllConversations != null) {
            ConversationPostDiffCallback postDiffCallback = new ConversationPostDiffCallback(mAllConversations, AllConversations);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

            mAllConversations.clear();
            mAllConversations.addAll(AllConversations);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            mAllConversations = AllConversations;
        }
        notifyDataSetChanged();
    }

    class PostDiffCallback extends DiffUtil.Callback {

        private final List<Post> oldPosts, newPosts;

        public PostDiffCallback(List<Post> oldPosts, List<Post> newPosts) {
            this.oldPosts = oldPosts;
            this.newPosts = newPosts;
        }

        @Override
        public int getOldListSize() {
            return oldPosts.size();
        }

        @Override
        public int getNewListSize() {
            return newPosts.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).key.equals(newPosts.get(newItemPosition).key);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).equals(newPosts.get(newItemPosition));
        }
    }

    private class GearPostDiffCallback extends DiffUtil.Callback {
        private final List<GearPost> oldPosts, newPosts;

        public GearPostDiffCallback(List<GearPost> oldPosts, List<GearPost> newPosts) {
            this.oldPosts = oldPosts;
            this.newPosts = newPosts;
        }

        @Override
        public int getOldListSize() {
            return oldPosts.size();
        }

        @Override
        public int getNewListSize() {
            return newPosts.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).key.equals(newPosts.get(newItemPosition).key);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).equals(newPosts.get(newItemPosition));
        }
    }

    private class ConversationPostDiffCallback extends DiffUtil.Callback {
        private final List<Conversation> oldPosts, newPosts;

        public ConversationPostDiffCallback(List<Conversation> oldPosts, List<Conversation> newPosts) {
            this.oldPosts = oldPosts;
            this.newPosts = newPosts;
        }

        @Override
        public int getOldListSize() {
            return oldPosts.size();
        }

        @Override
        public int getNewListSize() {
            return newPosts.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).key.equals(newPosts.get(newItemPosition).key);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).equals(newPosts.get(newItemPosition));
        }
    }
}