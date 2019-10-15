package com.shaym.leash.ui.home.profile;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.shaym.leash.R;
import com.shaym.leash.models.Post;
import com.shaym.leash.data.utils.FireBasePostsHelper;
import com.shaym.leash.ui.home.HomeActivity;
import com.shaym.leash.ui.utils.UIHelper;

import java.util.Objects;


public class ProfileForumPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView dateView;
    private ImageView postImage;
    private ProgressBar progressBar;
    private TextView bodyView;
    private Post mCurrentPost;

    ProfileForumPostViewHolder(View itemView) {
        super(itemView);
        dateView = itemView.findViewById(R.id.publish_date);
        bodyView = itemView.findViewById(R.id.post_body);
        postImage = itemView.findViewById(R.id.post_attached_image);
        progressBar = itemView.findViewById(R.id.post_attached_progressbar);
        itemView.setOnClickListener(this);
    }


    void bindToPost(Post post) {
        mCurrentPost = post;
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

            UIHelper.getInstance().attachPic(post.images.get(0), postImage, progressBar, 200, 200);
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
        HomeActivity.mSelectedPostID = mCurrentPost.key;
        HomeActivity.mTablayout.selectTab(HomeActivity.mTablayout.getTabAt(2));
    }
}