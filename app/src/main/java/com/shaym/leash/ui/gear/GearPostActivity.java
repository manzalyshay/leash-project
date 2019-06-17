package com.shaym.leash.ui.gear;

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
import com.shaym.leash.logic.forum.PostViewModel;
import com.shaym.leash.logic.gear.GearPost;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.user.UsersViewModel;
import com.shaym.leash.logic.utils.FireBasePostsHelper;
import com.shaym.leash.ui.forum.CommentsAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.shaym.leash.logic.utils.CONSTANT.ALL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.POST_COMMENTS;
import static com.shaym.leash.logic.utils.CONSTANT.USED_GEAR_POSTS;

public class GearPostActivity extends FragmentActivity implements View.OnClickListener, TextWatcher {

    private static final String TAG = "PostDetailActivity";

    public static final String EXTRA_POST_KEY = "post_key";

    private DatabaseReference mCommentsReference;
    private String mPostKey;
    private CommentsAdapter mAdapter;
    private ProgressBar mProgressBar;
    private ImageView mAuthorPic;
    private TextView mAuthorView;
    private TextView mBodyView;
    private TextView mDateView;
    private TextView mLocationView;

    private ImageView mGearThumb;
    private ImageView mBackBtn;

    private ProgressBar mGearThumbProgressBar;
    private TextView mPriceView;


    private EditText mCommentField;
    private Button mCommentButton;
    private RecyclerView mCommentsRecycler;
    private DatabaseReference mDatabase;
    private Profile mUser;
    private GearPost mCurrentPost;
    boolean isKeyboardShowing = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_up,  R.anim.no_anim); // remember to put it after startActivity, if you put it to above, animation will not working

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Bundle b = getIntent().getExtras();
        // Get post,Category key from intent
        assert b != null;
        mPostKey = b.getString(EXTRA_POST_KEY);

        if (mPostKey == null ) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        mCommentsReference = mDatabase
                .child(POST_COMMENTS).child(mPostKey);
        
        setContentView(R.layout.activity_gearpost);
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
        mAuthorPic = findViewById(R.id.gear_post_author_photo);
        mProgressBar = findViewById(R.id.gear_post_author_photo_progressbar);
        mAuthorView = findViewById(R.id.gear_post_author);
        mBodyView = findViewById(R.id.gear_post_body);
        mDateView = findViewById(R.id.gear_post_date);
        mPriceView = findViewById(R.id.price_amount);
        mBackBtn = findViewById(R.id.back_icon_toolbar);
        mLocationView = findViewById(R.id.gear_post_location);
        mGearThumb = findViewById(R.id.gear_thumb);
        mGearThumbProgressBar = findViewById(R.id.gear_thumb_progress);
        mCommentField = findViewById(R.id.comment_field);
        mCommentField.addTextChangedListener(this);
        mCommentButton = findViewById(R.id.send_comment_button);
        mCommentButton.setOnClickListener(this);

        mCommentsRecycler = findViewById(R.id.comments_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setItemPrefetchEnabled(true);

        mCommentsRecycler.setLayoutManager(layoutManager);

        initKeyboardListener();
        setCommentsAdapter();

    }

    private void initKeyboardListener() {
        RelativeLayout rootlayout = findViewById(R.id.activity_gear_post_rootview);

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
        PostViewModel mPostViewModel = ViewModelProviders.of(this).get(PostViewModel.class);

        mPostViewModel.setPOST_LIVE_DATA(mDatabase.child(GEAR_POSTS).child(USED_GEAR_POSTS).child(ALL_POSTS).child(mPostKey));
        mPostViewModel.setCOMMENTS_LIVE_DATA(mDatabase.child(POST_COMMENTS).child(mPostKey));

        LiveData<DataSnapshot> currentPostLiveData = mPostViewModel.getPostLiveData();
        LiveData<DataSnapshot> currentPostCommentsLiveData = mPostViewModel.getCommentsLiveData();


        currentPostLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                Log.d(TAG, "initPostViewModel: Current post changed ");
                mCurrentPost = dataSnapshot.getValue(GearPost.class);
                 updatePostUI();
            }
        });

        currentPostCommentsLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<Comment> newdata = new ArrayList<>();
                Log.d(TAG, "initCommentsObserver: Comments data changed");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Comment comment = ds.getValue(Comment.class);
                    newdata.add(comment);
                }

                mAdapter.setComments(newdata);
            }
        });
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
        mLocationView.setText(mCurrentPost.location);

        if (mCurrentPost.images != null) {
            FireBasePostsHelper.getInstance().attachPic(mCurrentPost.images.get(0), mGearThumb, mGearThumbProgressBar, 200, 200);
        }
        mPriceView.setText(Integer.toString(mCurrentPost.price));


    }

    private void updateUsersPostUI(){
        mAuthorView.setText(mUser.getDisplayname());
        FireBasePostsHelper.getInstance().attachRoundPic(mUser.getAvatarurl(), mAuthorPic, mProgressBar, 100, 100);

    }




    private void setCommentsAdapter() {
        mAdapter = new CommentsAdapter();
        mCommentsRecycler.setAdapter(mAdapter);
    }

    private void initUsersViewModel() {
        Log.d(TAG, "initUsersViewModel: ");

        UsersViewModel mUsersViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);

        LiveData<DataSnapshot> currentUserLiveData = mUsersViewModel.getCurrentUserDataSnapshotLiveData();
        LiveData<DataSnapshot> allUserLiveData = mUsersViewModel.getAllUsersDataSnapshotLiveData();

        currentUserLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                Log.d(TAG, "initUsersViewModel: Current User Changed");
                mUser = dataSnapshot.getValue(Profile.class);
                updateUsersPostUI();
            }
        });


        allUserLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<Profile> allusers = new ArrayList<>();
                Log.d(TAG, "initAllUsersObserver: Users data changed");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Profile user = ds.getValue(Profile.class);
                    allusers.add(user);
                }

                mAdapter.setAllUsers(allusers);
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


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.send_comment_button) {
            postComment();
        }
        else if ( i == R.id.back_icon_toolbar){
            finish();
            overridePendingTransition(R.anim.no_anim, R.anim.slide_down);

        }
    }

    private void postComment() {
        // Create new comment object
        String commentText = mCommentField.getText().toString();
        Comment comment = new Comment(mUser.getUid(), mCommentsReference.push().getKey(), new Date(), commentText);

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
