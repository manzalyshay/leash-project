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

import com.google.android.material.tabs.TabLayout;
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
import com.shaym.leash.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.CHAT_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.FORUM_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GENERAL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.PROFILE_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.PROFILE_FORUM_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.PROFILE_GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.SPOTS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.TRIPS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USED_GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USER_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.USER_POSTS;

/**
 * Created by shaym on 2/17/18.
 */


public class ProfileFragment extends Fragment implements  TabLayout.OnTabSelectedListener {

    private static final String TAG = "ProfileFragment";
    private TabLayout mProfileMenu;
    private RecyclerView mRecyclerView;
    private ProfileAdapter mAdapter;
    private DatabaseReference mDatabase;
    private Profile mUser;
    private List<Profile> mAllUsers = new ArrayList<>();
    private List<Post> mAllPosts = new ArrayList<>();
    private List<GearPost> mAllGearPosts = new ArrayList<>();
    private List<Conversation> mAllConversations = new ArrayList<>();
    private ProfileViewModel mProfileViewModel;

    public ProfileFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
        UsersViewModel mUsersViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);

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
        mProfileMenu = Objects.requireNonNull(getView()).findViewById(R.id.profile_menu);
        UIHelper.getInstance().addTab(mProfileMenu, getString(R.string.forum_activity), true);
        UIHelper.getInstance().addTab(mProfileMenu, getString(R.string.shop_activity), false);
        UIHelper.getInstance().addTab(mProfileMenu, getString(R.string.inbox_menu_item), false);
        mProfileMenu.addOnTabSelectedListener(this);

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

    public boolean haschatWith(String chatkey) {
        for (int i=0; i<mAllConversations.size(); i++){
            if (mAllConversations.get(i).key.equals(chatkey)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()){
            case 0:
                mAdapter.mViewType = PROFILE_FORUM_POSTS;
                break;

            case 1:
                mAdapter.mViewType = PROFILE_GEAR_POSTS;

                break;

            case 2:
                mAdapter.mViewType = PROFILE_CONVERSATIONS;

                break;
        }

        mAdapter.notifyDataSetChanged();


    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}


