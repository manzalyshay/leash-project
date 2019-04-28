package com.shaym.leash.ui.home.cameras;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shaym.leash.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.PLAYER_STREAM;
import static com.shaym.leash.logic.utils.CONSTANT.WEB_STREAM;

/**
 * Created by shaym on 2/17/18.
 */
public class CamerasFragment extends Fragment implements onCameraSelectedListener, View.OnClickListener {

    private CamerasAdapter mAdapter;
    private List<Camera> mIsraelCamerasList = new ArrayList<>();
    private List<Camera> mCaliforniaCamerasList = new ArrayList<>();

    private static final String TAG = "CamerasFragment";
    public static final String CAMERA_PARCE = "CAMERA_PARCE";
    private boolean mIsraelSelected = true;
    private TextView mIsraelCamerasButton;
    private TextView mCaliforniaCamerasButton;
    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
       return inflater.inflate(R.layout.fragment_cameras, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");

        mRecyclerView = Objects.requireNonNull(getView()).findViewById(R.id.cameras_list);
        mAdapter = new CamerasAdapter(this, mIsraelCamerasList);
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(mAdapter);
        prepareCameras();

        mIsraelCamerasButton = getView().findViewById(R.id.cameras_israel_label);
        mIsraelCamerasButton.setOnClickListener(this);
        mCaliforniaCamerasButton = getView().findViewById(R.id.cameras_california_label);
        mCaliforniaCamerasButton.setOnClickListener(this);

    }

    private void setVideoViewPlayerFragment(Camera camera) {
        VideoViewPlayerFragment childFragment = new VideoViewPlayerFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(CAMERA_PARCE, camera);
        childFragment.setArguments(bundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.player_fragment_container, childFragment).commit();
    }

    private void setWebViewPlayerFragment(Camera camera) {
        WebViewPlayerFragment childFragment = new WebViewPlayerFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(CAMERA_PARCE, camera);
        childFragment.setArguments(bundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.player_fragment_container, childFragment).commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");

        setVideoViewPlayerFragment(mIsraelCamerasList.get(0));
    }

    private void prepareCameras() {
        mIsraelCamerasList.clear();
        mCaliforniaCamerasList.clear();

        Camera c = new Camera(getString(R.string.hiltonB_cam_title), getString(R.string.telaviv_location), getString(R.string.HiltonB), PLAYER_STREAM, R.drawable.hilton_cover);
        mIsraelCamerasList.add(c);

        Camera b = new Camera(getString(R.string.ceasearea_camtitle), getString(R.string.shfeld_location), getString(R.string.Caesarea), WEB_STREAM, R.drawable.netanya_cover);
        mIsraelCamerasList.add(b);

        Camera a = new Camera(getString(R.string.hiltonA_cam_title), getString(R.string.telaviv_location), getString(R.string.HiltonA), PLAYER_STREAM, R.drawable.hilton_cover);
        mIsraelCamerasList.add(a);

        Camera f = new Camera(getString(R.string.gordon), getString(R.string.telaviv_location), getString(R.string.Gordon), PLAYER_STREAM, R.drawable.hilton_cover);
        mIsraelCamerasList.add(f);

        Camera g = new Camera(getString(R.string.marina_cam_title), getString(R.string.herzelia_location), getString(R.string.MarinaHerzelia), WEB_STREAM, R.drawable.herzelia_cover);
        mIsraelCamerasList.add(g);


        Camera t = new Camera(getString(R.string.santa_monica_title), getString(R.string.cameras_title_california), getString(R.string.Santa_Monica), WEB_STREAM, R.drawable.herzelia_cover);
        mCaliforniaCamerasList.add(t);

        Camera y = new Camera(getString(R.string.Huntington_Beach_title), getString(R.string.cameras_title_california), getString(R.string.Huntington_Beach), WEB_STREAM, R.drawable.herzelia_cover);
        mCaliforniaCamerasList.add(y);

        Camera u = new Camera(getString(R.string.Redondo_Beach_title), getString(R.string.cameras_title_california), getString(R.string.Redondo_Beach), WEB_STREAM, R.drawable.herzelia_cover);
        mCaliforniaCamerasList.add(u);

        Camera i = new Camera(getString(R.string.Farallon_Islands_title), getString(R.string.cameras_title_california), getString(R.string.Farallon_Islands), WEB_STREAM, R.drawable.herzelia_cover);
        mCaliforniaCamerasList.add(i);


        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCameraSelected(Camera cam) {
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
            case R.id.cameras_israel_label:
                if (!mIsraelSelected){
                    setIsraelCameras();
                }
                break;

            case R.id.cameras_california_label:
                if (mIsraelSelected){
                    setCaliforniaCameras();

                }
                break;
        }
    }

    private void setCaliforniaCameras() {
        mIsraelSelected = false;
        mCaliforniaCamerasButton.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.underline_cameras));
        mIsraelCamerasButton.setBackgroundResource(R.color.transparent);

        mAdapter = new CamerasAdapter(this, mCaliforniaCamerasList);
        mRecyclerView.swapAdapter(mAdapter, true);
    }

    private void setIsraelCameras() {
        mIsraelSelected = true;
        mIsraelCamerasButton.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.underline_cameras));
        mCaliforniaCamerasButton.setBackgroundResource(R.color.transparent);
        mAdapter = new CamerasAdapter(this, mIsraelCamerasList);
        mRecyclerView.swapAdapter(mAdapter, true);
    }
}



