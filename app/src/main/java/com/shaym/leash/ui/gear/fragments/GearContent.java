package com.shaym.leash.ui.gear.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.shaym.leash.logic.gear.BoardGearPost;

import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.BOARDS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USED_GEAR_POSTS;

public  class GearContent extends Fragment {
    private static final String TAG = "ForumFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private BoardsAdapter mAdapter;
    private RecyclerView mRecycler;
    private String mCurrentCategory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]
        mCurrentCategory = BOARDS_POSTS;
        View v  = inflater.inflate(R.layout.fragment_gear_content, container, false);
        mRecycler = v.findViewById(R.id.gear_list);

        mRecycler.setHasFixedSize(true);

        return v;
    }


    @Override
    public void onStart() {
        super.onStart();

        // Set up Layout Manager, reverse layout
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<BoardGearPost>()
                .setQuery(postsQuery, BoardGearPost.class)
                .build();

        mAdapter = new BoardsAdapter(options);
        mRecycler.setAdapter(mAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//
//            @NonNull
//            @Override
//            public GearPostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
//                return new GearPostViewHolder(inflater.inflate(R.layout.item_gear_post, viewGroup, false));
//            }
//
//            @Override
//            protected void onBindViewHolder(@NonNull GearPostViewHolder viewHolder, int position, @NonNull final GearPost model) {
//                final DatabaseReference postRef = getRef(position);
//
//                // Set click listener for the whole post view
//                final String postKey = postRef.getKey();
//                mType = model.category;
//                viewHolder.itemView.setOnClickListener(v -> {
//                    // Launch PostDetailActivity
//                    // Create new fragment and transaction
//
//                    if (GearContent.mGearFab != null){
//                        GearContent.mGearFab.setVisibility(View.INVISIBLE);
//                    }
//
//
//                    GearPostFragment f = new GearPostFragment();
//                    // Supply index input as an argument.
//                    Bundle args = new Bundle();
//                    args.putString(EXTRA_GEAR_POST_KEY, postKey);
//                    args.putString(EXTRA_GEAR_TYPE_KEY, mType);
//
//                    f.setArguments(args);
//                    assert getFragmentManager() != null;
//                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//// Replace whatever is in the fragment_container view with this fragment,
//// and add the transaction to the back stack if needed
//                    transaction.replace(frameid, f, postKey);
//
//                    transaction.addToBackStack(null);
//
//                    transaction.commit();
//                });
//
//                // Determine if the current user has liked this post and set UI accordingly
////                if (model.stars.containsKey(getUid())) {
////                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
////                } else {
////                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
////                }
//
//                // Bind Post to ViewHolder, setting OnClickListener for the star button
//                viewHolder.bindToPost(model, starView -> {
//                    // Need to write to both places the post is stored
//                    DatabaseReference globalPostRef = mDatabase.child(ALL_GEAR_POSTS).child(Objects.requireNonNull(postRef.getKey()));
//                    DatabaseReference userPostRef = mDatabase.child(USER_POSTS).child(model.uid).child(postRef.getKey());
//
//                    // Run two transactions
//                    onStarClicked(globalPostRef);
//                    onStarClicked(userPostRef);
//                }, deleteview -> {
//                    // Need to write to both places the post is stored
//                    try {
//                        mDatabase.child(ALL_GEAR_POSTS).child(Objects.requireNonNull(postRef.getKey())).getRef().removeValue();
//                        mDatabase.child(USER_GEAR_POSTS).child(model.uid).child(postRef.getKey()).getRef().removeValue();
//                        mDatabase.child(model.category).child(postRef.getKey()).getRef().removeValue();
//                    }
//                    catch (Exception e){
//                        Log.d(TAG, "onClick: Cannot Delete Record");
//                    }
//                });
//            }
//        };
    }

        private Query getQuery() {
        return mDatabase.child(GEAR_POSTS).child(USED_GEAR_POSTS).child(mCurrentCategory);

    }

    private void setCurrentCategory(String category){
        this.mCurrentCategory = category;
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



    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }



}
