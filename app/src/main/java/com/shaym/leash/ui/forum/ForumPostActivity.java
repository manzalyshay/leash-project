package com.shaym.leash.ui.forum;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.forum.Comment;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.forum.PostViewModel;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.user.UsersViewModel;
import com.shaym.leash.logic.utils.FireBasePostsHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.shaym.leash.logic.utils.CONSTANT.ALL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.FORUM_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.POST_COMMENTS;

public class ForumPostActivity extends FragmentActivity implements View.OnClickListener, TextWatcher {

    private static final String TAG = "ForumPostActivity";
    public static final String EXTRA_POST_KEY = "post_key";

    private DatabaseReference mCommentsReference;
    private String mPostKey;
    private CommentsAdapter mAdapter;
    private ProgressBar mAuthorProgressBar;
    private ImageView mAuthorPic;
    private ProgressBar mImageProgressBar;
    private ImageView mPostImage;
    private ImageView mBackBtn;
    private RelativeLayout mAttachLayout;
    private TextView mAuthorView;
    private TextView mBodyView;
    private TextView mDateView;

    private EditText mCommentField;
    private Button mCommentButton;
    private RecyclerView mCommentsRecycler;
    private DatabaseReference mDatabase;
    FirebaseStorage storage;
    StorageReference storageReference;
    private List<Profile> mAllUsers = new ArrayList<>();
    private Profile mUser;
    private UsersViewModel mUsersViewModel;
    private PostViewModel mPostViewModel;
    private Post mCurrentPost;
    boolean isKeyboardShowing = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_up,  R.anim.no_anim); // remember to put it after startActivity, if you put it to above, animation will not working

        Bundle b = getIntent().getExtras();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Get post,forum key from intent
        assert b != null;
        mPostKey = b.getString(EXTRA_POST_KEY);

        if (mPostKey == null ) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        mCommentsReference = mDatabase
                .child(POST_COMMENTS).child(mPostKey);

        setContentView(R.layout.activity_post);
    }



    @Override
    public void onStart() {
        super.onStart();
        initUI();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initUsersViewModel();
        initPostViewModel();
    }

    private void initUI() {
        // Initialize Views

        mAuthorPic = findViewById(R.id.profile_icon_toolbar);
        mAuthorProgressBar = findViewById(R.id.profile_icon_progressbar);
        mAuthorView = findViewById(R.id.display_name);
        mAttachLayout = findViewById(R.id.attach_layout);
        mPostImage = findViewById(R.id.post_attached_image);
        mImageProgressBar = findViewById(R.id.post_attached_progressbar);
        mBodyView = findViewById(R.id.post_body);
        mDateView = findViewById(R.id.publish_date);
        mBackBtn = findViewById(R.id.back_icon_toolbar);
        mBackBtn.setOnClickListener(this);
        mCommentField = findViewById(R.id.comment_field);
        mCommentField.addTextChangedListener(this);
        mCommentButton = findViewById(R.id.send_comment_button);
        mCommentButton.setOnClickListener(this);

        mCommentsRecycler = findViewById(R.id.comments_list);


        initKeyboardListener();
        setCommentsAdapter();

    }

    private void initKeyboardListener() {

        RelativeLayout rootlayout = findViewById(R.id.activity_post_rootview);


// ContentView is the root view of the layout of this activity/fragment
        rootlayout.getViewTreeObserver().addOnGlobalLayoutListener(
                () -> {

                    Rect r = new Rect();
                    rootlayout.getWindowVisibleDisplayFrame(r);
                    int screenHeight = rootlayout.getRootView().getHeight();

                    // r.bottom is the position above soft keypad or device button.
                    // if keypad is shown, the r.bottom is smaller than that before.
                    int keypadHeight = screenHeight - r.bottom;

                    Log.d(TAG, "keypadHeight = " + keypadHeight);

                    if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                        // keyboard is opened
                        if (!isKeyboardShowing) {
                            isKeyboardShowing = true;
                        }
                    }
                    else {
                        // keyboard is closed
                        if (isKeyboardShowing) {
                            isKeyboardShowing = false;
                        }
                    }
                });
    }



    private void initPostViewModel() {
        Log.d(TAG, "initPostViewModel: ");
        mPostViewModel = ViewModelProviders.of(this).get(PostViewModel.class);

        mPostViewModel.setPOST_LIVE_DATA(mDatabase.child(FORUM_POSTS).child(ALL_POSTS).child(mPostKey));
        mPostViewModel.setCOMMENTS_LIVE_DATA(mDatabase.child(POST_COMMENTS).child(mPostKey));
        LiveData<DataSnapshot> currentPostLiveData = mPostViewModel.getPostLiveData();
        LiveData<DataSnapshot> currentCommentPostLiveData = mPostViewModel.getCommentsLiveData();

        currentPostLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                Log.d(TAG, "initPostViewModel: Current post changed ");
                mCurrentPost = dataSnapshot.getValue(Post.class);
                updatePostUI();
            }
        });

        currentCommentPostLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<Comment> newData = new ArrayList<>();
                Log.d(TAG, "initPostViewModel: Comments data changed");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Comment comment = ds.getValue(Comment.class);
                    newData.add(comment);
                }
                mAdapter.setComments(newData);

            }
        });
    }



    @Override
    public void onBackPressed() {
        if (isKeyboardShowing)
            super.onBackPressed();
        else{
            finish();
            overridePendingTransition(R.anim.no_anim, R.anim.slide_down);

        }
    }

    private void updatePostUI() {
        mBodyView.setText(mCurrentPost.body);
        int dayspast = FireBasePostsHelper.getInstance().getDaysDifference(mCurrentPost.date);

        if (dayspast != 0) {
            mDateView.setText("לפני " + dayspast + " ימים");
        }
        else {
            mDateView.setText("היום");

        }

        if (mCurrentPost.images != null){
            mAttachLayout.setVisibility(View.VISIBLE);
            FireBasePostsHelper.getInstance().attachPic(mCurrentPost.images.get(0), mPostImage, mImageProgressBar, 200, 200);
        }


    }

    private void setCommentsAdapter() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setItemPrefetchEnabled(true);
//        layoutManager.setStackFromEnd(true);

        mCommentsRecycler.setLayoutManager(layoutManager);
        // Listen for comments
        mAdapter = new CommentsAdapter();
        mCommentsRecycler.setAdapter(mAdapter);
    }

    private void initUsersViewModel() {
        Log.d(TAG, "initUsersViewModel: ");

        mUsersViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);

        LiveData<DataSnapshot> currentUserLiveData = mUsersViewModel.getCurrentUserDataSnapshotLiveData();

        currentUserLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                Log.d(TAG, "initUsersViewModel: Current User Changed");
                mUser = dataSnapshot.getValue(Profile.class);
                updateUserPostUI();
            }
        });

        LiveData<DataSnapshot> allUserLiveData = mUsersViewModel.getAllUsersDataSnapshotLiveData();

        allUserLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                mAllUsers.clear();
                Log.d(TAG, "initAllUsersObserver: Users data changed");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Profile user = ds.getValue(Profile.class);
                    mAllUsers.add(user);
                }
                mAdapter.setAllUsers(mAllUsers);

            }
        });
    }

    private void updateUserPostUI() {
        mAuthorView.setText(mUser.getDisplayname());

        FireBasePostsHelper.getInstance().attachRoundPic(mUser.getAvatarurl(), mAuthorPic, mAuthorProgressBar, 100, 100);
    }



    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.send_comment_button) {
            postComment();
        }
        else if (i == R.id.back_icon_toolbar){
            finish();
            overridePendingTransition(R.anim.no_anim, R.anim.slide_down);

        }
    }

    private void postComment() {
        // Create new comment object
        String commentText = mCommentField.getText().toString();
        Comment comment = new Comment(mUser.getUid(),mCommentsReference.push().getKey(), new Date(), commentText);

        // Push the comment, it will appear in the list
        mCommentsReference.push().setValue(comment);

        // Clear the field
        mCommentField.setText(null);
        mCommentsRecycler.postDelayed(() -> mCommentsRecycler.scrollToPosition(mCommentsRecycler.getAdapter().getItemCount() - 1), 500);

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (count > 0)
            mCommentButton.setEnabled(true);
        else
            mCommentButton.setEnabled(false);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
