package com.shaym.leash.ui.home.fragments;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shaym.leash.R;
import com.skyfishjy.library.RippleBackground;

/**
 * Created by shaym on 2/17/18.
 */

public class VisualizeFragment extends Fragment {

    private ImageView mEyepic;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_visualize, container, false);
        final RippleBackground rippleBackground = v.findViewById(R.id.eyeripple);
        rippleBackground.startRippleAnimation();
        mEyepic = v.findViewById(R.id.eyeImage);
        mEyepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.leash.ar");
                if (launchIntent != null) {
                    startActivity(launchIntent);//null pointer check in case package name was not found
                }
            }
        });
        return v;
    }

}
