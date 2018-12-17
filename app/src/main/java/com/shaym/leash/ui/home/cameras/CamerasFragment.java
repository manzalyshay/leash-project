package com.shaym.leash.ui.home.cameras;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shaym.leash.MainApplication;
import com.shaym.leash.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.shaym.leash.ui.home.cameras.CamerasAdapter.PLAYER_STREAM;
import static com.shaym.leash.ui.home.cameras.CamerasAdapter.WEB_STREAM;

/**
 * Created by shaym on 2/17/18.
 */
public class CamerasFragment extends Fragment {

    private CamerasAdapter mAdapter;
    private List<Camera> mCamerasList = new ArrayList<>();
    private View mView;
    private TextView mCamerasHeader;
    private static final String TAG = "CamerasFragment";


    @Nullable
    @Override
    public View onCreateView(@NonNull final  LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
            mView = inflater.inflate(R.layout.fragment_cameras, container, false);
        RecyclerView mRecyclerView = mView.findViewById(R.id.recycler_view);
        mCamerasHeader = mView.findViewById(R.id.cameras_header);

        mAdapter = new CamerasAdapter(getActivity(), mCamerasList, Objects.requireNonNull(getActivity()).getSupportFragmentManager());

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mView.getContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        LocalBroadcastManager.getInstance(MainApplication.getInstace().getApplicationContext()).registerReceiver(mMessageReceiver,
                new IntentFilter(getString(R.string.broadcast_cameras_changed)));

        int mCountrySelect = 0;

        prepareCameras(mCountrySelect);

        try {
            Picasso.get().load(R.drawable.wave_banner).into((ImageView) mView.findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }

        initCollapsingToolbar();


        return mView;
    }



    private void prepareCameras(int mCountrySelect) {

        mCamerasList.clear();

        switch (mCountrySelect) {
            case 0:
            Camera a = new Camera(getString(R.string.hiltonA_cam_title), getString(R.string.telaviv_location), getString(R.string.HiltonA), PLAYER_STREAM, "hiltona");
            mCamerasList.add(a);

            Camera b = new Camera(getString(R.string.ceasearea_camtitle), getString(R.string.shfeld_location), getString(R.string.Caesarea), WEB_STREAM, "");
            mCamerasList.add(b);

            Camera c = new Camera(getString(R.string.hiltonB_cam_title), getString(R.string.telaviv_location), getString(R.string.HiltonB), PLAYER_STREAM, "hiltonb");
            mCamerasList.add(c);

            Camera f = new Camera(getString(R.string.gordon), getString(R.string.telaviv_location), getString(R.string.Gordon), PLAYER_STREAM, "gordon");
            mCamerasList.add(f);

            Camera g = new Camera(getString(R.string.marina_cam_title), getString(R.string.herzelia_location), getString(R.string.MarinaHerzelia), WEB_STREAM, "");
            mCamerasList.add(g);

            mCamerasHeader.setText(getString(R.string.cameras_title_israel));
            break;

            case 1:

                Camera t = new Camera(getString(R.string.santa_monica_title), getString(R.string.cameras_title_california), getString(R.string.Santa_Monica), WEB_STREAM, "");
                mCamerasList.add(t);

                Camera y = new Camera(getString(R.string.Huntington_Beach_title), getString(R.string.cameras_title_california), getString(R.string.Huntington_Beach), WEB_STREAM, "");
                mCamerasList.add(y);

                Camera u = new Camera(getString(R.string.Redondo_Beach_title), getString(R.string.cameras_title_california), getString(R.string.Redondo_Beach), WEB_STREAM, "");
                mCamerasList.add(u);

                Camera i = new Camera(getString(R.string.Farallon_Islands_title), getString(R.string.cameras_title_california), getString(R.string.Farallon_Islands), WEB_STREAM, "");
                mCamerasList.add(i);

                mCamerasHeader.setText(getString(R.string.cameras_title_california));

                break;

        }

        mAdapter.notifyDataSetChanged();
    }


    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                mView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = mView.findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private int dpToPx() {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics()));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Log.d(TAG +"receiver", "Got message: ");
            int country = intent.getIntExtra("val", -1);
            prepareCameras(country);

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(MainApplication.getInstace().getApplicationContext()).unregisterReceiver(mMessageReceiver);

    }
}

