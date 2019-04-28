package com.shaym.leash.ui.home.cameras;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shaym.leash.R;

import tcking.github.com.giraffeplayer2.VideoView;

import static com.shaym.leash.ui.home.cameras.CamerasFragment.CAMERA_PARCE;

public class VideoViewPlayerFragment extends Fragment {
    private TextView mTitleView;
    private TextView mSponsorView;

    private VideoView mVideoView;
    private ImageView mCoverView;
    private Camera mCurrentCamera;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_videoviewplayer, container, false);
        mTitleView = v.findViewById(R.id.title_view);
        mSponsorView = v.findViewById(R.id.sponsor_view);

        mVideoView = v.findViewById(R.id.player_video_view);
        mCoverView = v.findViewById(R.id.camera_cover);

        // 1. Get the object in onCreate();
        if (getArguments() != null) {
            mCurrentCamera = getArguments().getParcelable(CAMERA_PARCE);
            updateCamera(mCurrentCamera);
        }

        return v;
    }

    public VideoView getVideoView() {
        return mVideoView;
    }

    public void updateCamera(Camera cam){
        mCurrentCamera = cam;
        mVideoView.setVideoPath(mCurrentCamera.getUrl());
        mVideoView.getPlayer().start();
        mTitleView.setText(mCurrentCamera.getBeachName());
        mSponsorView.setText(mCurrentCamera.getLocation());
    }
}
