package com.shaym.leash.ui.home.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shaym.leash.R;

import java.util.Objects;

/**
 * Created by shaym on 2/17/18.
 */

public class VisualizeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_visualize, container, false);
        ImageView mEyepic = v.findViewById(R.id.eyeImage);
        mEyepic.setOnClickListener(view -> {
            Intent launchIntent = Objects.requireNonNull(getActivity()).getPackageManager().getLaunchIntentForPackage("com.leash.ar");
            if (launchIntent != null) {
                startActivity(launchIntent);//null pointer check in case package name was not found
            }
        });
        return v;
    }

}
