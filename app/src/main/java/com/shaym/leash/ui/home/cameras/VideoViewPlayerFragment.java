package com.shaym.leash.ui.home.cameras;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.shaym.leash.R;
import com.shaym.leash.logic.cameras.CameraObject;
import com.shaym.leash.logic.utils.FireBasePostsHelper;
import com.shaym.leash.ui.utils.UIHelper;

import tcking.github.com.giraffeplayer2.GiraffePlayer;
import tcking.github.com.giraffeplayer2.PlayerListener;
import tcking.github.com.giraffeplayer2.VideoView;
import tv.danmaku.ijk.media.player.IjkTimedText;

import static com.shaym.leash.ui.home.cameras.CamerasFragment.CAMERA_PARCE;

public class VideoViewPlayerFragment extends Fragment implements PlayerListener, View.OnClickListener {
    private static final String TAG = "VideoViewPlayerFragment";

    private VideoView mVideoView;
    private CameraObject mCurrentCameraObject;
    private ImageView mSponserLogo;
    private ProgressBar mSponserLogoPbar;
    private onStreamLoadedListener mStreamListener;

    public VideoViewPlayerFragment (){

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_videoviewplayer, container, false);

        mVideoView = v.findViewById(R.id.player_video_view);
        mVideoView.setPlayerListener(this);
        mSponserLogo = v.findViewById(R.id.sponsor_logo);
        mSponserLogo.setOnClickListener(this);
        mSponserLogoPbar = v.findViewById(R.id.sponsor_logo_pbar);
        mVideoView.getVideoInfo().setPortraitWhenFullScreen(false);

        // 1. Get the object in onCreate();
        if (getArguments() != null) {
            mCurrentCameraObject = getArguments().getParcelable(CAMERA_PARCE);
            updateCamera(mCurrentCameraObject);
        }


        try {
            mStreamListener = (onStreamLoadedListener ) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException( "Parent Fragment"
                    + " must implement MyInterface ");
        }

        return v;
    }


    void updateCamera(CameraObject cam){
        mCurrentCameraObject = cam;
        mVideoView.setVideoPath(mCurrentCameraObject.getUrl());
        mVideoView.getPlayer().start();
        UIHelper.getInstance().attachPic(mCurrentCameraObject.mSponsorPicRef, mSponserLogo, mSponserLogoPbar, 70, 70);
    }

    @Override
    public void onPrepared(GiraffePlayer giraffePlayer) {
        Log.d(TAG, "onPrepared: ");
    }

    @Override
    public void onBufferingUpdate(GiraffePlayer giraffePlayer, int percent) {
    }

    @Override
    public boolean onInfo(GiraffePlayer giraffePlayer, int what, int extra) {
        return false;
    }

    @Override
    public void onCompletion(GiraffePlayer giraffePlayer) {

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
            mStreamListener.onStreamLoaded(true);

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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sponsor_logo){



        }
    }
}
