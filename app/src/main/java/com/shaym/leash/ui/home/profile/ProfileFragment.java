package com.shaym.leash.ui.home.profile;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shaym.leash.R;
import com.shaym.leash.logic.chat.Conversation;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.gear.GearPost;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.user.UsersViewModel;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;
import com.shaym.leash.logic.utils.UsersHelperListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.CHAT_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.FORUM_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.PROFILE_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.PROFILE_FORUM_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.PROFILE_GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USED_GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USER_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.USER_POSTS;

/**
 * Created by shaym on 2/17/18.
 */


public class ProfileFragment extends Fragment implements  View.OnClickListener, UsersHelperListener {

    private static final String TAG = "ProfileFragment";
    private TextView mForumTab;
    private TextView mStoreTab;
    private TextView mInboxTab;

    private String mViewType;
    private RecyclerView mRecyclerView;
    private ProfileAdapter mAdapter;
    private DatabaseReference mDatabase;
    private Profile mUser;
    private List<Profile> mAllUsers = new ArrayList<>();
    private List<Post> mAllPosts = new ArrayList<>();
    private List<GearPost> mAllGearPosts = new ArrayList<>();
    private List<Conversation> mAllConversations = new ArrayList<>();

    private UsersViewModel mUsersViewModel;
    private ProfileViewModel mProfileViewModel;

    public ProfileFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mViewType = PROFILE_FORUM_POSTS;
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();

        initUsersViewModel();
        initProfileViewModel();
        setAdapter();
    }


    private void initProfileViewModel() {
        mProfileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        mProfileViewModel.setALL_USER_POSTS_LIVE_DATA(mDatabase.child(FORUM_POSTS).child(USER_POSTS).child(FireBaseUsersHelper.getInstance().getUid()));
        mProfileViewModel.setALL_USER_CONVERSATIONS_LIVE_DATA(mDatabase.child(CHAT_CONVERSATIONS).child(USER_CONVERSATIONS).child(FireBaseUsersHelper.getInstance().getUid()));
        mProfileViewModel.setALL_USER_GEARPOSTS_LIVE_DATA(mDatabase.child(GEAR_POSTS).child(USED_GEAR_POSTS).child(USER_POSTS).child(FireBaseUsersHelper.getInstance().getUid()));
        LiveData<DataSnapshot> mAllPostsLiveData = mProfileViewModel.getAllUserPostsLiveData();
        LiveData<DataSnapshot> mAllGearPostsLiveData = mProfileViewModel.getAllUserGearPostsLiveData();
        LiveData<DataSnapshot> mAllConversationsLiveData = mProfileViewModel.getAllUserConversationsLiveData();

        mAllPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                mAllPosts.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Post post = ds.getValue(Post.class);
                    mAllPosts.add(post);
                }
                mAdapter.updateForumData(mAllPosts);
            }
        });

        mAllGearPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                mAllGearPosts.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost gearPost = ds.getValue(GearPost.class);
                    mAllGearPosts.add(gearPost);
                }
                mAdapter.updateGearData(mAllGearPosts);
            }
        });

        mAllConversationsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                mAllConversations.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Conversation conversation = ds.getValue(Conversation.class);
                    mAllConversations.add(conversation);
                }
                mAdapter.updateConversationsData(mAllConversations);

            }
        });

    }

    private void initUsersViewModel() {
        mUsersViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);

        LiveData<DataSnapshot> currentUserLiveData = mUsersViewModel.getCurrentUserDataSnapshotLiveData();
        LiveData<DataSnapshot> allUserLiveData = mUsersViewModel.getAllUsersDataSnapshotLiveData();

        currentUserLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                Log.d(TAG, "initUsersViewModel: ");
                mUser = dataSnapshot.getValue(Profile.class);
                mAdapter.setUser(mUser);
            }
        });

        allUserLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                mAllUsers.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Profile user = ds.getValue(Profile.class);
                    mAllUsers.add(user);
                }
                mAdapter.setAllUsers(mAllUsers);


            }
        });


    }


    private void initUI() {
        mForumTab = Objects.requireNonNull(getView()).findViewById(R.id.forum_activity);
        mStoreTab = getView().findViewById(R.id.store_activity);
        mInboxTab = getView().findViewById(R.id.inbox);

        mForumTab.setOnClickListener(this);
        mStoreTab.setOnClickListener(this);
        mInboxTab.setOnClickListener(this);

        mRecyclerView = Objects.requireNonNull(getView()).findViewById(R.id.profile_list);
        mRecyclerView.setHasFixedSize(true);

        // Set up Layout Manager, reverse layout
        LinearLayoutManager mManager = new LinearLayoutManager(getContext());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mManager);

        ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);


    }

    private void setAdapter() {
        mAdapter= new ProfileAdapter( mProfileViewModel, this);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.forum_activity:
                setButtonChecked(mForumTab);
                mAdapter.mViewType = PROFILE_FORUM_POSTS;
                mAdapter.notifyDataSetChanged();
                break;

            case R.id.store_activity:
                setButtonChecked(mStoreTab);
                mAdapter.mViewType = PROFILE_GEAR_POSTS;
                mAdapter.notifyDataSetChanged();

                break;

            case R.id.inbox:
                setButtonChecked(mInboxTab);
                mAdapter.mViewType = PROFILE_CONVERSATIONS;
                mAdapter.notifyDataSetChanged();
                break;
        }
    }



    private void setButtonChecked(TextView mCurrentForum) {
        mForumTab.setBackgroundResource(R.color.transparent);
        mStoreTab.setBackgroundResource(R.color.transparent);
        mInboxTab.setBackgroundResource(R.color.transparent);

        mCurrentForum.setBackgroundResource(R.drawable.underline_cameras);
    }



    @Override
    public void onUserByIDLoaded(Profile userbyID) {

    }

    public boolean haschatWith(String chatkey) {
        for (int i=0; i<mAllConversations.size(); i++){
            if (mAllConversations.get(i).key.equals(chatkey)){
                return true;
            }
        }
        return false;
    }
}


