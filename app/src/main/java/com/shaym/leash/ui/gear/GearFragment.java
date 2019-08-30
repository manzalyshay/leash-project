package com.shaym.leash.ui.gear;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.shaym.leash.logic.gear.GearPost;
import com.shaym.leash.logic.gear.GearViewModel;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.user.UsersViewModel;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;
import com.shaym.leash.ui.utils.FabClickedListener;
import com.shaym.leash.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.BOARDS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.CLOTHING_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.FINS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.LEASHES_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.OTHER_POSTS;

/**
 * Created by shaym on 2/14/18.
 */

public class GearFragment extends Fragment implements View.OnClickListener, FabClickedListener, AdapterView.OnItemSelectedListener, TabLayout.OnTabSelectedListener {
    private static final String TAG = "GearFragment";
    private RecyclerView mRecyclerView;
    private GearAdapter mAdapter;
    private Spinner mSecondHandCategories;
    private String mCurrentCategory;
    private NewGearPostDialog mNewGearPostDialog;
    private LinearLayoutManager mManager;
    private List<GearPost> mBoardPosts = new ArrayList<>();
    private List<GearPost> mLeashPosts = new ArrayList<>();
    private List<GearPost> mFinsPosts = new ArrayList<>();
    private List<GearPost> mClothingPosts = new ArrayList<>();
    private List<GearPost> mOtherPosts = new ArrayList<>();
    private List<Profile> mAllUsers = new ArrayList<>();
    private int lastPos = -1;
    private Profile mUser;
    private TabLayout mGearMenu;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_gear, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
        initUI(Objects.requireNonNull(getView()));
        initAdapter();
        setGearViewModel();
        initUsersViewModel();
    }


    private void initUI(View v)  {
        Log.d(TAG, "initUI: ");
        mGearMenu = v.findViewById(R.id.gear_menu);
        UIHelper.getInstance().addTab(mGearMenu, getString(R.string.second_hand));
        UIHelper.getInstance().addTab(mGearMenu, getString(R.string.new_gear));
        mGearMenu.addOnTabSelectedListener(this);

        FloatingActionButton mFab = v.findViewById(R.id.fab_new_post);
        mFab.setOnClickListener(this);

        mSecondHandCategories = v.findViewById(R.id.secondhand_categories);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()),
                R.array.gear_categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSecondHandCategories.setAdapter(adapter);
        mSecondHandCategories.setOnItemSelectedListener(this);

        mRecyclerView = v.findViewById(R.id.posts_list);

        mRecyclerView.setHasFixedSize(true);
        ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);

        mCurrentCategory = BOARDS_POSTS;


    }


    private void initUsersViewModel() {
        UsersViewModel mUsersViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);


        LiveData<DataSnapshot> allUserLiveData = mUsersViewModel.getAllUsersDataSnapshotLiveData();

        allUserLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                Log.d(TAG, "All users Observer Triggered ");

                List<Profile> allusers = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Profile user = ds.getValue(Profile.class);
                    allusers.add(user);
                }
                mAllUsers.clear();
                mAllUsers.addAll(allusers);
                getCurrentUser();
                mAdapter.updateUsers(mAllUsers, mUser);
            }
        });


    }

    private void getCurrentUser(){
        for (int i=0; i<mAllUsers.size(); i++){
            if (mAllUsers.get(i).getUid().equals(FireBaseUsersHelper.getInstance().getUid())){
                mUser = mAllUsers.get(i);
            }
        }
    }

    private void initAdapter() {
        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query

        mAdapter = new GearAdapter( this);
        mRecyclerView.setAdapter(mAdapter);

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

    private void setGearViewModel() {
        Log.d(TAG, "setGearViewModel: ");
        GearViewModel mGearViewModel = ViewModelProviders.of(this).get(GearViewModel.class);

        LiveData<DataSnapshot> mBoardsPostsLiveData = mGearViewModel.getBoardPostsLiveData();
        LiveData<DataSnapshot> mLeashesPostsLiveData = mGearViewModel.getLeashPostsLiveData();
        LiveData<DataSnapshot> mFinsPostsLiveData = mGearViewModel.getFinsPostsLiveData();
        LiveData<DataSnapshot> mClothingPostsLiveData = mGearViewModel.getClothingPostsLiveData();
        LiveData<DataSnapshot> mOtherPostsLiveData = mGearViewModel.getOtherPostsLiveData();

        mBoardsPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<GearPost> boardposts = new ArrayList<>();
                Log.d(TAG, "Board Posts Observer Triggered ");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost post = ds.getValue(GearPost.class);
                    boardposts.add(post);
                }
                mBoardPosts.clear();
                mBoardPosts.addAll(boardposts);

                if (mCurrentCategory.equals(BOARDS_POSTS)){
                    mAdapter.updateCurrentData(mBoardPosts);
                }
            }
        });

        mLeashesPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<GearPost> leashposts = new ArrayList<>();
                Log.d(TAG, "Leash Posts Observer Triggered ");

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost post = ds.getValue(GearPost.class);
                    leashposts.add(post);
                }
                mLeashPosts.clear();
                mLeashPosts.addAll(leashposts);

                if (mCurrentCategory.equals(LEASHES_POSTS)){
                    mAdapter.updateCurrentData(mLeashPosts);
                }            }
        });

        mFinsPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<GearPost> finsposts = new ArrayList<>();
                Log.d(TAG, "Fins Posts Observer Triggered ");

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost post = ds.getValue(GearPost.class);
                    finsposts.add(post);
                }
                mFinsPosts.clear();
                mFinsPosts.addAll(finsposts);

                if (mCurrentCategory.equals(FINS_POSTS)){
                    mAdapter.updateCurrentData(mFinsPosts);
                }            }
        });

        mClothingPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<GearPost> clothingposts = new ArrayList<>();
                Log.d(TAG, "Clothing Posts Observer Triggered ");

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost post = ds.getValue(GearPost.class);
                    clothingposts.add(post);
                }
                mClothingPosts.clear();
                mClothingPosts.addAll(clothingposts);

                if (mCurrentCategory.equals(CLOTHING_POSTS)){
                    mAdapter.updateCurrentData(mClothingPosts);
                }            }
        });

        mOtherPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<GearPost> otherposts = new ArrayList<>();
                Log.d(TAG, "Other Posts Observer Triggered ");


                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost post = ds.getValue(GearPost.class);
                    otherposts.add(post);
                }
                mOtherPosts.clear();
                mOtherPosts.addAll(otherposts);

                if (mCurrentCategory.equals(OTHER_POSTS)){
                    mAdapter.updateCurrentData(mOtherPosts);
                }            }
        });

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);
        mNewGearPostDialog.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){


            case R.id.fab_new_post:
                try {
                    FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                    NewGearPostDialog newGearPostDialog= NewGearPostDialog.newInstance(mCurrentCategory);
                    newGearPostDialog.show(fm, newGearPostDialog.getTag());
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;

        }
    }




    @Override
    public void onFabClicked() {
        Log.d(TAG, "onFabClicked: ");
        try {
            FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
            mNewGearPostDialog = NewGearPostDialog.newInstance(mCurrentCategory);
            mNewGearPostDialog.show(fm, mNewGearPostDialog.getTag());
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected: ");
        switch (position){
            case 0 :
                mCurrentCategory = BOARDS_POSTS;
                swapAdapter();
                mAdapter.updateCurrentData(mBoardPosts);

                break;

            case 1:
                mCurrentCategory = LEASHES_POSTS;
                swapAdapter();
                mAdapter.updateCurrentData(mLeashPosts);
                break;

            case 2:
                mCurrentCategory = FINS_POSTS;
                swapAdapter();
                mAdapter.updateCurrentData(mFinsPosts);
                break;

            case 3:
                mCurrentCategory = CLOTHING_POSTS;
                swapAdapter();
                mAdapter.updateCurrentData(mClothingPosts);
                break;

            case 4:
                mCurrentCategory = OTHER_POSTS;
                swapAdapter();
                mAdapter.updateCurrentData(mOtherPosts);
                break;

        }
    }

    private void swapAdapter() {
        mAdapter = new GearAdapter(this);
        mAdapter.mCurrentCategory = mCurrentCategory;
        mAdapter.updateUsers(mAllUsers, mUser);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
            case BOARDS_POSTS:
                mSecondHandCategories.setSelection(0);
                break;
            case LEASHES_POSTS:
                mSecondHandCategories.setSelection(1);

                break;
            case FINS_POSTS:
                mSecondHandCategories.setSelection(2);

            case CLOTHING_POSTS:
                mSecondHandCategories.setSelection(3);


            case OTHER_POSTS:
                mSecondHandCategories.setSelection(4);


        }

        mRecyclerView.scrollToPosition(pos);
    }


    private  Map<String, Integer> searchForPost(String postid) {
        Map<String, Integer> map = new HashMap<>();
        String category = "";
        int pos = -1;
        List<GearPost> allPosts = new ArrayList<>();
        allPosts.addAll(mBoardPosts);
        allPosts.addAll(mClothingPosts);
        allPosts.addAll(mFinsPosts);
        allPosts.addAll(mLeashPosts);
        allPosts.addAll(mOtherPosts);

        for (int i=0; i<allPosts.size(); i++){
            if (allPosts.get(i).key.equals(postid)){
                category = allPosts.get(i).category;
                switch (category){
                    case BOARDS_POSTS:
                        for (int j=0; j<mBoardPosts.size(); j++) {
                            if (mBoardPosts.get(j).key.equals(postid)) {
                                pos = j;
                            }
                        }
                        break;
                    case LEASHES_POSTS:
                        for (int j=0; j<mLeashPosts.size(); j++) {
                            if (mLeashPosts.get(j).key.equals(postid)) {
                                pos = j;
                            }
                        }
                        break;
                    case FINS_POSTS:
                        for (int j=0; j<mFinsPosts.size(); j++) {
                            if (mFinsPosts.get(j).key.equals(postid)) {
                                pos = j;
                            }
                        }

                    case CLOTHING_POSTS:
                        for (int j=0; j<mClothingPosts.size(); j++) {
                            if (mClothingPosts.get(j).key.equals(postid)) {
                                pos = j;
                            }
                        }

                    case OTHER_POSTS:
                        for (int j=0; j<mOtherPosts.size(); j++) {
                            if (mOtherPosts.get(j).key.equals(postid)) {
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
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()){
            case 1:
                Toast.makeText(getContext(), getString(R.string.stores_notavail), Toast.LENGTH_SHORT).show();
                mGearMenu.selectTab(mGearMenu.getTabAt(0));
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






