package com.shaym.leash.ui.home.profile;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.shaym.leash.logic.utils.FireBasePostsHelper;

import java.util.Objects;


public class ProfileForumPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView titleView;
    private TextView dateView;
    private ImageView postImage;
    private ProgressBar progressBar;
    public ImageView starView;
    private ImageView deleteView;
    private TextView numStarsView;
    private TextView bodyView;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private Fragment mFragment;
    private Post mCurrentPost;

    public ProfileForumPostViewHolder(View itemView) {
        super(itemView);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //titleView = itemView.findViewById(R.id.post_title);
        dateView = itemView.findViewById(R.id.publish_date);
        starView = itemView.findViewById(R.id.star);
        numStarsView = itemView.findViewById(R.id.post_num_stars);
        bodyView = itemView.findViewById(R.id.post_body);
        postImage = itemView.findViewById(R.id.post_attached_image);
        progressBar = itemView.findViewById(R.id.post_attached_progressbar);
        itemView.setOnClickListener(this);
    }


    public void bindToPost(Post post, Fragment fragment) {
        mCurrentPost = post;
        mFragment = fragment;
        numStarsView.setText(String.valueOf(post.starCount));
        bodyView.setText(post.body);

        int dayspast = FireBasePostsHelper.getInstance().getDaysDifference(post.date);

        if (dayspast != 0) {
            dateView.setText("לפני " + dayspast + " ימים");
        }
        else {
            dateView.setText("היום");

        }

        if (post.images!= null && !post.images.isEmpty()){
            itemView.findViewById(R.id.attach_layout).setVisibility(View.VISIBLE);

            FireBasePostsHelper.getInstance().attachPic(post.images.get(0), postImage, progressBar, 200, 200);
        }
        else {
            itemView.findViewById(R.id.attach_layout).setVisibility(View.GONE);

        }

    }





    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }


    @Override
    public void onClick(View v) {

    }
}