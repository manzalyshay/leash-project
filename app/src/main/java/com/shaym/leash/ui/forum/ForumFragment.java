package com.shaym.leash.ui.forum;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shaym.leash.R;
import com.shaym.leash.logic.chat.Conversation;
import com.shaym.leash.logic.forum.ForumViewModel;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.gear.GearPost;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.user.UsersViewModel;
import com.shaym.leash.logic.utils.FireBasePostsHelper;
import com.shaym.leash.logic.utils.PostDiffCallback;
import com.shaym.leash.logic.utils.UserDiffCallback;
import com.shaym.leash.ui.home.profile.ProfileViewModel;
import com.shaym.leash.ui.utils.FabClickedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.ALL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.BOARDS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.CHAT_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.FORUM_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GENERAL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.SPOTS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.TRIPS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USED_GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USER_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.USER_POSTS;

/**
 * Created by shaym on 2/14/18.
 */

public class ForumFragment extends Fragment implements  View.OnClickListener, FabClickedListener {
    private static final String TAG = "ForumFragment";
    private String mCurrentForum = GENERAL_POSTS;
    private RecyclerView mRecyclerView;
    private ForumAdapter mAdapter;
    private Button mGeneralButton;
    private Button mTripsButton;
    private Button mSpotsButton;
    private List<Profile> mAllUsers = new ArrayList<>();
    private List<Post> mGeneralPosts = new ArrayList<>();
    private List<Post> mTripsPosts = new ArrayList<>();
    private List<Post> mSpotsPosts = new ArrayList<>();
    private int lastPos = -1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View v = inflater.inflate(R.layout.fragment_forum, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
        initUi(Objects.requireNonNull(getView()));

        initUsersViewModel();
        setForumViewModel();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        mRecyclerView.scrollToPosition(0);
    }

    private void initUi(View v) {
        mGeneralButton = v.findViewById(R.id.general_forum_button);
        mSpotsButton = v.findViewById(R.id.spots_forum_button);
        mTripsButton = v.findViewById(R.id.trips_forum_button);

        mGeneralButton.setOnClickListener(this);
        setButtonChecked(mGeneralButton);
        mSpotsButton.setOnClickListener(this);
        mTripsButton.setOnClickListener(this);

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
                mAdapter.updateUsers(mAllUsers);
            }
        });


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.general_forum_button:
                setButtonChecked(mGeneralButton);
                mCurrentForum = GENERAL_POSTS;

                swapAdapter();
                mAdapter.updateCurrentData(mGeneralPosts);
                break;

            case R.id.spots_forum_button:
                setButtonChecked(mSpotsButton);
                mCurrentForum = SPOTS_POSTS;

                swapAdapter();
                mAdapter.updateCurrentData(mSpotsPosts);

                break;

            case R.id.trips_forum_button:
                setButtonChecked(mTripsButton);
                mCurrentForum = TRIPS_POSTS;

                swapAdapter();
                mAdapter.updateCurrentData(mTripsPosts);

                break;
        }
    }

    private void swapAdapter() {
        mAdapter = new ForumAdapter(this);
        mAdapter.mPostType = mCurrentForum;
        mAdapter.updateUsers(mAllUsers);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initAdapter() {
        Log.d(TAG, "initAdapter: ");
        // Set up Layout Manager, reverse layout
        mAdapter = new ForumAdapter(this);
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



    private void setButtonChecked(Button b) {
        mGeneralButton.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.rounded_button_unchecked));
        mGeneralButton.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.bottom_nav_bg_color));

        mTripsButton.setBackground(getActivity().getDrawable(R.drawable.rounded_button_unchecked));
        mTripsButton.setTextColor(ContextCompat.getColor(getContext(), R.color.bottom_nav_bg_color));

        mSpotsButton.setBackground(getActivity().getDrawable(R.drawable.rounded_button_unchecked));
        mSpotsButton.setTextColor(ContextCompat.getColor(getContext(), R.color.bottom_nav_bg_color));

        b.setBackground(getActivity().getDrawable(R.drawable.rounded_button_checked));
        b.setTextColor(Color.WHITE);

    }

    @Override
    public void onFabClicked() {
        Log.d(TAG, "onFabClicked: ");
        try {
            FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
            NewForumPostDialog newForumPostDialog= NewForumPostDialog.newInstance(mCurrentForum);
            newForumPostDialog.show(fm, newForumPostDialog.getTag());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }









}







