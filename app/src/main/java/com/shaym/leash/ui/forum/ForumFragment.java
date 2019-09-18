package com.shaym.leash.ui.forum;

import android.animation.Animator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.shaym.leash.R;
import com.shaym.leash.logic.forum.ForumViewModel;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.user.UsersViewModel;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;
import com.shaym.leash.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.GENERAL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.SPOTS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.TRIPS_POSTS;

/**
 * Created by shaym on 2/14/18.
 */

public class ForumFragment extends Fragment implements  View.OnClickListener, TabLayout.OnTabSelectedListener {
    private static final String TAG = "ForumFragment";
    private String mCurrentForum = GENERAL_POSTS;
    private RecyclerView mRecyclerView;
    private ForumAdapter mAdapter;
    private TabLayout mForumButtons;
    private List<Profile> mAllUsers = new ArrayList<>();
    private List<Post> mGeneralPosts = new ArrayList<>();
    private List<Post> mTripsPosts = new ArrayList<>();
    private List<Post> mSpotsPosts = new ArrayList<>();
    private FloatingActionButton mFab;
    private int lastPos = -1;
    private Profile mUser;

    public ForumFragment (){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_forum, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
        initUi(Objects.requireNonNull(getView()));

        initUsersViewModel();
        setForumViewModel();
    }

    public void setCurrentPost(String postid){
        Map<String, Integer> map = searchForPost(postid);
        String category = "";
        int pos = -1;
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            category = (String) pair.getKey();
            pos = (int) pair.getValue();
            it.remove(); // avoids a ConcurrentModificationException
        }

        switch (category){
            case GENERAL_POSTS:
                mForumButtons.selectTab(mForumButtons.getTabAt(0));


                break;

            case SPOTS_POSTS:
                mForumButtons.selectTab(mForumButtons.getTabAt(1));

                break;

            case TRIPS_POSTS:
                mForumButtons.selectTab(mForumButtons.getTabAt(2));

                break;

        }

        mRecyclerView.scrollToPosition(pos);
    }


    private  Map<String, Integer> searchForPost(String postid) {
        Map<String, Integer> map = new HashMap<>();
        String category = "";
        int pos = -1;
        List<Post> allPosts = new ArrayList<>();
        allPosts.addAll(mGeneralPosts);
        allPosts.addAll(mTripsPosts);
        allPosts.addAll(mSpotsPosts);

        for (int i=0; i<allPosts.size(); i++){
            if (allPosts.get(i).key.equals(postid)){
                category = allPosts.get(i).category;
                switch (category){
                    case GENERAL_POSTS:
                        for (int j=0; j<mGeneralPosts.size(); j++) {
                            if (mGeneralPosts.get(j).key.equals(postid)) {
                                pos = j;
                            }
                        }
                        break;
                    case TRIPS_POSTS:
                        for (int j=0; j<mTripsPosts.size(); j++) {
                            if (mTripsPosts.get(j).key.equals(postid)) {
                                pos = j;
                            }
                        }
                        break;
                    case SPOTS_POSTS:
                        for (int j=0; j<mSpotsPosts.size(); j++) {
                            if (mSpotsPosts.get(j).key.equals(postid)) {
                                pos = j;
                            }
                        }
                        break;
                }
                break;

            }
        }
        map.put(category, pos);
        return map;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        mRecyclerView.scrollToPosition(0);
    }

    private void initUi(View v) {
        mFab = v.findViewById(R.id.fab_new_post);
        mFab.setOnClickListener(this);
        mForumButtons = v.findViewById(R.id.forum_buttons);
        UIHelper.getInstance().addTab(mForumButtons,getString(R.string.general_menu_title), true);
        UIHelper.getInstance().addTab(mForumButtons,getString(R.string.spots_menu_title), false);
        UIHelper.getInstance().addTab(mForumButtons,getString(R.string.trips_menu_title), false);

        mForumButtons.addOnTabSelectedListener(this);

        mRecyclerView = v.findViewById(R.id.forum_posts_list);

        mRecyclerView.setHasFixedSize(true);
        ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);

        initAdapter();

    }

    private void initUsersViewModel() {
        Log.d(TAG, "initUsersViewModel: ");

        UsersViewModel mUsersViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);

        LiveData<DataSnapshot> allUserLiveData = mUsersViewModel.getAllUsersDataSnapshotLiveData();

        allUserLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<Profile> allusers = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Profile user = ds.getValue(Profile.class);
                    allusers.add(user);
                }
                mAllUsers.clear();
                mAllUsers.addAll(allusers);
                mUser = FireBaseUsersHelper.getInstance().findProfile(FireBaseUsersHelper.getInstance().getUid(), mAllUsers);
                mAdapter.updateUsers(mAllUsers, mUser);
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.fab_new_post:
                try {
                    FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                    NewForumPostDialog newForumPostDialog= NewForumPostDialog.newInstance(mCurrentForum);
                    newForumPostDialog.show(fm, newForumPostDialog.getTag());
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void swapAdapter() {
        mAdapter = new ForumAdapter(this, Objects.requireNonNull(getView()).findViewById(R.id.forum_container));
        mAdapter.mPostType = mCurrentForum;
        mAdapter.updateUsers(mAllUsers, mUser);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initAdapter() {
        Log.d(TAG, "initAdapter: ");
        // Set up Layout Manager, reverse layout
        mAdapter = new ForumAdapter(this, Objects.requireNonNull(getView()).findViewById(R.id.forum_container));
        mRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(mManager);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int pos = mManager.findFirstCompletelyVisibleItemPosition();
                Log.d(TAG, "onScrollStateChanged: " + pos);
                if (pos != -1 && pos != lastPos) {

                    if (lastPos != -1){
                        mAdapter.notifyItemChanged(lastPos, false);
                    }
                    mAdapter.notifyItemChanged(pos, true);
                    lastPos = pos;
                }
            }
        });
    }

    private void setForumViewModel() {
        ForumViewModel mForumViewModel = ViewModelProviders.of(this).get(ForumViewModel.class);

            LiveData<DataSnapshot> mGeneralPostsLiveData = mForumViewModel.getGeneralPostsLiveData();
            LiveData<DataSnapshot> mSpotsPostsLiveData = mForumViewModel.getSpotsPostsLiveData();
            LiveData<DataSnapshot> mTripsPostsLiveData = mForumViewModel.getTripsPostsLiveData();

        mGeneralPostsLiveData.observe(this, dataSnapshot -> {
                if (dataSnapshot != null) {
                    List<Post> generalposts = new ArrayList<>();
                    Log.d(TAG, "setForumViewModel: Observer Triggered");
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Post post = ds.getValue(Post.class);
                        generalposts.add(post);
                    }
                    mGeneralPosts.clear();
                    mGeneralPosts.addAll(generalposts);

                    if (mCurrentForum.equals(GENERAL_POSTS)){
                        mAdapter.updateCurrentData(mGeneralPosts);
                    }

                }
            });

        mSpotsPostsLiveData.observe(this, dataSnapshot -> {
                if (dataSnapshot != null) {
                    List<Post> spotsposts = new ArrayList<>();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Post post = ds.getValue(Post.class);
                        spotsposts.add(post);
                    }
                    mSpotsPosts.clear();
                    mSpotsPosts.addAll(spotsposts);
                }

            if (mCurrentForum.equals(SPOTS_POSTS)){
                mAdapter.updateCurrentData(mSpotsPosts);
            }
            });

            mTripsPostsLiveData.observe(this, dataSnapshot -> {
                if (dataSnapshot != null) {
                    List<Post> tripsposts = new ArrayList<>();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Post post = ds.getValue(Post.class);
                        tripsposts.add(post);
                    }

                    mTripsPosts.clear();
                    mTripsPosts.addAll(tripsposts);

                    if (mCurrentForum.equals(TRIPS_POSTS)){
                        mAdapter.updateCurrentData(mTripsPosts);
                    }
                }
            });

        }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()){
            case 0:
                mCurrentForum = GENERAL_POSTS;

                swapAdapter();
                mAdapter.updateCurrentData(mGeneralPosts);
                break;

            case 1:
                mCurrentForum = TRIPS_POSTS;

                swapAdapter();
                mAdapter.updateCurrentData(mTripsPosts);
                break;

            case 2:
                mCurrentForum = SPOTS_POSTS;

                swapAdapter();
                mAdapter.updateCurrentData(mSpotsPosts);
                break;
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}







