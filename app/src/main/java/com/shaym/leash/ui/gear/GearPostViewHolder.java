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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shaym.leash.R;
import com.shaym.leash.models.Comment;
import com.shaym.leash.viewmodels.ForumViewModel;
import com.shaym.leash.models.GearPost;
import com.shaym.leash.models.Profile;
import com.shaym.leash.data.utils.CONSTANT;
import com.shaym.leash.data.utils.FireBasePostsHelper;
import com.shaym.leash.data.utils.FireBaseUsersHelper;
import com.shaym.leash.ui.forum.CommentsAdapter;
import com.shaym.leash.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.shaym.leash.data.utils.CONSTANT.ROLE_USER;
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

    private ProgressBar progressBar;
    private ProgressBar authorProgressBar;

    private ImageView authorPic;
    private GearPost currentPost;
    private Fragment mFragment;
    private Profile postProfile;
    private boolean isExpanded;
    private CommentsAdapter mAdapter;
    private EditText mCommentField;
    private TextView mCommentButton;
    private TextView mCommentsAmount;
    private ImageView mExpandArrow;
    private RecyclerView mCommentsRecycler;
    private ImageView postCommentImage;
    private ImageView purchaseGear;

    private ProgressBar postCommentImageProgressBar;
    private List<Profile> mAllUsers;
    private RelativeLayout mCommentForm;
    private List<Comment> mCommentList = new ArrayList<>();
    private RelativeLayout mCommentsLayout;
    private boolean isCommentExpanded;
    private boolean isArrowExpanded;
    private Profile mUser;
    private Button mSaveEditBtn;
    private RelativeLayout mContainer;

    GearPostViewHolder(View itemView) {
        super(itemView);

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
        gearPic.setOnClickListener(this);
        progressBar = itemView.findViewById(R.id.gear_thumb_progress);
        authorName = itemView.findViewById(R.id.gear_post_author);
        authorPic = itemView.findViewById(R.id.gear_post_author_photo);
        authorProgressBar = itemView.findViewById(R.id.gear_post_author_photo_progressbar);
        settingsPic = itemView.findViewById(R.id.settings_icon);
        mSaveEditBtn = itemView.findViewById(R.id.save_edit_btn);
        mSaveEditBtn.setOnClickListener(this);
        purchaseGear = itemView.findViewById(R.id.purchase_gear);
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
        mAdapter = new CommentsAdapter(currentPost, mFragment);
        mAdapter.setAllUsers(mAllUsers);
        mCommentsRecycler.setAdapter(mAdapter);
    }

    private void postComment() {
        // Create new comment object
        String commentText = mCommentField.getText().toString();

        FireBasePostsHelper.getInstance().writeNewGearComment(mUser, postProfile,  currentPost, commentText );

        // Clear the field
        mCommentField.setText(null);
        mCommentsRecycler.postDelayed(() -> mCommentsRecycler.scrollToPosition(Objects.requireNonNull(mCommentsRecycler.getAdapter()).getItemCount() - 1), 500);
    }

    @SuppressLint("SetTextI18n")
    void bindToPost(GearPost post, Fragment fragment, Profile postprofile, Profile user,  List<Profile> allUsers, RelativeLayout container) {
        mAllUsers = allUsers;
        mUser = user;
        postProfile = postprofile;
        mFragment = fragment;
        currentPost = post;
        mContainer = container;
        setCommentsAdapter();

        if (!postprofile.getRole().equals(ROLE_USER)){
            purchaseGear.setVisibility(View.VISIBLE);

            purchaseGear.setOnClickListener(v -> {
                try {
                    FragmentManager fm = Objects.requireNonNull(fragment.getActivity()).getSupportFragmentManager();
                    NewGearPurchaseForm newGearPurchaseForm = NewGearPurchaseForm.newInstance();
                    newGearPurchaseForm.show(fm, newGearPurchaseForm.getTag());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        settingsPic.setOnClickListener(this::showPopup);

        priceView.setText(Integer.toString(post.price));
        desciption.setText(post.body);

        gearpostlocation.setText(post.salelocation);

        int dayspast = FireBasePostsHelper.getInstance().getDaysDifference(post.date);
        if (dayspast != 0) {
            gearpostdate.setText("לפני " + dayspast + " ימים");
        }
        else {
            gearpostdate.setText("היום");

        }

        if (post.images != null && post.images.size() > 0) {
            UIHelper.getInstance().attachPic(post.images.get(0), gearPic, progressBar, 200, 200);
        }
        else {
            progressBar.setVisibility(View.GONE);
        }


        if (postProfile != null && mUser != null) {
            UIHelper.getInstance().attachRoundPic(postProfile.getAvatarurl(), authorPic, authorProgressBar, 100, 100);
            UIHelper.getInstance().attachRoundPic(mUser.getAvatarurl(), postCommentImage, postCommentImageProgressBar, 100, 100);
            authorPic.setOnClickListener(v -> FireBaseUsersHelper.getInstance().showProfilePopup(postProfile, mFragment));

            authorName.setText(postProfile.getDisplayname());
        }

        ForumViewModel mForumViewModel = ViewModelProviders.of(fragment).get(ForumViewModel.class);
        DatabaseReference mCommentsReference = FirebaseDatabase.getInstance().getReference().child(CONSTANT.POST_COMMENTS).child(post.category).child(post.key);
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

    void showCommentForm(boolean state){
        if (!state) {
            if (isCommentExpanded) {
                mCommentForm.setVisibility(View.GONE);
                setArrowExpanded(false);
                isCommentExpanded = false;
            }
        } else {
            if ((mCommentList.size() == 0 || isExpanded) && !isCommentExpanded) {
                mCommentForm.setVisibility(View.VISIBLE);
                setArrowExpanded(true);
                isCommentExpanded = true;
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



    private void showPopup(View v) {
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
                if(!postProfile.getRole().equals(ROLE_USER)){
                    try {
                        FireBasePostsHelper.getInstance().deleteNewGearPost(currentPost);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                else {
                    try {
                        FireBasePostsHelper.getInstance().deleteUsedGearPost(currentPost);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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

            case R.id.gear_thumb:
                UIHelper.getInstance().zoomImageFromThumb(mContainer, mContainer.findViewById(R.id.expanded_image), gearPic, currentPost.images.get(0));
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



