package com.shaym.leash.ui.home.profile;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.shaym.leash.R;
import com.shaym.leash.logic.gear.GearPost;
import com.shaym.leash.logic.utils.FireBasePostsHelper;
import com.shaym.leash.ui.home.HomeActivity;

import java.util.Objects;


public class ProfileGearPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView dateView;
    private TextView locationView;
    private ImageView gearThumb;
    private ProgressBar progressBar;
    public ImageView starView;
    private TextView priceView;
    private TextView bodyView;
    private GearPost mCurrentPost;

    public ProfileGearPostViewHolder(View itemView) {
        super(itemView);
        progressBar = itemView.findViewById(R.id.gear_thumb_progress);
        gearThumb = itemView.findViewById(R.id.gear_thumb);
        locationView = itemView.findViewById(R.id.gear_post_location);
        starView = itemView.findViewById(R.id.star);
        itemView.findViewById(R.id.gear_contact_layout).setVisibility(View.GONE);
        bodyView = itemView.findViewById(R.id.gear_post_body);
        dateView = itemView.findViewById(R.id.gear_post_date);
        priceView = itemView.findViewById(R.id.price_amount);
        itemView.setOnClickListener(this);
    }


    public void bindToPost(GearPost post, Fragment fragment) {
        mCurrentPost = post;

        int dayspast = FireBasePostsHelper.getInstance().getDaysDifference(mCurrentPost.date);

        if (dayspast != 0) {
            dateView.setText("לפני " + dayspast + " ימים");
        }
        else {
            dateView.setText("היום");

        }

        locationView.setText(mCurrentPost.location);
        priceView.setText(Integer.toString(mCurrentPost.price));

        bodyView.setText(mCurrentPost.body);

        if (mCurrentPost.images != null) {
            FireBasePostsHelper.getInstance().attachPic(mCurrentPost.images.get(0), gearThumb, progressBar, 200, 200);
        }
        else {
            progressBar.setVisibility(View.GONE);
        }

    }








    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }


    @Override
    public void onClick(View v) {
        HomeActivity.mSelectedPostID = mCurrentPost.key;
        HomeActivity.mTablayout.selectTab(HomeActivity.mTablayout.getTabAt(3));
    }
}