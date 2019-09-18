package com.shaym.leash.ui.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.fragment.app.DialogFragment;

import com.shaym.leash.R;

import java.util.Objects;

public class EnlargeImageDialog extends DialogFragment  {
    private static final String TAG = "EnlargeImageDialog";
    private static final String URL_KEY = "URL_KEY";

    private ImageView mImage;
    private ProgressBar mProgressBar;
    private String mURL;

    public EnlargeImageDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EnlargeImageDialog newInstance(String URL) {
        EnlargeImageDialog frag = new EnlargeImageDialog();
        Bundle args = new Bundle();
        args.putString(URL_KEY, URL);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        assert args != null;
        mURL = args.getString(URL_KEY);
        return inflater.inflate(R.layout.dialog_enlargeimage, container);
    }

    @Override
    public void onStart() {
        super.onStart();
        initViews(Objects.requireNonNull(getView()));
    }


    private void initViews(View v) {
    mImage = v.findViewById(R.id.image_enlarge);
    mProgressBar = v.findViewById(R.id.image_enlarge_progressbar);

    }

    @Override
    public void onResume() {super.onResume();

       UIHelper.getInstance().attachPic(mURL, mImage, mProgressBar, 400, 400);

    }
}