package com.shaym.leash.ui.home.cameras;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.shaym.leash.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shaym on 2/17/18.
 */
public class CamerasFragment extends Fragment {

    private CamerasAdapter mAdapter;
    private List<Camera> mCamerasList;
    private RecyclerView mRecyclerView;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_cameras, container, false);

        initCollapsingToolbar();

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);

        mCamerasList = new ArrayList<>();
        mAdapter = new CamerasAdapter(mView.getContext(), mCamerasList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mView.getContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        prepareCameras();

        try {
            Glide.with(this).load(R.drawable.wave_banner).into((ImageView) mView.findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mView;
    }

    private void prepareCameras() {

        Camera a = new Camera(getString(R.string.hiltonA_cam_title), getString(R.string.telaviv_location), getString(R.string.HiltonA), getString(R.string.player_stream_kind));
        mCamerasList.add(a);

        Camera b = new Camera(getString(R.string.ceasearea_camtitle), getString(R.string.shfeld_location), getString(R.string.Caesarea), getString(R.string.web_stream_kind));
        mCamerasList.add(b);

        Camera c = new Camera(getString(R.string.hiltonB_cam_title), getString(R.string.telaviv_location), getString(R.string.HiltonB), getString(R.string.player_stream_kind));
        mCamerasList.add(c);

        Camera d = new Camera(getString(R.string.marina_cam_title), getString(R.string.telaviv_location), getString(R.string.Marina_url), getString(R.string.web_stream_kind));
        mCamerasList.add(d);


//
//        a = new Camera("Mezizim", "Telaviv", covers[2]);
//        mCamerasList.add(a);
//
//        a = new Camera("Hazorfim", "Ashdod", covers[3]);
//        mCamerasList.add(a);
//
//        a = new Camera("Hakshatot", "Ashdod", covers[4]);
//        mCamerasList.add(a);
//
//        a = new Camera("BatGalim", "Haifa", covers[5]);
//        mCamerasList.add(a);
//
//        a = new Camera("Hagolshim", "Batyam", covers[6]);
//        mCamerasList.add(a);
//
//        a = new Camera("Sugar Ray", "Telaviv", covers[7]);
//        mCamerasList.add(a);
//
//        a = new Camera("Sugar Ray", "Telaviv", covers[8]);
//        mCamerasList.add(a);
//
//        a = new Camera("Sugar Ray", "Telaviv", covers[9]);
//        mCamerasList.add(a);

        mAdapter.notifyDataSetChanged();
    }


    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) mView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) mView.findViewById(R.id.appbar);
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

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}

