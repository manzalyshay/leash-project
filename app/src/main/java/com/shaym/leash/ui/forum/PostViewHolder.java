package com.shaym.leash.ui.forum;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.FireBasePostsHelper;

import java.util.Objects;

public class PostViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener, View.OnClickListener {

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
    private TextView bodyView;
    private Profile postProfile;
    private Post currentPost;
    private Fragment mFragment;
    private boolean isExpanded;

    public PostViewHolder(View itemView) {
        super(itemView);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
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
        itemView.setOnClickListener(this);
    }


    public void bindToPost(Post post, Fragment fragment, Profile postprofile) {
        mFragment = fragment;
        postProfile = postprofile;

        currentPost = post;

        settingsView.setOnClickListener(this::showPopup);


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

        if (postprofile != null) {
            FireBasePostsHelper.getInstance().attachRoundPic(postProfile.getAvatarurl(), authorPic, authorPicProgressBar, 100, 100);
            authorPic.setOnClickListener(v -> FireBasePostsHelper.getInstance().showProfilePopup(postProfile, mFragment));
            authorView.setText(postProfile.getDisplayname());
        }


    }




    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }



    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.forumpost_settings_menu, popup.getMenu());
        if (getUid().equals(currentPost.uid)){
            popup.getMenu().findItem(R.id.delete).setVisible(true);
        }
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                FireBasePostsHelper.getInstance().deleteForumPost(currentPost);
                return true;
            case R.id.share:

                return true;
            default:
                return false;
        }
    }


    @Override
    public void onClick(View v) {

    }
}