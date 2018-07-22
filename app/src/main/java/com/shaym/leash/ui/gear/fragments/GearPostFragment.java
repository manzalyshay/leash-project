package com.shaym.leash.ui.gear.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.forum.Comment;
import com.shaym.leash.logic.gear.GearPost;
import com.shaym.leash.logic.user.Profile;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.shaym.leash.R.id;
import static com.shaym.leash.R.layout;
import static com.shaym.leash.logic.CONSTANT.POST_COMMENTS;
import static com.shaym.leash.logic.CONSTANT.USERS_TABLE;

public class GearPostFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "GearPostFragment";

    public static final String EXTRA_GEAR_POST_KEY = "gear_post_key";
    public static final String EXTRA_GEAR_TYPE_KEY = "gear_type_key";

    private DatabaseReference mGearPostReference;
    private DatabaseReference mCommentsReference;
    private ValueEventListener mPostListener;
    private String mGearPostKey;
    private String mGearType;
    private CommentAdapter mAdapter;

    private TextView mGearTitle;
    private TextView mGearPrice;
    private TextView mPostSeller;
    private TextView mSellerPhoneNumber;
    private ImageView mPostPhoto;
    private EditText mCommentField;
    private Button mCommentButton;
    private RecyclerView mCommentsRecycler;
    FirebaseStorage storage;
    StorageReference storageReference;
    public GearPostFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(layout.fragment_gear_post, container, false);
        Bundle args = getArguments();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        v.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        // Get post,forum key from intent
        mGearPostKey = args.getString(EXTRA_GEAR_POST_KEY);
        mGearType = args.getString(EXTRA_GEAR_TYPE_KEY);

        if (mGearPostKey == null || mGearType == null) {
            throw new IllegalArgumentException("Must pass EXTRA_GEAR_POST_KEY,EXTRA_GEAR_TYPE_KEY");
        }

        // Initialize Database
        mGearPostReference = FirebaseDatabase.getInstance().getReference()
                .child(mGearType).child(mGearPostKey);
        Log.d(TAG, "onCreateView: " + mGearPostReference);
        mCommentsReference = FirebaseDatabase.getInstance().getReference()
                .child(POST_COMMENTS).child(mGearPostKey);

        // Initialize Views
        mGearTitle = v.findViewById(id.post_gear_title);
        mGearPrice = v.findViewById(id.post_gear_price);
        mPostPhoto = v.findViewById(id.post_gear_photo);
        mPostSeller = v.findViewById(id.post_seller);
        mSellerPhoneNumber = v.findViewById(id.seller_phone_number);
        mCommentField = v.findViewById(id.field_comment_text);
        mCommentButton = v.findViewById(id.button_post_comment);
        mCommentsRecycler = v.findViewById(id.comments_list);

        mCommentButton.setOnClickListener(this);
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                GearPost gearPost = dataSnapshot.getValue(GearPost.class);
                // [START_EXCLUDE]
                if (gearPost != null) {
                    Log.d(TAG, "onDataChange: gearPost Displayed");
                    mGearTitle.setText(gearPost.title);
                    mGearPrice.setText(Integer.toString(gearPost.price));
                    mPostSeller.setText(gearPost.author);
                    mSellerPhoneNumber.setText(gearPost.phonenumber);
                    attachPic(gearPost.imageurl);
                    // [END_EXCLUDE]
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(getActivity(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mGearPostReference.addValueEventListener(postListener);
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        mPostListener = postListener;

        // Listen for comments
        mAdapter = new CommentAdapter(getActivity(), mCommentsReference);
        mCommentsRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mPostListener != null) {
            mGearPostReference.removeEventListener(mPostListener);
        }

        // Clean up comments listener
        mAdapter.cleanupListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == id.button_post_comment) {
            postComment();
        }
    }

    private void postComment() {
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user information

                        Profile user = dataSnapshot.getValue(Profile.class);


                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + uid + " is unexpectedly null");
                            Toast.makeText(getActivity(),
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            String authorName = user.getDisplayname();

                            // Create new comment object
                            String commentText = mCommentField.getText().toString();
                            Comment comment = new Comment(uid, authorName, commentText);

                            // Push the comment, it will appear in the list
                            mCommentsReference.push().setValue(comment);

                            // Clear the field
                            mCommentField.setText(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    private static class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView authorView;
        public TextView bodyView;

        public CommentViewHolder(View itemView) {
            super(itemView);

            authorView = itemView.findViewById(id.comment_author);
            bodyView = itemView.findViewById(id.comment_body);
        }
    }

    private static class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        private List<String> mCommentIds = new ArrayList<>();
        private List<Comment> mComments = new ArrayList<>();

        public CommentAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;

            // Create child event listener
            // [START child_event_listener_recycler]
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new comment has been added, add it to the displayed list
                    Comment comment = dataSnapshot.getValue(Comment.class);

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    mCommentIds.add(dataSnapshot.getKey());
                    mComments.add(comment);
                    notifyItemInserted(mComments.size() - 1);
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    Comment newComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Replace with the new data
                        mComments.set(commentIndex, newComment);

                        // Update the RecyclerView
                        notifyItemChanged(commentIndex);
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Remove data from the list
                        mCommentIds.remove(commentIndex);
                        mComments.remove(commentIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(commentIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    Comment movedComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);
            // [END child_event_listener_recycler]

            // Store reference to listener so it can be removed on app stop
            mChildEventListener = childEventListener;
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            Comment comment = mComments.get(position);
            holder.authorView.setText(comment.author);
            holder.bodyView.setText(comment.text);
        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

    }

    public void attachPic(String url){
        if (!url.isEmpty()) {
            storageReference.child(url).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(final Uri uri) {
                    Picasso.get().load(uri).resize(400,400).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().into(mPostPhoto, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            //Try again online if cache failed
                            Picasso.get()
                                    .load(uri)
                                    .error(R.drawable.ic_launcher)
                                    .into(mPostPhoto, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Log.v("Picasso","Could not fetch image" + e.toString());
                                        }
                                    });
                        }
                    });

                }
            });
        }
        else {
            Picasso.get().load(R.drawable.ic_launcher).resize(400,400).centerCrop().into(mPostPhoto);

        }
    }
}