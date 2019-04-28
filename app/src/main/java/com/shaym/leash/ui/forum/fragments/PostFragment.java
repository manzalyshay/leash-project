package com.shaym.leash.ui.forum.fragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;
import com.shaym.leash.ui.forum.ForumFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.POST_COMMENTS;
import static com.shaym.leash.logic.utils.CONSTANT.USERS_TABLE;

public class PostFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "PostDetailActivity";

    public static final String EXTRA_POST_KEY = "post_key";
    public static final String EXTRA_FORUM_KEY = "forum_key";

    private DatabaseReference mPostReference;
    private DatabaseReference mCommentsReference;
    private ValueEventListener mPostListener;
    private CommentAdapter mAdapter;

    private ProgressBar mAuthorPicProgressBar;
    private ImageView mAuthorPic;
    private ProgressBar mAttachProgressBar;
    private ImageView mAttachedPic;

    private TextView mAuthorView;
    private TextView mTitleView;
    private TextView mBodyView;
    private EditText mCommentField;
    private RecyclerView mCommentsRecycler;
    private RelativeLayout mAttachLayout;

    private DatabaseReference mDatabase;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Dialog mEnlargedImageDialog;

    public PostFragment () {
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_post, container, false);
        Bundle args = getArguments();

        mView.setOnTouchListener((v1, event) -> true);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Get post,forum key from intent
        assert args != null;
        String mPostKey = args.getString(EXTRA_POST_KEY);
        String mPostForum = args.getString(EXTRA_FORUM_KEY);

        if (mPostKey == null || mPostForum == null) {
            throw new IllegalArgumentException("Must pass EXTRA_CHAT_KEY,FORUMKEY");
        }


            mPostReference = FirebaseDatabase.getInstance().getReference()
                    .child(mPostForum).child(mPostKey);


        // Initialize Database

        Log.d(TAG, "onCreateView: " + mPostReference);
        mCommentsReference = FirebaseDatabase.getInstance().getReference()
                .child(POST_COMMENTS).child(mPostKey);

        // Initialize Views
        mAuthorPic = mView.findViewById(R.id.post_author_photo);
        mAuthorPicProgressBar = mView.findViewById(R.id.post_author_photo_progressbar);
        mAttachedPic = mView.findViewById(R.id.post_attached_image);

        mAttachProgressBar = mView.findViewById(R.id.post_attached_progressbar);
        mAuthorView = mView.findViewById(R.id.post_author);
        //mTitleView = mView.findViewById(R.id.post_title);
        mBodyView = mView.findViewById(R.id.post_body);
        mCommentField = mView.findViewById(R.id.field_comment_text);
        Button mCommentButton = mView.findViewById(R.id.button_post_comment);
        mCommentsRecycler = mView.findViewById(R.id.comments_list);
        mAttachLayout = mView.findViewById(R.id.attach_layout);

        mCommentButton.setOnClickListener(this);
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        return mView;
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
                    attachPic(post.attachment, mAttachedPic, mAttachProgressBar);

                    mBodyView.setText(post.body);
                    mDatabase.child(USERS_TABLE).child(post.uid).addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    // Get user value
                                    Profile profile = dataSnapshot.getValue(Profile.class);
                                    assert profile != null;
                                    FireBaseUsersHelper.getInstance().LoadUserPic(profile.getAvatarURL(), mAuthorPic, mAuthorPicProgressBar, 200);


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
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
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }


    private static class CommentViewHolder extends RecyclerView.ViewHolder {

        ImageView authorPic;
        ProgressBar authorPicProgressBar;
        TextView authorView;
        TextView bodyView;

        CommentViewHolder(View itemView) {
            super(itemView);
            authorPic = itemView.findViewById(R.id.comment_photo);
            authorPicProgressBar = itemView.findViewById(R.id.comment_photo_progressbar);
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

        CommentAdapter(final Context context, DatabaseReference ref) {
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

        @NonNull
        @Override
        public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
            Comment comment = mComments.get(position);
            holder.authorView.setText(comment.author);
            holder.bodyView.setText(comment.text);

            FireBaseUsersHelper.getInstance().getDatabase().child(USERS_TABLE).child(comment.uid).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Get user value
                            Profile profile = dataSnapshot.getValue(Profile.class);
                            assert profile != null;
                            FireBaseUsersHelper.getInstance().LoadUserPic(profile.getAvatarURL(), holder.authorPic, holder.authorPicProgressBar, 50);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        }
                    });

        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser){
            ForumFragment.mFab.setVisibility(View.INVISIBLE);
        }
        else {
            ForumFragment.mFab.setVisibility(View.VISIBLE);

        }
    }

    public void attachPic(String url, ImageView pic, ProgressBar progressBar){
        if (url != null && !url.isEmpty()) {
            mAttachLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
                storageReference.child(url).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).resize(400, 400).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().into(pic, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(uri).resize(400, 400).centerCrop().into(pic, new Callback() {
                            @Override
                            public void onSuccess() {

                                progressBar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                                //Try again online if cache failed
                                e.printStackTrace();

                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        });

                    }

                }));


                pic.setOnClickListener(v -> {
                    showEnlargedImageDialog(url);
                });
            }

    }

    private void showEnlargedImageDialog(String url) {
        // The method that displays the popup.
        mEnlargedImageDialog = new Dialog(Objects.requireNonNull(getActivity()));
        mEnlargedImageDialog.setContentView(R.layout.dialog_image_enlarge );
        ImageView closedialogpic = mEnlargedImageDialog.findViewById(R.id.imagedialog_close);
        ImageView imageView = mEnlargedImageDialog.findViewById(R.id.enlarged_image);
        ProgressBar progressBar = mEnlargedImageDialog.findViewById(R.id.enlarged_image_progressbar);
        closedialogpic.setOnClickListener(view -> mEnlargedImageDialog.dismiss());

        progressBar.setVisibility(View.VISIBLE);
        storageReference.child(url).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(uri).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        //Try again online if cache failed
                        e.printStackTrace();

                        progressBar.setVisibility(View.INVISIBLE);

                    }
                });

            }

        }));

        Objects.requireNonNull(mEnlargedImageDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mEnlargedImageDialog.show();
    }





}