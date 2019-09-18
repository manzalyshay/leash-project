package com.shaym.leash.ui.home.cameras;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
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

import static com.shaym.leash.logic.utils.CONSTANT.PLAYER_STREAM;

/**
 * Created by shaym on 2/17/18.
 */
public class CamerasFragment extends Fragment implements onCameraSelectedListener,
        CamerasListener, TabLayout.OnTabSelectedListener, onStreamLoadedListener {

    private CamerasAdapter mAdapter;
    private List<List<CameraObject>> mCamerasByCity = new ArrayList<>();

    private static final String TAG = "CamerasFragment";
    static final String CAMERA_PARCE = "CAMERA_PARCE";
    private TabLayout mTabLayout;
    private ProgressBar mCamerasProgressbar;

    public CamerasFragment (){ }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_cameras, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
        initUI();
        initCamerasViewModel();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        new DownloadCameras().execute();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    private void initUI() {
        RecyclerView mRecyclerView = Objects.requireNonNull(getView()).findViewById(R.id.cameras_list);
        mAdapter = new CamerasAdapter(this);
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(mAdapter);
        mTabLayout = getView().findViewById(R.id.cameras_menu);
        mTabLayout.addOnTabSelectedListener(this);
        setVideoViewPlayerFragment(null);
        mCamerasProgressbar = getView().findViewById(R.id.cameras_progressbar);
    }

    private void initCamerasViewModel() {
        CamerasViewModel mCamerasViewModel = ViewModelProviders.of(this).get(CamerasViewModel.class);
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
                assert f != null;
                ((WebViewPlayerFragment) f).updateCamera(cam);

            }

        }
    }






    @Override
    public void onCamerasLoaded(List<List<CameraObject>> camerasbycity) {
        mTabLayout.removeAllTabs();

        if (camerasbycity.get(0).size() > 0){
            UIHelper.getInstance().addTab(mTabLayout,getString(R.string.telaviv_location), false);
            mCamerasByCity.add(camerasbycity.get(0));
        }

        if (camerasbycity.get(1).size() > 0){
            UIHelper.getInstance().addTab(mTabLayout,getString(R.string.ashdod_location), false);
            mCamerasByCity.add(camerasbycity.get(1));

        }

        if (camerasbycity.get(2).size() > 0){
            UIHelper.getInstance().addTab(mTabLayout,getString(R.string.ceasearea_camtitle), false);
            mCamerasByCity.add(camerasbycity.get(2));

        }

        if (camerasbycity.get(3).size() > 0){
            UIHelper.getInstance().addTab(mTabLayout,getString(R.string.herzelia_location), false);
            mCamerasByCity.add(camerasbycity.get(3));

        }

        mTabLayout.selectTab(mTabLayout.getTabAt(0));



    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (mCamerasByCity.size() > 0) {
            switch (tab.getPosition()) {
                case 0:
                    mAdapter.setCamerasList(mCamerasByCity.get(0));
                    onCameraSelected(mCamerasByCity.get(0).get(0));
                    break;

                case 1:
                    mAdapter.setCamerasList(mCamerasByCity.get(1));
                    onCameraSelected(mCamerasByCity.get(1).get(0));
                    break;

                case 2:
                    mAdapter.setCamerasList(mCamerasByCity.get(2));
                    onCameraSelected(mCamerasByCity.get(2).get(0));
                    break;

                case 3:
                    mAdapter.setCamerasList(mCamerasByCity.get(3));
                    onCameraSelected(mCamerasByCity.get(3).get(0));
                    break;

            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onStreamLoaded(boolean status) {
        if (status){
            mCamerasProgressbar.setVisibility(View.GONE);
        }
    }
}



