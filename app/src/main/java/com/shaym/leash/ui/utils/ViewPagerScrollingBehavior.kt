package com.shaym.leash.ui.utils;

import android.app.Activity
import android.content.Context
import android.graphics.Point

import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout

/**
 * Custom extension of AppBarLayout.ScrollingViewBehavior to deal with ViewPager height
 * in a bunch with Collapsing Toolbar Layout. Works dynamically when AppBar Layout height is changing.
 */
class ViewPagerScrollingBehavior(context: Context, attributeSet: AttributeSet? = null) :
        AppBarLayout.ScrollingViewBehavior(context, attributeSet) {

        override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        val layoutParams = child.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.height = child.height - (dependency.bottom - child.top)
        child.layoutParams = layoutParams
        child.requestLayout()
        return super.onDependentViewChanged(parent, child, dependency)
        }

        }

/**
 * Custom implementation of ViewTreeObserver.OnGlobalLayoutListener to fix the View height
 * in a bunch with Collapsing Toolbar Layout. Works when View is drawn on the screen for first time.
 * To be used with ViewPagerScrollingBehavior.
 */
class FixHeightGlobalLayoutListener(val activity: Activity, val view: View) : ViewTreeObserver.OnGlobalLayoutListener {

        override fun onGlobalLayout() {
        val display = activity.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val height = size.y

        val location = IntArray(2)
        view.getLocationOnScreen(location)

        view.post {
        val layoutParams = view.layoutParams as ViewGroup.LayoutParams
        layoutParams.height = height - location[1]
        view.layoutParams = layoutParams
        view.requestLayout()
        }

        view.viewTreeObserver.removeOnGlobalLayoutListener(this)
        }

        }