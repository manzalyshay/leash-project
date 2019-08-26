package com.shaym.leash.ui.home.cameras;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shaym.leash.R;
import com.shaym.leash.logic.cameras.CameraObject;
import com.shaym.leash.logic.utils.FireBasePostsHelper;
import com.shaym.leash.ui.home.HomeActivity;

import tcking.github.com.giraffeplayer2.GiraffePlayer;
import tcking.github.com.giraffeplayer2.PlayerListener;
import tcking.github.com.giraffeplayer2.VideoView;
import tv.danmaku.ijk.media.player.IjkTimedText;

import static com.shaym.leash.ui.home.cameras.CamerasFragment.CAMERA_PARCE;

public class VideoViewPlayerFragment extends Fragment implements PlayerListener {
    private static final String TAG = "VideoViewPlayerFragment";
    private TextView mTitleView;
    private TextView mLocationView;

    private VideoView mVideoView;
    private ImageView mCoverView;
    private CameraObject mCurrentCameraObject;
    private ImageView mSponserLogo;
    private ProgressBar mSponserLogoPbar;
    private boolean firstPlay;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_videoviewplayer, container, false);
        mTitleView = v.findViewById(R.id.title_view);
        mLocationView = v.findViewById(R.id.location_view);

        mVideoView = v.findViewById(R.id.player_video_view);
        mVideoView.setPlayerListener(this);
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

    @Override
    public void onPrepared(GiraffePlayer giraffePlayer) {
        Log.d(TAG, "onPrepared: ");
    }

    @Override
    public void onBufferingUpdate(GiraffePlayer giraffePlayer, int percent) {
        Log.d(TAG, "onBufferingUpdate: ");
    }

    @Override
    public boolean onInfo(GiraffePlayer giraffePlayer, int what, int extra) {
        return false;
    }

    @Override
    public void onCompletion(GiraffePlayer giraffePlayer) {
        Log.d(TAG, "onCompletion: ");

    }

    @Override
    public void onSeekComplete(GiraffePlayer giraffePlayer) {

    }

    @Override
    public boolean onError(GiraffePlayer giraffePlayer, int what, int extra) {
        return false;
    }

    @Override
    public void onPause(GiraffePlayer giraffePlayer) {

    }

    @Override
    public void onRelease(GiraffePlayer giraffePlayer) {

    }

    @Override
    public void onStart(GiraffePlayer giraffePlayer) {
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onTargetStateChange(int oldState, int newState) {

    }

    @Override
    public void onCurrentStateChange(int oldState, int newState) {
        Log.d(TAG, "onCurrentStateChange: ");
        if (newState == 3){
            firstPlay = true;
            HomeActivity activity = (HomeActivity) getActivity();
            activity.dismissProgressBar();

        }
    }

    @Override
    public void onDisplayModelChange(int oldModel, int newModel) {

    }

    @Override
    public void onPreparing(GiraffePlayer giraffePlayer) {

    }

    @Override
    public void onTimedText(GiraffePlayer giraffePlayer, IjkTimedText text) {

    }

    @Override
    public void onLazyLoadProgress(GiraffePlayer giraffePlayer, int progress) {

    }

    @Override
    public void onLazyLoadError(GiraffePlayer giraffePlayer, String message) {

    }
}
