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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shaym.leash.R;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.gear.GearPost;
import com.shaym.leash.ui.forum.ForumAdapter;
import com.shaym.leash.ui.utils.FabClickedListener;

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

public class GearFragment extends Fragment implements View.OnClickListener, onGearPostSelectedListener, FabClickedListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "GearFragment";
    private FloatingActionButton mGearFab;
    private int mCurrentGear;
    private DatabaseReference mDatabase;
    private RecyclerView mRecyclerView;
    private GearAdapter mAdapter;
    private Button mSecondHandButton;
    private Button mStoresButton;
    private Spinner mSecondHandCategories;
    private String mCurrentCategory;
    private NewGearPostDialog mNewGearPostDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gear, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mCurrentGear = 0;
        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter!= null) {
            mAdapter.startListening();
        }
    }

    private void initUI()  {
        mSecondHandButton = Objects.requireNonNull(getView()).findViewById(R.id.secondhand_button);
        mStoresButton = getView().findViewById(R.id.stores_button);

        mSecondHandButton.setOnClickListener(this);
        setButtonChecked(mSecondHandButton);
        mSecondHandButton.setOnClickListener(this);
        mStoresButton.setOnClickListener(this);

        mSecondHandCategories = getView().findViewById(R.id.secondhand_categories);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()),
                R.array.gear_categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSecondHandCategories.setAdapter(adapter);
        mSecondHandCategories.setOnItemSelectedListener(this);

        mRecyclerView = Objects.requireNonNull(getView()).findViewById(R.id.posts_list);
        mRecyclerView.setNestedScrollingEnabled(false);

        mRecyclerView.setHasFixedSize(true);
        // Set up Layout Manager, reverse layout
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        mCurrentCategory = BOARDS_POSTS;
        Query postsQuery = getQuery();
        assert postsQuery != null;
        Log.d(TAG, "initUi: " + postsQuery.toString());

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<GearPost>()
                .setQuery(postsQuery, GearPost.class)
                .build();

        mAdapter = new GearAdapter(options, this );
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter!= null) {
            mAdapter.stopListening();
        }
    }

    private Query getQuery() {
        Query query = null;
        switch (mCurrentCategory){
            case BOARDS_POSTS:
                query = mDatabase.child(GEAR_POSTS).child(USED_GEAR_POSTS).child(BOARDS_POSTS);
                break;

            case LEASHES_POSTS:
                query = mDatabase.child(GEAR_POSTS).child(USED_GEAR_POSTS).child(LEASHES_POSTS);
                break;
            case FINS_POSTS:
                query = mDatabase.child(GEAR_POSTS).child(USED_GEAR_POSTS).child(FINS_POSTS);
                break;

            case CLOTHING_POSTS:
                query = mDatabase.child(GEAR_POSTS).child(USED_GEAR_POSTS).child(CLOTHING_POSTS);
                break;

            case OTHER_POSTS:
                query = mDatabase.child(GEAR_POSTS).child(USED_GEAR_POSTS).child(OTHER_POSTS);
                break;
        }

        return query;
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
//                mCurrentGear = 0;
//                swapAdapter();
                break;

            case R.id.stores_button:
                Toast.makeText(getContext(), "Stores Section is not available at the moment.", Toast.LENGTH_SHORT).show();
//                setButtonChecked(mStoresButton);
//                mCurrentGear = 1;
//                swapAdapter();
                break;

        }
    }

    private void swapAdapter() {
        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery();
        Log.d(TAG, "initUi: " + postsQuery.toString());

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<GearPost>()
                .setQuery(postsQuery, GearPost.class)
                .build();

        GearAdapter newAdapter = new GearAdapter(options, this );

        newAdapter.startListening();

        mRecyclerView.swapAdapter(newAdapter, true);

        mAdapter = newAdapter;
    }

    @Override
    public void onGearPostSelected(GearPost post) {

    }

    @Override
    public void onFabClicked() {
        Log.d(TAG, "onFabClicked: ");
        try {
            FragmentManager fm = Objects.requireNonNull(getFragmentManager());

            mNewGearPostDialog = NewGearPostDialog.newInstance();
            mNewGearPostDialog.show(fm, mNewGearPostDialog.getTag());
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0 :
                mCurrentCategory = BOARDS_POSTS;
                break;

            case 1:
                mCurrentCategory = LEASHES_POSTS;
                break;

            case 2:
                mCurrentCategory = FINS_POSTS;
                break;

            case 3:
                mCurrentCategory = CLOTHING_POSTS;
                break;

            case 4:
                mCurrentCategory = OTHER_POSTS;
                break;

        }
        swapAdapter();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}






