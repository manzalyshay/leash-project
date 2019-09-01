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
import com.shaym.leash.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.BOARDS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.CLOTHING_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.FCS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.FINS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.FREEGULL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GALIM_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.INTERSURF_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.LEASHES_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.NEW_GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.OTHER_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USED_GEAR_POSTS;

/**
 * Created by shaym on 2/14/18.
 */

public class GearFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, TabLayout.OnTabSelectedListener {
    private static final String TAG = "GearFragment";
    private RecyclerView mRecyclerView;
    private GearAdapter mAdapter;
    private GearSpinner mGearCategories;
    private String mCurrentCategory = BOARDS_POSTS;
    private String mCurrentGear = USED_GEAR_POSTS;

    private NewGearPostDialog mNewGearPostDialog;
    private LinearLayoutManager mManager;
    private List<GearPost> mUsedBoardPosts = new ArrayList<>();
    private List<GearPost> mUsedLeashPosts = new ArrayList<>();
    private List<GearPost> mUsedFinsPosts = new ArrayList<>();
    private List<GearPost> mUsedClothingPosts = new ArrayList<>();
    private List<GearPost> mUsedOtherPosts = new ArrayList<>();

    private List<GearPost> mNewBoardPosts = new ArrayList<>();
    private List<GearPost> mNewLeashPosts = new ArrayList<>();
    private List<GearPost> mNewFinsPosts = new ArrayList<>();
    private List<GearPost> mNewClothingPosts = new ArrayList<>();
    private List<GearPost> mNewOtherPosts = new ArrayList<>();
    private List<Profile> mAllUsers = new ArrayList<>();
    private int lastPos = -1;
    private Profile mUser;
    private TabLayout mGearMenu;
    private TabLayout mStoresMenu;


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
        UIHelper.getInstance().addTab(mGearMenu, getString(R.string.second_hand), true);
        UIHelper.getInstance().addTab(mGearMenu, getString(R.string.new_gear), false);
        mGearMenu.addOnTabSelectedListener(this);

        mStoresMenu = v.findViewById(R.id.stores_menu);
        UIHelper.getInstance().addTab(mStoresMenu, getString(R.string.intersurf_menutitle), true);
        UIHelper.getInstance().addTab(mStoresMenu, "FCS", false);
        UIHelper.getInstance().addTab(mStoresMenu, "FreeGull", false);
        UIHelper.getInstance().addTab(mStoresMenu, "Galim", false);

        mStoresMenu.addOnTabSelectedListener(this);

        FloatingActionButton mFab = v.findViewById(R.id.fab_new_post);
        mFab.setOnClickListener(this);

        mGearCategories = v.findViewById(R.id.gear_categories);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()),
                R.array.gear_categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGearCategories.setAdapter(adapter);
        mGearCategories.setOnItemSelectedListener(this);

        mRecyclerView = v.findViewById(R.id.posts_list);

        mRecyclerView.setHasFixedSize(true);
        ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);

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
                mUser = FireBaseUsersHelper.getInstance().findProfile(FireBaseUsersHelper.getInstance().getUid(), mAllUsers);
                mAdapter.updateUsers(mAllUsers, mUser);
            }
        });


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

        LiveData<DataSnapshot> mUsedBoardsPostsLiveData = mGearViewModel.getUsedBoardPostsLiveData();
        LiveData<DataSnapshot> mUsedLeashesPostsLiveData = mGearViewModel.getUsedLeashPostsLiveData();
        LiveData<DataSnapshot> mUsedFinsPostsLiveData = mGearViewModel.getUsedFinsPostsLiveData();
        LiveData<DataSnapshot> mUsedClothingPostsLiveData = mGearViewModel.getUsedClothingPostsLiveData();
        LiveData<DataSnapshot> mUsedOtherPostsLiveData = mGearViewModel.getUsedOtherPostsLiveData();

        LiveData<DataSnapshot> mNewBoardsPostsLiveData = mGearViewModel.getNewBoardPostsLiveData();
        LiveData<DataSnapshot> mNewLeashesPostsLiveData = mGearViewModel.getNewleashPostsLiveData();
        LiveData<DataSnapshot> mNewFinsPostsLiveData = mGearViewModel.getNewfinsPostsLiveData();
        LiveData<DataSnapshot> mNewClothingPostsLiveData = mGearViewModel.getNewclothingPostsLiveData();
        LiveData<DataSnapshot> mNewOtherPostsLiveData = mGearViewModel.getNewotherPostsLiveData();

        mUsedBoardsPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<GearPost> boardposts = new ArrayList<>();
                Log.d(TAG, "Board Posts Observer Triggered ");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost post = ds.getValue(GearPost.class);
                    boardposts.add(post);
                }
                mUsedBoardPosts.clear();
                mUsedBoardPosts.addAll(boardposts);

                if (mCurrentCategory.equals(BOARDS_POSTS) && mCurrentGear.equals(USED_GEAR_POSTS)){
                    mAdapter.updateCurrentData(mUsedBoardPosts);
                }
            }
        });

        mUsedLeashesPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<GearPost> leashposts = new ArrayList<>();
                Log.d(TAG, "Leash Posts Observer Triggered ");

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost post = ds.getValue(GearPost.class);
                    leashposts.add(post);
                }
                mUsedLeashPosts.clear();
                mUsedLeashPosts.addAll(leashposts);

                if (mCurrentCategory.equals(LEASHES_POSTS) && mCurrentGear.equals(USED_GEAR_POSTS)){
                    mAdapter.updateCurrentData(mUsedLeashPosts);
                }            }
        });

        mUsedFinsPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<GearPost> finsposts = new ArrayList<>();
                Log.d(TAG, "Fins Posts Observer Triggered ");

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost post = ds.getValue(GearPost.class);
                    finsposts.add(post);
                }
                mUsedFinsPosts.clear();
                mUsedFinsPosts.addAll(finsposts);

                if (mCurrentCategory.equals(FINS_POSTS) && mCurrentGear.equals(USED_GEAR_POSTS)){
                    mAdapter.updateCurrentData(mUsedFinsPosts);
                }
            }
        });

        mUsedClothingPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<GearPost> clothingposts = new ArrayList<>();
                Log.d(TAG, "Clothing Posts Observer Triggered ");

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost post = ds.getValue(GearPost.class);
                    clothingposts.add(post);
                }
                mUsedClothingPosts.clear();
                mUsedClothingPosts.addAll(clothingposts);

                if (mCurrentCategory.equals(CLOTHING_POSTS) && mCurrentGear.equals(USED_GEAR_POSTS)){
                    mAdapter.updateCurrentData(mUsedClothingPosts);
                }            }
        });

        mUsedOtherPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<GearPost> otherposts = new ArrayList<>();
                Log.d(TAG, "Other Posts Observer Triggered ");


                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost post = ds.getValue(GearPost.class);
                    otherposts.add(post);
                }
                mUsedOtherPosts.clear();
                mUsedOtherPosts.addAll(otherposts);

                if (mCurrentCategory.equals(OTHER_POSTS) && mCurrentGear.equals(USED_GEAR_POSTS)){
                    mAdapter.updateCurrentData(mUsedOtherPosts);
                }            }
        });

        mNewBoardsPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<GearPost> newboardposts = new ArrayList<>();
                Log.d(TAG, "New Board Posts Observer Triggered ");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost post = ds.getValue(GearPost.class);
                    newboardposts.add(post);
                }
                mNewBoardPosts.clear();
                mNewBoardPosts.addAll(newboardposts);

                if (mCurrentCategory.equals(BOARDS_POSTS) && mCurrentGear.equals(NEW_GEAR_POSTS)){
                    mAdapter.updateCurrentData(mNewBoardPosts);
                }
            }
        });

        mNewLeashesPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<GearPost> newleashposts = new ArrayList<>();
                Log.d(TAG, "New Leash Posts Observer Triggered ");

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost post = ds.getValue(GearPost.class);
                    newleashposts.add(post);
                }
                mNewLeashPosts.clear();
                mNewLeashPosts.addAll(newleashposts);

                if (mCurrentCategory.equals(LEASHES_POSTS) && mCurrentGear.equals(NEW_GEAR_POSTS)){
                    mAdapter.updateCurrentData(mNewLeashPosts);
                }            }
        });

        mNewFinsPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<GearPost> newfinsposts = new ArrayList<>();
                Log.d(TAG, "New Fins Posts Observer Triggered ");

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost post = ds.getValue(GearPost.class);
                    newfinsposts.add(post);
                }
                mNewFinsPosts.clear();
                mNewFinsPosts.addAll(newfinsposts);

                if (mCurrentCategory.equals(FINS_POSTS) && mCurrentGear.equals(NEW_GEAR_POSTS)){
                    mAdapter.updateCurrentData(mNewFinsPosts);
                }
            }
        });

        mNewClothingPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<GearPost> newclothingposts = new ArrayList<>();
                Log.d(TAG, "New Clothing Posts Observer Triggered ");

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost post = ds.getValue(GearPost.class);
                    newclothingposts.add(post);
                }
                mNewClothingPosts.clear();
                mNewClothingPosts.addAll(newclothingposts);

                if (mCurrentCategory.equals(CLOTHING_POSTS) && mCurrentGear.equals(NEW_GEAR_POSTS)){
                    mAdapter.updateCurrentData(mNewClothingPosts);
                }            }
        });

        mNewOtherPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<GearPost> newotherposts = new ArrayList<>();
                Log.d(TAG, "New Other Posts Observer Triggered ");


                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost post = ds.getValue(GearPost.class);
                    newotherposts.add(post);
                }
                mNewOtherPosts.clear();
                mNewOtherPosts.addAll(newotherposts);

                if (mCurrentCategory.equals(OTHER_POSTS) && mCurrentGear.equals(NEW_GEAR_POSTS)){
                    mAdapter.updateCurrentData(mNewOtherPosts);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected: ");
        switch (position){
            case 0 :
                mCurrentCategory = BOARDS_POSTS;
                swapAdapter();

                if (mCurrentGear.equals(USED_GEAR_POSTS)) {
                    mAdapter.updateCurrentData(mUsedBoardPosts);
                }
                else {
                    mAdapter.updateCurrentData(mNewBoardPosts);
                }

                break;

            case 1:
                mCurrentCategory = LEASHES_POSTS;
                swapAdapter();

                if (mCurrentGear.equals(USED_GEAR_POSTS)) {
                    mAdapter.updateCurrentData(mUsedLeashPosts);
                }
                else {
                    mAdapter.updateCurrentData(mNewLeashPosts);

                }

                break;

            case 2:
                mCurrentCategory = FINS_POSTS;
                swapAdapter();

                if (mCurrentGear.equals(USED_GEAR_POSTS)) {
                    mAdapter.updateCurrentData(mUsedFinsPosts);
                }
                else {
                    mAdapter.updateCurrentData(mNewFinsPosts);

                }
                break;

            case 3:
                mCurrentCategory = CLOTHING_POSTS;
                swapAdapter();
                mAdapter.updateCurrentData(mUsedClothingPosts);
                break;

            case 4:
                mCurrentCategory = OTHER_POSTS;
                swapAdapter();
                if (mCurrentGear.equals(USED_GEAR_POSTS)) {
                    mAdapter.updateCurrentData(mUsedOtherPosts);
                }
                else {
                    mAdapter.updateCurrentData(mNewOtherPosts);

                }                break;

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
                mGearCategories.setSelection(0);
                break;
            case LEASHES_POSTS:
                mGearCategories.setSelection(1);

                break;
            case FINS_POSTS:
                mGearCategories.setSelection(2);

            case CLOTHING_POSTS:
                mGearCategories.setSelection(3);


            case OTHER_POSTS:
                mGearCategories.setSelection(4);


        }

        mRecyclerView.scrollToPosition(pos);
    }


    private  Map<String, Integer> searchForPost(String postid) {
        Map<String, Integer> map = new HashMap<>();
        String category = "";
        int pos = -1;
        List<GearPost> allPosts = new ArrayList<>();
        allPosts.addAll(mUsedBoardPosts);
        allPosts.addAll(mUsedClothingPosts);
        allPosts.addAll(mUsedFinsPosts);
        allPosts.addAll(mUsedLeashPosts);
        allPosts.addAll(mUsedOtherPosts);

        for (int i=0; i<allPosts.size(); i++){
            if (allPosts.get(i).key.equals(postid)){
                category = allPosts.get(i).category;
                switch (category){
                    case BOARDS_POSTS:
                        for (int j = 0; j< mUsedBoardPosts.size(); j++) {
                            if (mUsedBoardPosts.get(j).key.equals(postid)) {
                                pos = j;
                            }
                        }
                        break;
                    case LEASHES_POSTS:
                        for (int j = 0; j< mUsedLeashPosts.size(); j++) {
                            if (mUsedLeashPosts.get(j).key.equals(postid)) {
                                pos = j;
                            }
                        }
                        break;
                    case FINS_POSTS:
                        for (int j = 0; j< mUsedFinsPosts.size(); j++) {
                            if (mUsedFinsPosts.get(j).key.equals(postid)) {
                                pos = j;
                            }
                        }

                    case CLOTHING_POSTS:
                        for (int j = 0; j< mUsedClothingPosts.size(); j++) {
                            if (mUsedClothingPosts.get(j).key.equals(postid)) {
                                pos = j;
                            }
                        }

                    case OTHER_POSTS:
                        for (int j = 0; j< mUsedOtherPosts.size(); j++) {
                            if (mUsedOtherPosts.get(j).key.equals(postid)) {
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
        if (tab.parent.getId() == R.id.gear_menu) {
            switch (tab.getPosition()) {
                case 0:
                    mStoresMenu.setVisibility(View.GONE);
                    mCurrentGear = USED_GEAR_POSTS;
                    mGearCategories.setSelection(mGearCategories.getSelectedItemPosition());

                    break;
                case 1:
                    mStoresMenu.setVisibility(View.VISIBLE);
                    mCurrentGear = NEW_GEAR_POSTS;
                    mGearCategories.setSelection(mGearCategories.getSelectedItemPosition());
                    mStoresMenu.selectTab(mStoresMenu.getTabAt(0));
                    break;
            }

        }
        else {
            switch (tab.getPosition()) {
                case 0:
                    mAdapter.filter(INTERSURF_POSTS);
                    break;
                case 1:
                    mAdapter.filter(FCS_POSTS);

                    break;

                case 2:
                    mAdapter.filter(FREEGULL_POSTS);

                    break;

                case 3:
                    mAdapter.filter(GALIM_POSTS);

                    break;
            }


        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}






