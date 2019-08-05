package com.shaym.leash.ui.home.cameras;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shaym.leash.R;
import com.shaym.leash.logic.cameras.CameraObject;
import com.shaym.leash.logic.cameras.CamerasListener;
import com.shaym.leash.logic.cameras.CamerasViewModel;
import com.shaym.leash.logic.cameras.DownloadCameras;
import com.shaym.leash.logic.cameras.GetCamerasByCity;
import com.shaym.leash.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.ASHDOD;
import static com.shaym.leash.logic.utils.CONSTANT.CAESAREA;
import static com.shaym.leash.logic.utils.CONSTANT.HERZELIA;
import static com.shaym.leash.logic.utils.CONSTANT.PLAYER_STREAM;
import static com.shaym.leash.logic.utils.CONSTANT.TELAVIV;
import static com.shaym.leash.logic.utils.CONSTANT.herzelia_location;

/**
 * Created by shaym on 2/17/18.
 */
public class CamerasFragment extends Fragment implements onCameraSelectedListener, View.OnClickListener, CamerasListener {

    private CamerasAdapter mAdapter;
    private List<CameraObject> mTelAvivCamerasList = new ArrayList<>();
    private List<CameraObject> mAshdodCameraList = new ArrayList<>();
    private List<CameraObject> mHerzeliaCameraList = new ArrayList<>();
    private List<CameraObject> mCaesareaCameraList = new ArrayList<>();

    private static final String TAG = "CamerasFragment";
    public static final String CAMERA_PARCE = "CAMERA_PARCE";

    private String currentTab = TELAVIV;
    private TextView mTelavivCamerasButton;
    private TextView mHerzeliaCamerasButton;
    private TextView mAshdodCamerasButton;
    private TextView mCaesareaCamerasButton;
    private RecyclerView mRecyclerView;
    private CamerasViewModel mCamerasViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cameras, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
        initCamerasViewModel();
        new DownloadCameras().execute();
    }

    private void initUI() {
        mRecyclerView = Objects.requireNonNull(getView()).findViewById(R.id.cameras_list);
        mAdapter = new CamerasAdapter(this, mTelAvivCamerasList);
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(mAdapter);

        mTelavivCamerasButton = getView().findViewById(R.id.telaviv_label);
        mTelavivCamerasButton.setOnClickListener(this);
        mHerzeliaCamerasButton = getView().findViewById(R.id.herzelia_label);
        mHerzeliaCamerasButton.setOnClickListener(this);
        mCaesareaCamerasButton = getView().findViewById(R.id.ceasara_label);
        mCaesareaCamerasButton.setOnClickListener(this);
        mAshdodCamerasButton = getView().findViewById(R.id.ashdod_label);
        mAshdodCamerasButton.setOnClickListener(this);

        setVideoViewPlayerFragment(null);
    }

    private void initCamerasViewModel() {
        mCamerasViewModel = ViewModelProviders.of(this).get(CamerasViewModel.class);
        mCamerasViewModel.getAllCameras().observe(this, cameras -> {
            // Update the cached copy of the words in the adapter.
            Log.d(TAG, "onCreateView: Observer Triggered");
            if (!cameras.isEmpty()) {
                new GetCamerasByCity(CamerasFragment.this, cameras).execute();
            }

        });
    }

    private void setVideoViewPlayerFragment(CameraObject cameraObject) {
        VideoViewPlayerFragment childFragment = new VideoViewPlayerFragment();

        if (cameraObject != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(CAMERA_PARCE, cameraObject);
            childFragment.setArguments(bundle);
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.player_fragment_container, childFragment).commit();
    }

    private void setWebViewPlayerFragment(CameraObject cameraObject) {
        WebViewPlayerFragment childFragment = new WebViewPlayerFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(CAMERA_PARCE, cameraObject);
        childFragment.setArguments(bundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.player_fragment_container, childFragment).commit();
    }



    @Override
    public void onCameraSelected(CameraObject cam) {
        Log.d(TAG, "onCameraSelected: ");
        Fragment f = getChildFragmentManager().findFragmentById(R.id.player_fragment_container);
        if (f instanceof VideoViewPlayerFragment) {
            if (cam.getStreamKind().equals(PLAYER_STREAM)) {
                // do something with f
                ((VideoViewPlayerFragment) f).updateCamera(cam);
            } else {
                setWebViewPlayerFragment(cam);
            }
        } else {
            if (cam.getStreamKind().equals(PLAYER_STREAM)) {
                setVideoViewPlayerFragment(cam);
            } else {
                ((WebViewPlayerFragment) f).updateCamera(cam);

            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.telaviv_label:
                mAdapter.setCamerasList(mTelAvivCamerasList);
                onCameraSelected(mTelAvivCamerasList.get(0));
                setActiveButton(mTelavivCamerasButton);
                currentTab = TELAVIV;
                break;

            case R.id.ashdod_label:
                mAdapter.setCamerasList(mAshdodCameraList);
                onCameraSelected(mAshdodCameraList.get(0));
                setActiveButton(mAshdodCamerasButton);
                currentTab = ASHDOD;

                break;

            case R.id.herzelia_label:
                mAdapter.setCamerasList(mHerzeliaCameraList);
                onCameraSelected(mHerzeliaCameraList.get(0));
                setActiveButton(mHerzeliaCamerasButton);
                currentTab = HERZELIA;

                break;

            case R.id.ceasara_label:
                mAdapter.setCamerasList(mCaesareaCameraList);
                onCameraSelected(mCaesareaCameraList.get(0));
                setActiveButton(mCaesareaCamerasButton);
                currentTab = CAESAREA;

                break;
        }
    }

    private void setActiveButton(TextView button) {
        mTelavivCamerasButton.setBackgroundResource(R.color.transparent);
        mCaesareaCamerasButton.setBackgroundResource(R.color.transparent);
        mAshdodCamerasButton.setBackgroundResource(R.color.transparent);
        mHerzeliaCamerasButton.setBackgroundResource(R.color.transparent);

        button.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.underline_cameras));
    }


    @Override
    public void onCamerasLoaded(List<List<CameraObject>> camerasbycity) {

        mTelAvivCamerasList = camerasbycity.get(0);
        mAshdodCameraList = camerasbycity.get(1);
        mCaesareaCameraList = camerasbycity.get(2);
        mHerzeliaCameraList = camerasbycity.get(3);

        switch (currentTab){
            case TELAVIV:
                mAdapter.setCamerasList(mTelAvivCamerasList);
                onCameraSelected(mTelAvivCamerasList.get(0));
                break;

            case ASHDOD:
                mAdapter.setCamerasList(mAshdodCameraList);

                onCameraSelected(mAshdodCameraList.get(0));
                break;
            case HERZELIA:
                mAdapter.setCamerasList(mHerzeliaCameraList);

                onCameraSelected(mHerzeliaCameraList.get(0));
                break;

            case CAESAREA:
                mAdapter.setCamerasList(mCaesareaCameraList);

                onCameraSelected(mCaesareaCameraList.get(0));
                break;

        }

    }
}



