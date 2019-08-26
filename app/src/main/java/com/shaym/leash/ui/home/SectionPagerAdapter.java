package com.shaym.leash.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shaym on 2/17/18.
 */

public class SectionPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "SectionPagerAdapter";
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public SectionPagerAdapter (FragmentManager fm){
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem: ");
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void AddFragment(Fragment fr){
        Log.d(TAG, "AddFragment: ");
        mFragmentList.add(fr);

    }

    public void RemoveFragment(int pos){
        mFragmentList.remove(pos);

    }





}
