package com.shaym.leash.ui.forum.fragments;

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
import com.shaym.leash.R;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.ui.forum.ForumActivity;
import com.shaym.leash.ui.forum.PostViewHolder;
import com.shaym.leash.ui.home.profile.UserPostsFragment;

import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.ALL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GENERAL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.SPOTS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.TRIPS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USER_POSTS;
import static com.shaym.leash.ui.forum.ForumActivity.mFab;
import static com.shaym.leash.ui.forum.fragments.PostFragment.EXTRA_FORUM_KEY;
import static com.shaym.leash.ui.forum.fragments.PostFragment.EXTRA_POST_KEY;

public abstract class ForumFragment extends Fragment {
    private static final String TAG = "ForumFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Post, PostViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private int frameid;
    private boolean fabExists;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        int layoutname = 0;
        View v;
        int listid = 0;



         if (ForumFragment.this instanceof UserPostsFragment){
            layoutname = R.layout.fragment_user_posts;
            frameid = R.id.root_frame_userposts;
            listid = R.id.user_posts_list;
            fabExists = false;
        }
        else if (ForumFragment.this instanceof GeneralFragment){
            layoutname = R.layout.fragment_general;
            frameid = R.id.root_frame_general;
            listid = R.id.general_posts_list;
            fabExists = true;
        }
        else if (ForumFragment.this instanceof TripsFragment){
            layoutname = R.layout.fragment_trips;
            frameid = R.id.root_frame_trips;
            listid = R.id.trips_posts_list;
            fabExists = true;
        }
        else if (ForumFragment.this instanceof SpotsFragment){
            layoutname = R.layout.fragment_spots;
            frameid = R.id.root_frame_spots;
            listid = R.id.spots_posts_list;
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
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        Log.d(TAG, "onActivityCreated: " + postsQuery.toString());

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(postsQuery, Post.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                Log.d(TAG, "onCreateViewHolder: ");
                return new PostViewHolder(inflater.inflate(R.layout.item_post, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder viewHolder, int position, @NonNull final Post model) {
                final DatabaseReference postRef = getRef(position);
                Log.d(TAG, "onBindViewHolder: ");
                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                final String currentag = model.forum;
                viewHolder.itemView.setOnClickListener(v -> {

                    switch (currentag){
                        case GENERAL_POSTS:
                            ForumActivity.GeneralPostOpened = true;
                            break;
                        case SPOTS_POSTS:
                            ForumActivity.SpotsPostOpened = true;
                            break;
                        case TRIPS_POSTS:
                            ForumActivity.TripsPostOpened = true;
                            break;
                    }
                    // Launch PostDetailActivity
                    // Create new fragment and transaction
                    if (fabExists) {
                        mFab.setVisibility(View.INVISIBLE);
                    }
                    PostFragment f = new PostFragment();
                    // Supply index input as an argument.
                    Bundle args = new Bundle();
                    args.putString(EXTRA_POST_KEY, postKey);
                    args.putString(EXTRA_FORUM_KEY, currentag);


                    f.setArguments(args);
                    assert getFragmentManager() != null;
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
                    transaction.replace(frameid, f, postKey);

                    transaction.addToBackStack(null);

                    transaction.commit();
                });

                // Determine if the current user has liked this post and set UI accordingly
                if (model.stars.containsKey(getUid())) {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
                } else {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
                }

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, starView -> {

                    // Need to write to both places the post is stored
                    DatabaseReference globalPostRef = mDatabase.child(ALL_POSTS).child(Objects.requireNonNull(postRef.getKey()));
                    DatabaseReference userPostRef = mDatabase.child(USER_POSTS).child(model.uid).child(postRef.getKey());

                    // Run two transactions
                    onStarClicked(globalPostRef);
                    onStarClicked(userPostRef);

                }, deleteview -> {
                    // Need to write to both places the post is stored
                    try {
                        mDatabase.child(ALL_POSTS).child(Objects.requireNonNull(postRef.getKey())).getRef().removeValue();
                        mDatabase.child(USER_POSTS).child(model.uid).child(postRef.getKey()).getRef().removeValue();
                        mDatabase.child(model.forum).child(postRef.getKey()).getRef().removeValue();
                    }
                    catch (Exception e){
                        Log.d(TAG, "onClick: Cannot Delete Record");
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    // [START post_stars_transaction]
    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
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
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}
