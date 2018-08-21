package com.shaym.leash.ui.forum.fragments;

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
import android.widget.ProgressBar;
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
import com.shaym.leash.logic.aroundme.CircleTransform;
import com.shaym.leash.logic.forum.Comment;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.ui.forum.ForumActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

import static com.shaym.leash.logic.CONSTANT.DIRECT_INCOMING_MESSAGES;
import static com.shaym.leash.logic.CONSTANT.DIRECT_OUTGOING_MESSAGES;
import static com.shaym.leash.logic.CONSTANT.POST_COMMENTS;
import static com.shaym.leash.logic.CONSTANT.USERS_TABLE;
import static com.shaym.leash.logic.CONSTANT.USER_POSTS;
import static tcking.github.com.giraffeplayer2.GiraffePlayer.TAG;

public class PostFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "PostDetailActivity";

    public static final String EXTRA_POST_KEY = "post_key";
    public static final String EXTRA_FORUM_KEY = "forum_key";
    public static final String EXTRA_DM_KEY = "dm_key";
    public static final String STRING_IDM = "IDM";
    public static final String STRING_ODM = "ODM";

    private DatabaseReference mPostReference;
    private DatabaseReference mCommentsReference;
    private ValueEventListener mPostListener;
    private String mPostKey;
    private String mPostForum;
    private CommentAdapter mAdapter;
    private ProgressBar mProgressBar;
    private ImageView mAuthorPic;
    private TextView mAuthorView;
    private TextView mTitleView;
    private TextView mBodyView;
    private EditText mCommentField;
    private Button mCommentButton;
    private RecyclerView mCommentsRecycler;
    private DatabaseReference mDatabase;
    FirebaseStorage storage;
    StorageReference storageReference;

    private String mDMType;


    public PostFragment () {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post, container, false);
        Bundle args = getArguments();

        v.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Get post,forum key from intent
        mPostKey = args.getString(EXTRA_POST_KEY);
        mPostForum = args.getString(EXTRA_FORUM_KEY);
        mDMType = args.getString(EXTRA_DM_KEY);

        if (mPostKey == null || mPostForum == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY,FORUMKEY");
        }

        switch (mDMType) {
            case STRING_ODM:
            mPostReference = FirebaseDatabase.getInstance().getReference()
                    .child(DIRECT_OUTGOING_MESSAGES).child(getUid()).child(mPostKey);
                    break;

            case STRING_IDM:
                mPostReference = FirebaseDatabase.getInstance().getReference()
                    .child(DIRECT_INCOMING_MESSAGES).child(getUid()).child(mPostKey);
                break;
                default:
            mPostReference = FirebaseDatabase.getInstance().getReference()
                    .child(mPostForum).child(mPostKey);
            break;
        }

        // Initialize Database

        Log.d(TAG, "onCreateView: " + mPostReference);
        mCommentsReference = FirebaseDatabase.getInstance().getReference()
                .child(POST_COMMENTS).child(mPostKey);

        // Initialize Views
        mAuthorPic = v.findViewById(R.id.post_author_photo);
        mProgressBar = v.findViewById(R.id.post_author_photo_progressbar);
        mAuthorView = v.findViewById(R.id.post_author);
        mTitleView = v.findViewById(R.id.post_title);
        mBodyView = v.findViewById(R.id.post_body);
        mCommentField = v.findViewById(R.id.field_comment_text);
        mCommentButton = v.findViewById(R.id.button_post_comment);
        mCommentsRecycler = v.findViewById(R.id.comments_list);

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
                Post post = dataSnapshot.getValue(Post.class);
                // [START_EXCLUDE]
                if (post != null) {
                    Log.d(TAG, "onDataChange: Post Displayed");
                    mAuthorView.setText(post.author);
                    mTitleView.setText(post.title);
                    mBodyView.setText(post.body);
                    mDatabase.child(USERS_TABLE).child(post.uid).addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Get user value
                                    Profile profile = dataSnapshot.getValue(Profile.class);
                                    attachPic(profile.getAvatarURL());

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                }
                            });
                    // [END single_value_read]
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
        mPostReference.addValueEventListener(postListener);
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
            mPostReference.removeEventListener(mPostListener);
        }

        // Clean up comments listener
        mAdapter.cleanupListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_post_comment) {
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

            authorView = itemView.findViewById(R.id.comment_author);
            bodyView = itemView.findViewById(R.id.comment_body);
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
            View view = inflater.inflate(R.layout.item_comment, parent, false);
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
                    Picasso.get().load(uri).resize(200,200).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new CircleTransform()).into(mAuthorPic, new Callback() {
                        @Override
                        public void onSuccess() {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            mAuthorPic.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            //Try again online if cache failed
                            Picasso.get()
                                    .load(uri)
                                    .error(R.drawable.ic_launcher)
                                    .transform(new CircleTransform()).into(mAuthorPic, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            mProgressBar.setVisibility(View.INVISIBLE);
                                            mAuthorPic.setVisibility(View.VISIBLE);
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
            Picasso.get().load(R.drawable.ic_launcher).resize(200,200).centerCrop().transform(new CircleTransform()).into(mAuthorPic, new Callback() {
                @Override
                public void onSuccess() {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mAuthorPic.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Exception e) {

                }
            });

        }
    }
}