package com.shaym.leash.ui.gear.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.gear.GearPost;
import com.shaym.leash.ui.forum.ForumActivity;
import com.shaym.leash.ui.forum.PostViewHolder;
import com.shaym.leash.ui.forum.fragments.PostFragment;
import com.shaym.leash.ui.gear.GearActivity;
import com.shaym.leash.ui.home.profile.UserGearPostsFragment;

import static com.shaym.leash.logic.CONSTANT.ALL_GEAR_POSTS;
import static com.shaym.leash.logic.CONSTANT.ALL_POSTS;
import static com.shaym.leash.logic.CONSTANT.GENERAL_POSTS;
import static com.shaym.leash.logic.CONSTANT.NEW_GEAR_POSTS;
import static com.shaym.leash.logic.CONSTANT.SPOTS_POSTS;
import static com.shaym.leash.logic.CONSTANT.TRIPS_POSTS;
import static com.shaym.leash.logic.CONSTANT.USED_GEAR_POSTS;
import static com.shaym.leash.logic.CONSTANT.USER_GEAR_POSTS;
import static com.shaym.leash.logic.CONSTANT.USER_POSTS;
import static com.shaym.leash.ui.forum.fragments.PostFragment.EXTRA_FORUM_KEY;
import static com.shaym.leash.ui.forum.fragments.PostFragment.EXTRA_POST_KEY;
import static com.shaym.leash.ui.gear.fragments.GearPostFragment.EXTRA_GEAR_POST_KEY;
import static com.shaym.leash.ui.gear.fragments.GearPostFragment.EXTRA_GEAR_TYPE_KEY;

public abstract class GearFragment extends Fragment {
    private static final String TAG = "ForumFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<GearPost, GearPostViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private String mType;
    private int frameid;
    private boolean fabExists;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        int layoutname = 0;
        int listid = 0;
        View v = null;

        if (GearFragment.this instanceof UserGearPostsFragment){
            layoutname = R.layout.fragment_user_gearposts;
            frameid = R.id.root_frame_usergearposts;
            listid =R.id.usergear_postslist;
            fabExists = false;
        }
        else if (GearFragment.this instanceof NewGearFragment){
            layoutname = R.layout.fragment_newgear;
            frameid = R.id.root_frame_newgear;
            listid =R.id.new_gear_list;
            fabExists = true;

        }
        else if (GearFragment.this instanceof UsedGearFragment){
            layoutname = R.layout.fragment_usedgear;
            frameid = R.id.root_frame_usedgear;
            listid =R.id.used_gear_list;
            fabExists = true;
        }

        v = inflater.inflate(layoutname, container, false);
        mRecycler = v.findViewById(listid);

        mRecycler.setHasFixedSize(true);

        return v;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<GearPost>()
                .setQuery(postsQuery, GearPost.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<GearPost, GearPostViewHolder>(options) {

            @Override
            public GearPostViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new GearPostViewHolder(inflater.inflate(R.layout.item_gear_post, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(GearPostViewHolder viewHolder, int position, final GearPost model) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                mType = model.type;
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        // Create new fragment and transaction
                        if (fabExists) {
                            GearActivity.mGearFab.setVisibility(View.INVISIBLE);
                        }
                        switch (mType){
                            case NEW_GEAR_POSTS:
                                GearActivity.mNewGearPostOpened = true;
                                break;
                            case USED_GEAR_POSTS:
                                GearActivity.mUsedGearPostOpened = true;
                                break;
                        }
                        GearPostFragment f = new GearPostFragment();
                        // Supply index input as an argument.
                        Bundle args = new Bundle();
                        args.putString(EXTRA_GEAR_POST_KEY, postKey);
                        args.putString(EXTRA_GEAR_TYPE_KEY, mType);

                        f.setArguments(args);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
                        transaction.replace(frameid, f, postKey);

                        transaction.addToBackStack(null);

                        transaction.commit();
                    }
                });

                // Determine if the current user has liked this post and set UI accordingly
                if (model.stars.containsKey(getUid())) {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
                } else {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
                }

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // Need to write to both places the post is stored
                        DatabaseReference globalPostRef = mDatabase.child(ALL_GEAR_POSTS).child(postRef.getKey());
                        DatabaseReference userPostRef = mDatabase.child(USER_POSTS).child(model.uid).child(postRef.getKey());

                        // Run two transactions
                        onStarClicked(globalPostRef);
                        onStarClicked(userPostRef);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View deleteview) {
                        // Need to write to both places the post is stored
                        try {
                            mDatabase.child(ALL_GEAR_POSTS).child(postRef.getKey()).getRef().removeValue();
                            mDatabase.child(USER_GEAR_POSTS).child(model.uid).child(postRef.getKey()).getRef().removeValue();
                            mDatabase.child(model.type).child(postRef.getKey()).getRef().removeValue();
                        }
                        catch (Exception e){
                            Log.d(TAG, "onClick: Cannot Delete Record");
                        }
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }



    // [START post_stars_transaction]
    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);


}
