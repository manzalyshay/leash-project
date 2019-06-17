package com.shaym.leash.ui.utils;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * No Predictive Animations GridLayoutManager
 */
public class NpaGridLayoutManager extends LinearLayoutManager {
    public NpaGridLayoutManager(Context context) {
        super(context);
    }



    /**
     * Disable predictive animations. There is a bug in RecyclerView which causes views that
     * are being reloaded to pull invalid ViewHolders from the internal recycler stack if the
     * adapter size has decreased since the ViewHolder was recycled.
     */
    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }


}
