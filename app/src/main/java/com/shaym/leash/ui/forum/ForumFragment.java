package com.shaym.leash.ui.forum;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shaym.leash.R;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.ui.gear.NewGearPostDialog;
import com.shaym.leash.ui.utils.FabClickedListener;

import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.FORUM_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GENERAL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.SPOTS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.TRIPS_POSTS;

/**
 * Created by shaym on 2/14/18.
 */

public class ForumFragment extends Fragment implements onPostSelectedListener, View.OnClickListener, FabClickedListener {
    private static final String TAG = "ForumFragment";
    // 0 - General, 1 - Spots, 2 - Trips, 3- All
    private int mCurrentForum;
    private RecyclerView mRecyclerView;
    private ForumAdapter mAdapter;
    private DatabaseReference mDatabase;
    private Button mGeneralButton;
    private Button mTripsButton;
    private Button mSpotsButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forum, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mCurrentForum = 0;
        return v;
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onStart() {
        super.onStart();
        initUi();

    }

    private void initUi() {
        mGeneralButton = Objects.requireNonNull(getView()).findViewById(R.id.general_forum_button);
        mSpotsButton = getView().findViewById(R.id.spots_forum_button);
        mTripsButton = getView().findViewById(R.id.trips_forum_button);

        mGeneralButton.setOnClickListener(this);
        setButtonChecked(mGeneralButton);
        mSpotsButton.setOnClickListener(this);
        mTripsButton.setOnClickListener(this);


        mRecyclerView = Objects.requireNonNull(getView()).findViewById(R.id.posts_list);

        mRecyclerView.setHasFixedSize(true);
        // Set up Layout Manager, reverse layout
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery();
        Log.d(TAG, "initUi: " + postsQuery.toString());

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(postsQuery, Post.class)
                .build();

        mAdapter = new ForumAdapter(options, this );
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAdapter != null){
            mAdapter.startListening();
        }
    }

    private Query getQuery() {
        Query query = null;

        switch (mCurrentForum){
            case 0:
                query = mDatabase.child(FORUM_POSTS).child(GENERAL_POSTS);
                break;

            case 1:
                query = mDatabase.child(FORUM_POSTS).child(SPOTS_POSTS);
                break;
            case 2:
                query = mDatabase.child(FORUM_POSTS).child(TRIPS_POSTS);
                break;
        }

        return query;

    }






    @Override
    public void onStop() {
        super.onStop();

    }


    @Override
    public void onPostSelected(Post post) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.general_forum_button:
                setButtonChecked(mGeneralButton);
                mCurrentForum = 0;
                swapAdapter();
                break;

            case R.id.spots_forum_button:
                setButtonChecked(mSpotsButton);
                mCurrentForum = 1;
                swapAdapter();

                break;

            case R.id.trips_forum_button:
                setButtonChecked(mTripsButton);
                mCurrentForum = 2;
                swapAdapter();

                break;
        }
    }

    private void swapAdapter() {
        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery();
        Log.d(TAG, "initUi: " + postsQuery.toString());

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(postsQuery, Post.class)
                .build();

        ForumAdapter newAdapter = new ForumAdapter(options, this );

        newAdapter.startListening();

        mRecyclerView.swapAdapter(newAdapter, true);

        mAdapter = newAdapter;
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
            NewForumPostDialog newForumPostDialog= NewForumPostDialog.newInstance();
            newForumPostDialog.show(fm, newForumPostDialog.getTag());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}







