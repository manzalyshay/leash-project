package com.shaym.leash.ui.home.cameras;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shaym.leash.R;
import com.shaym.leash.logic.cameras.CameraObject;
import com.shaym.leash.logic.utils.FireBasePostsHelper;

import tcking.github.com.giraffeplayer2.VideoView;

import static com.shaym.leash.ui.home.cameras.CamerasFragment.CAMERA_PARCE;

public class VideoViewPlayerFragment extends Fragment {
    private TextView mTitleView;
    private TextView mLocationView;

    private VideoView mVideoView;
    private ImageView mCoverView;
    private CameraObject mCurrentCameraObject;
    private ImageView mSponserLogo;
    private ProgressBar mSponserLogoPbar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_videoviewplayer, container, false);
        mTitleView = v.findViewById(R.id.title_view);
        mLocationView = v.findViewById(R.id.location_view);

        mVideoView = v.findViewById(R.id.player_video_view);
        mCoverView = v.findViewById(R.id.camera_cover);
        mSponserLogo = v.findViewById(R.id.sponsor_logo);
        mSponserLogoPbar = v.findViewById(R.id.sponsor_logo_pbar);
        mVideoView.getVideoInfo().setPortraitWhenFullScreen(false);

        // 1. Get the object in onCreate();
        if (getArguments() != null) {
            mCurrentCameraObject = getArguments().getParcelable(CAMERA_PARCE);
            updateCamera(mCurrentCameraObject);
        }

        return v;
    }

    public VideoView getVideoView() {
        return mVideoView;
    }

    public void updateCamera(CameraObject cam){
        mCurrentCameraObject = cam;
        mVideoView.setVideoPath(mCurrentCameraObject.getUrl());
        mVideoView.getPlayer().start();
        mTitleView.setText(mCurrentCameraObject.getLocation());
        mLocationView.setText(mCurrentCameraObject.getCity());
        FireBasePostsHelper.getInstance().attachPic(mCurrentCameraObject.mSponsorPicRef, mSponserLogo, mSponserLogoPbar, 30, 30);
    }
}
