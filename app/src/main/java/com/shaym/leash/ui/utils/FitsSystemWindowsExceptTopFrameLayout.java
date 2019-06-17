package com.shaym.leash.ui.utils;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;

/**
 * Implements an effect similar to {@code android:fitsSystemWindows="true"} on Lollipop or higher,
 * except ignoring the top system window inset. {@code android:fitsSystemWindows="true"} does not
 * and should not be set on this layout.
 */
public class FitsSystemWindowsExceptTopFrameLayout extends FrameLayout {

    public FitsSystemWindowsExceptTopFrameLayout(Context context) {
        super(context);
    }

    public FitsSystemWindowsExceptTopFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FitsSystemWindowsExceptTopFrameLayout(Context context, AttributeSet attrs,
                                                 int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public FitsSystemWindowsExceptTopFrameLayout(Context context, AttributeSet attrs,
                                                 int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setPadding(insets.getSystemWindowInsetLeft(), 0, insets.getSystemWindowInsetRight(),
                    insets.getSystemWindowInsetBottom());
            return insets.replaceSystemWindowInsets(0, insets.getSystemWindowInsetTop(), 0, 0);
        } else {
            return super.onApplyWindowInsets(insets);
        }
    }
}