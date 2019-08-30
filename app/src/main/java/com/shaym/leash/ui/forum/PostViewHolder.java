package com.shaym.leash.ui.forum;

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
import com.shaym.leash.R;
import com.shaym.leash.logic.forum.Comment;
import com.shaym.leash.logic.forum.ForumViewModel;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.CONSTANT;
import com.shaym.leash.logic.utils.FireBasePostsHelper;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;

import java.util.ArrayList;
import java.util.List;

public class PostViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener, View.OnClickListener, TextWatcher {

    private static final String TAG = "PostViewHolder";
    private TextView authorView;
    private TextView publishdate;
    private ImageView authorPic;
    private ProgressBar authorPicProgressBar;
    public ImageView starView;
    private ImageView settingsView;
    private RelativeLayout attachLayout;
    private ImageView postImage;
    private ProgressBar postImageProgressBar;
    private TextView numStarsView;
    private EditText bodyView;
    private Profile mPostProfile;
    private Post currentPost;
    private Fragment mFragment;
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

    public PostViewHolder(View itemView) {
        super(itemView);
        authorPicProgressBar = itemView.findViewById(R.id.post_author_photo_progressbar);
        authorPic = itemView.findViewById(R.id.post_author_photo);
        attachLayout = itemView.findViewById(R.id.attach_layout);
        postImage = itemView.findViewById(R.id.post_attached_image);
        postImageProgressBar = itemView.findViewById(R.id.post_attached_progressbar);
        publishdate = itemView.findViewById(R.id.publish_date);
        authorView = itemView.findViewById(R.id.post_author);
        starView = itemView.findViewById(R.id.star);
        settingsView = itemView.findViewById(R.id.settings);
        numStarsView = itemView.findViewById(R.id.post_num_stars);
        bodyView = itemView.findViewById(R.id.post_body);
        bodyView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()){
                    mSaveEditBtn.setEnabled(true);
                }
                else {
                    mSaveEditBtn.setEnabled(false);

                }

            }
        });
        mSaveEditBtn = itemView.findViewById(R.id.save_edit_btn);
        mSaveEditBtn.setOnClickListener(this);
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


    public void bindToPost(Post post, Fragment fragment,Profile user, List<Profile> allUsers) {
        mAllUsers = allUsers;
        mPostProfile = FireBaseUsersHelper.getInstance().findProfile(post.uid, allUsers);
        mFragment = fragment;
        mUser = user;
        currentPost = post;

        settingsView.setOnClickListener(this::showPopup);

        setCommentsAdapter();

        int dayspast = FireBasePostsHelper.getInstance().getDaysDifference(post.date);

        if (dayspast != 0) {
            publishdate.setText("לפני " + dayspast + " ימים");
        }
        else {
            publishdate.setText("היום");

        }

        numStarsView.setText(String.valueOf(post.starCount));
        bodyView.setText(post.body);

        if (post.images != null){
            attachLayout.setVisibility(View.VISIBLE);
            FireBasePostsHelper.getInstance().attachPic(post.images.get(0), postImage, postImageProgressBar, 200, 200);
        }

        if (mPostProfile != null && mUser != null) {
            FireBasePostsHelper.getInstance().attachRoundPic(mPostProfile.getAvatarurl(), authorPic, authorPicProgressBar, 100, 100);
            FireBasePostsHelper.getInstance().attachRoundPic(mUser.getAvatarurl(), postCommentImage, postCommentImageProgressBar, 100, 100);

            authorPic.setOnClickListener(v -> FireBasePostsHelper.getInstance().showProfilePopup(mUser, mPostProfile, mFragment));
            authorView.setText(mPostProfile.getDisplayname());
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



    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.forumpost_settings_menu, popup.getMenu());
        if (mUser.getUid().equals(currentPost.uid)){
            popup.getMenu().findItem(R.id.delete).setVisible(true);
            popup.getMenu().findItem(R.id.edit).setVisible(true);

        }
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                FireBasePostsHelper.getInstance().deleteForumPost(currentPost);
                return true;
            case R.id.edit:
                itemView.setOnClickListener(null);
                setExpanded(false);
                setEditable(true);
                mSaveEditBtn.setVisibility(View.VISIBLE);
                return true;
            default:
                return false;
        }
    }


    private void setEditable(boolean isEditable){
        if (isEditable) {
            bodyView.setFocusableInTouchMode(true);
            bodyView.setClickable(true);
            bodyView.requestFocus();
        }
        else {
            bodyView.setFocusable(false);
            bodyView.setClickable(false);
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
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_post:
                isExpanded = !isExpanded;
                setExpanded(isExpanded);

                break;

            case R.id.save_edit_btn:
                setEditable(false);
                mSaveEditBtn.setVisibility(View.GONE);
                FireBasePostsHelper.getInstance().updateForumPost(currentPost, bodyView.getText().toString().trim());
                itemView.setOnClickListener(this);

                break;
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

    private void postComment() {
        // Create new comment object
        String commentText = mCommentField.getText().toString();

        // Push the comment, it will appear in the list
        FireBasePostsHelper.getInstance().writeNewComment(mUser, mPostProfile, currentPost, commentText );

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