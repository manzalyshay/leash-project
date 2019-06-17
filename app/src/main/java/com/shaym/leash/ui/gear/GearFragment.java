package com.shaym.leash.ui.gear;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shaym.leash.R;
import com.shaym.leash.logic.forum.ForumViewModel;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.gear.GearPost;
import com.shaym.leash.logic.gear.GearViewModel;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.user.UsersViewModel;
import com.shaym.leash.ui.forum.ForumAdapter;
import com.shaym.leash.ui.utils.FabClickedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.BOARDS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.CLOTHING_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.FINS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.FORUM_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GENERAL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.LEASHES_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.OTHER_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.SPOTS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.TRIPS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USED_GEAR_POSTS;

/**
 * Created by shaym on 2/14/18.
 */

public class GearFragment extends Fragment implements View.OnClickListener, FabClickedListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "GearFragment";
    private FloatingActionButton mGearFab;
    private DatabaseReference mDatabase;
    private RecyclerView mRecyclerView;
    private GearAdapter mAdapter;
    private Button mSecondHandButton;
    private Button mStoresButton;
    private Spinner mSecondHandCategories;
    private String mCurrentCategory;
    private NewGearPostDialog mNewGearPostDialog;
    private GearViewModel mGearViewModel;
    private LinearLayoutManager mManager;
    private UsersViewModel mUsersViewModel;
    private Profile mUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View v = inflater.inflate(R.layout.fragment_gear, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        initUI(v);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
            initAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();

        setGearViewModel();
        initUsersViewModel();
    }

    private void initUI(View v)  {
        Log.d(TAG, "initUI: ");
        mSecondHandButton = v.findViewById(R.id.secondhand_button);
        mStoresButton = v.findViewById(R.id.stores_button);

        mSecondHandButton.setOnClickListener(this);
        setButtonChecked(mSecondHandButton);
        mSecondHandButton.setOnClickListener(this);
        mStoresButton.setOnClickListener(this);

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
        mUsersViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);

        LiveData<DataSnapshot> currentUserLiveData = mUsersViewModel.getCurrentUserDataSnapshotLiveData();

        currentUserLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                Log.d(TAG, "initUsersViewModel: ");
                mUser = dataSnapshot.getValue(Profile.class);
                mAdapter.setViewerProfile(mUser);
            }

        });

        LiveData<DataSnapshot> allUserLiveData = mUsersViewModel.getAllUsersDataSnapshotLiveData();

        allUserLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<Profile> allusers = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Profile user = ds.getValue(Profile.class);
                    allusers.add(user);
                }
                mAdapter.updateUsers(allusers);
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

        mAdapter = new GearAdapter(  this );
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setGearViewModel() {
        Log.d(TAG, "setGearViewModel: ");
        mGearViewModel = ViewModelProviders.of(this).get(GearViewModel.class);

        LiveData<DataSnapshot> mBoardsPostsLiveData = mGearViewModel.getBoardPostsLiveData();
        LiveData<DataSnapshot> mLeashesPostsLiveData = mGearViewModel.getLeashPostsLiveData();
        LiveData<DataSnapshot> mFinsPostsLiveData = mGearViewModel.getFinsPostsLiveData();
        LiveData<DataSnapshot> mClothingPostsLiveData = mGearViewModel.getClothingPostsLiveData();
        LiveData<DataSnapshot> mOtherPostsLiveData = mGearViewModel.getOtherPostsLiveData();

        mBoardsPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<GearPost> boardposts = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost post = ds.getValue(GearPost.class);
                    boardposts.add(post);
                }
                mAdapter.updateBoardPostsData(boardposts);
            }
        });

        mLeashesPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<GearPost> leashposts = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost post = ds.getValue(GearPost.class);
                    leashposts.add(post);
                }
                mAdapter.updateleashPostsData(leashposts);
            }
        });

        mFinsPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<GearPost> finsposts = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost post = ds.getValue(GearPost.class);
                    finsposts.add(post);
                }
                mAdapter.updatefinsPostsData(finsposts);
            }
        });

        mClothingPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<GearPost> clothingposts = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost post = ds.getValue(GearPost.class);
                    clothingposts.add(post);
                }
                mAdapter.updateclothingPostsData(clothingposts);
            }
        });

        mOtherPostsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<GearPost> otherposts = new ArrayList<>();


                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GearPost post = ds.getValue(GearPost.class);
                    otherposts.add(post);
                }
                mAdapter.updateotherPostsData(otherposts);
            }
        });

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);
        mNewGearPostDialog.onActivityResult(requestCode, resultCode, data);
    }


    private void setButtonChecked(Button b) {
        Log.d(TAG, "setButtonChecked: ");
        mSecondHandButton.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.rounded_button_unchecked));
        mSecondHandButton.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.bottom_nav_bg_color));

        mStoresButton.setBackground(getActivity().getDrawable(R.drawable.rounded_button_unchecked));
        mStoresButton.setTextColor(ContextCompat.getColor(getContext(), R.color.bottom_nav_bg_color));

        b.setBackground(getActivity().getDrawable(R.drawable.rounded_button_checked));
        b.setTextColor(Color.WHITE);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.secondhand_button:
                setButtonChecked(mSecondHandButton);
                break;

            case R.id.stores_button:
                Toast.makeText(getContext(), "Stores Section is not available at the moment.", Toast.LENGTH_SHORT).show();
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
                mAdapter.mCurrentCategory = mCurrentCategory;
                mAdapter.notifyDataSetChanged();
                break;

            case 1:
                mCurrentCategory = LEASHES_POSTS;
                mAdapter.mCurrentCategory = mCurrentCategory;
                mAdapter.notifyDataSetChanged();
                break;

            case 2:
                mCurrentCategory = FINS_POSTS;
                mAdapter.mCurrentCategory = mCurrentCategory;
                mAdapter.notifyDataSetChanged();
                break;

            case 3:
                mCurrentCategory = CLOTHING_POSTS;
                mAdapter.mCurrentCategory = mCurrentCategory;
                mAdapter.notifyDataSetChanged();
                break;

            case 4:
                mCurrentCategory = OTHER_POSTS;
                mAdapter.mCurrentCategory = mCurrentCategory;
                mAdapter.notifyDataSetChanged();
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}






