package com.shaym.leash.ui.gear;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shaym.leash.R;
import com.shaym.leash.ui.gear.fragments.GearContent;

/**
 * Created by shaym on 2/14/18.
 */

public class GearFragment extends Fragment {
    private static final String TAG = "GearContent";
    @SuppressLint("StaticFieldLeak")
    public static FloatingActionButton mGearFab;

    public final static int NEWGEAR_FRAGMENT_ITEM_ID = 0401;
    public final static int USEDGEAR_FRAGMENT_ITEM_ID = 0402;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gear, container, false);
        return v;
    }

    private void initUI()  {

        setupFab();
    }



    @Override
    public void onStart() {
        super.onStart();


        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        GearContent fragment = new GearContent();
        fragmentTransaction.add(R.id.container_gearactivity, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
            initUI();


    }



    private void setupFab() {
        mGearFab = getView().findViewById(R.id.fab_new_post_gear);
        mGearFab.setOnClickListener(v -> {


            Intent i = new Intent(getContext(), NewGearPostActivity.class);
            startActivity(i);
        });
    }







}






