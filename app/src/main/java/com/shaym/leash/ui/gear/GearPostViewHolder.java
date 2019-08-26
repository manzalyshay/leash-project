package com.shaym.leash.ui.gear;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
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
import com.shaym.leash.logic.forum.ForumViewModel;
import com.shaym.leash.logic.gear.GearPost;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.CONSTANT;
import com.shaym.leash.logic.utils.FireBasePostsHelper;
import com.shaym.leash.ui.forum.CommentsAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.shaym.leash.ui.home.chat.ChatDialog.getUid;

class GearPostViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener, View.OnClickListener, TextWatcher {
    private static final String TAG = "GearPostViewHolder";
    private EditText desciption;
    private EditText priceView;
    private TextView authorName;
    private TextView gearpostdate;
    private TextView gearpostlocation;

    private ImageView gearPic;
    private ImageView settingsPic;

    private StorageReference storageReference;
    private ProgressBar progressBar;
    private ProgressBar authorProgressBar;

    private DatabaseReference mDatabase;
    private ImageView authorPic;
    private GearPost currentPost;
    private Fragment mFragment;
    private Profile postProfile;
    private boolean isExpanded;
    private ForumViewModel mForumViewModel;
    private CommentsAdapter mAdapter;
    private EditText mCommentField;
    private TextView mCommentButton;
    private TextView mCommentsAmount;
    private ImageView mExpandArrow;
    private RecyclerView mCommentsRecycler;
    private ImageView postCommentImage;
    private ProgressBar postCommentImageProgressBar;
    private DatabaseReference mCommentsReference;
    private List<Profile> mAllUsers;
    private RelativeLayout mCommentForm;
    private List<Comment> mCommentList = new ArrayList<>();
    private RelativeLayout mCommentsLayout;
    private boolean isCommentExpanded;
    private boolean isArrowExpanded;
    private Profile mUser;
    private Button mSaveEditBtn;

    GearPostViewHolder(View itemView) {
        super(itemView);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        gearpostdate = itemView.findViewById(R.id.gear_post_date);
        gearpostlocation = itemView.findViewById(R.id.gear_post_location);

        priceView = itemView.findViewById(R.id.price_amount);

        priceView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty() && !desciption.getText().toString().isEmpty()){
                    mSaveEditBtn.setEnabled(true);
                }
                else {
                    mSaveEditBtn.setEnabled(false);

                }
            }
        });

        gearPic = itemView.findViewById(R.id.gear_thumb);
        progressBar = itemView.findViewById(R.id.gear_thumb_progress);
        authorName = itemView.findViewById(R.id.gear_post_author);
        authorPic = itemView.findViewById(R.id.gear_post_author_photo);
        authorProgressBar = itemView.findViewById(R.id.gear_post_author_photo_progressbar);
        settingsPic = itemView.findViewById(R.id.settings_icon);
        mSaveEditBtn = itemView.findViewById(R.id.save_edit_btn);
        mSaveEditBtn.setOnClickListener(this);

        desciption = itemView.findViewById(R.id.gear_post_body);
        desciption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty() && !priceView.getText().toString().isEmpty()){
                    mSaveEditBtn.setEnabled(true);
                }
                else {
                    mSaveEditBtn.setEnabled(false);

                }

            }
        });


        mExpandArrow = itemView.findViewById(R.id.expand_icon);
        itemView.setOnClickListener(this);
        mCommentField = itemView.findViewById(R.id.comment_field);
        mCommentField.addTextChangedListener(this);
        mCommentButton = itemView.findViewById(R.id.send_comment_button);
        mCommentButton.setOnClickListener(v -> postComment());
        mCommentsRecycler = itemView.findViewById(R.id.comments_list);
        postCommentImage = itemView.findViewById(R.id.post_comment_photo);
        postCommentImageProgressBar = itemView.findViewById(R.id.post_comment_photo_progressbar);
        mCommentsAmount = itemView.findViewById(R.id.comments_amount);
        mCommentForm = itemView.findViewById(R.id.comment_form);
        mCommentsLayout = itemView.findViewById(R.id.comments_recycler_layout);
    }


    private void setCommentsAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mFragment.getContext());
        layoutManager.setItemPrefetchEnabled(true);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mCommentsRecycler.setLayoutManager(layoutManager);
        // Listen for comments
        mAdapter = new CommentsAdapter(currentPost);
        mAdapter.setAllUsers(mAllUsers);
        mCommentsRecycler.setAdapter(mAdapter);
    }

    private void postComment() {
        // Create new comment object
        String commentText = mCommentField.getText().toString();

        FireBasePostsHelper.getInstance().writeNewComment(mUser.getUid(), currentPost.category, currentPost.key, commentText );

        // Clear the field
        mCommentField.setText(null);
        mCommentsRecycler.postDelayed(() -> mCommentsRecycler.scrollToPosition(mCommentsRecycler.getAdapter().getItemCount() - 1), 500);

    }

    @SuppressLint("SetTextI18n")
    void bindToPost(GearPost post, Fragment fragment, Profile postprofile, Profile user,  List<Profile> allUsers) {
        mAllUsers = allUsers;
        mUser = user;
        postProfile = postprofile;
        mFragment = fragment;
        currentPost = post;
        setCommentsAdapter();

        settingsPic.setOnClickListener(this::showPopup);

        priceView.setText(Integer.toString(post.price));
        desciption.setText(post.body);

        gearpostlocation.setText(post.location);

        int dayspast = FireBasePostsHelper.getInstance().getDaysDifference(post.publishdate);
        if (dayspast != 0) {
            gearpostdate.setText("לפני " + dayspast + " ימים");
        }
        else {
            gearpostdate.setText("היום");

        }

        if (post.images != null && post.images.size() > 0) {
            FireBasePostsHelper.getInstance().attachPic(post.images.get(0), gearPic, progressBar, 200, 200);
        }
        else {
            progressBar.setVisibility(View.GONE);
        }


        if (postProfile != null && mUser != null) {
            FireBasePostsHelper.getInstance().attachRoundPic(postProfile.getAvatarurl(), authorPic, authorProgressBar, 100, 100);
            FireBasePostsHelper.getInstance().attachRoundPic(mUser.getAvatarurl(), postCommentImage, postCommentImageProgressBar, 100, 100);
            authorPic.setOnClickListener(v -> FireBasePostsHelper.getInstance().showProfilePopup(mUser, postProfile, mFragment));

            authorName.setText(postProfile.getDisplayname());
        }

        mForumViewModel = ViewModelProviders.of(fragment).get(ForumViewModel.class);
        mCommentsReference = FirebaseDatabase.getInstance().getReference().child(CONSTANT.POST_COMMENTS).child(post.category).child(post.key);
        mForumViewModel.setCOMMENTS_LIVE_DATA(mCommentsReference);
        LiveData<DataSnapshot> currentCommentPostLiveData = mForumViewModel.getCommentsLiveData();


        currentCommentPostLiveData.observe(fragment, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<Comment> newData = new ArrayList<>();
                Log.d(TAG, "initPostViewModel: Comments data changed");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Comment comment = ds.getValue(Comment.class);
                    newData.add(comment);
                }
                mAdapter.setComments(newData);
                mCommentList.clear();
                mCommentList.addAll(newData);
                mCommentsAmount.setText(String.valueOf(mCommentList.size()));

            }
        });
    }

    public void showCommentForm(boolean state){
        if (state){
            if ((mCommentList.size() == 0 || isExpanded) && !isCommentExpanded) {
                mCommentForm.setVisibility(View.VISIBLE);
                setArrowExpanded(true);
                isCommentExpanded = true;
            }
        }
        else {
            if (isCommentExpanded) {
                mCommentForm.setVisibility(View.GONE);
                setArrowExpanded(false);
                isCommentExpanded = false;
            }
        }
    }

    private void setArrowExpanded(boolean expanded) {
        if (expanded){
            if (!isArrowExpanded) {
                RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(500);
                rotate.setInterpolator(new LinearInterpolator());
                rotate.setFillAfter(true);

                mExpandArrow.startAnimation(rotate);
                isArrowExpanded = true;
            }
        }

        else {
            if (isArrowExpanded) {
                RotateAnimation rotate = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(500);
                rotate.setInterpolator(new LinearInterpolator());
                rotate.setFillAfter(true);

                mExpandArrow.startAnimation(rotate);
                isArrowExpanded = false;
            }
        }

    }



    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.forumpost_settings_menu, popup.getMenu());
        if (getUid().equals(currentPost.uid)){
            popup.getMenu().findItem(R.id.delete).setVisible(true);
            popup.getMenu().findItem(R.id.edit).setVisible(true);

        }
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                FireBasePostsHelper.getInstance().deleteGearPost(currentPost);
                return true;
            case R.id.edit:
                itemView.setOnClickListener(null);
                setExpanded(false);
                setEditable(true);
                mSaveEditBtn.setVisibility(View.VISIBLE);
            default:
                return false;
        }
    }

    private void setExpanded(boolean isExpanded){
        if (isExpanded){
            setArrowExpanded(true);
            mCommentsLayout.setVisibility(View.VISIBLE);
            showCommentForm(true);
        }
        else {
            setArrowExpanded(false);
            mCommentsLayout.setVisibility(View.GONE);
            showCommentForm(false);
        }

    }

    private void setEditable(boolean isEditable){
        if (isEditable) {
            desciption.setFocusableInTouchMode(true);
            desciption.setClickable(true);
            desciption.requestFocus();

            priceView.setFocusableInTouchMode(true);
            priceView.setClickable(true);
        }
        else {
            desciption.setFocusable(false);
            desciption.setClickable(false);

            priceView.setFocusableInTouchMode(false);
            priceView.setClickable(false);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_gear_post:
                isExpanded = !isExpanded;
                setExpanded(isExpanded);

                break;

            case R.id.save_edit_btn:
                setEditable(false);
                mSaveEditBtn.setVisibility(View.GONE);
                FireBasePostsHelper.getInstance().updateGearPost(currentPost, desciption.getText().toString().trim(), priceView.getText().toString().trim());
                itemView.setOnClickListener(this);

                break;
        }
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



