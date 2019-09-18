package com.shaym.leash.ui.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.MainApplication;
import com.shaym.leash.R;
import com.shaym.leash.logic.aroundme.CircleTransform;
import com.shaym.leash.logic.aroundme.RoundedCornersTransform;
import com.shaym.leash.ui.home.aroundme.AroundMeFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class UIHelper {
    private static final String TAG = "UIHelper";
    public final static String CAMERAS_SELECTED = "CAMERAS_SELECTED";
    public final static String PROFILE_SELECTED = "PROFILE_SELECTED";
    public final static String AROUNDME_SELECTED = "AROUNDME_SELECTED";
    public final static String CHAT_SELECTED = "CHAT_SELECTED";
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public final static String FORECAST_SELECTED = "FORECAST_SELECTED";
    public final static String FORUM_SELECTED = "FORUM_SELECTED";
    public final static String GEAR_SELECTED = "GEAR_SELECTED";
    private Animator currentAnimator;

    private int shortAnimationDuration;
    private static UIHelper instance = new UIHelper();

    private UIHelper() {
        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = MainApplication.getInstace().getApplicationContext().getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }

    public static UIHelper getInstance(){
        return instance;
    }

    public void updateToolBar(String currentState, Activity context, AppBarLayout mAppBar, ImageView mHeaderView){

        switch (currentState) {
            case CAMERAS_SELECTED:
                mAppBar.setExpanded(true);
                context.findViewById(R.id.location_icon_toolbar).setVisibility(View.VISIBLE);
                context.findViewById(R.id.profile_layout_toolbar).setVisibility(View.VISIBLE);
                context.findViewById(R.id.back_icon_toolbar).setVisibility(View.GONE);
                context.findViewById(R.id.back_icon_toolbar).setClickable(false);

                context.findViewById(R.id.profilefragment_profilepic_layout).setVisibility(View.GONE);

                break;

            case FORECAST_SELECTED:
                mAppBar.setExpanded(false);
                context.findViewById(R.id.profilefragment_profilepic_layout).setVisibility(View.GONE);

                break;

            case FORUM_SELECTED:
                mAppBar.setExpanded(false);
                context.findViewById(R.id.profilefragment_profilepic_layout).setVisibility(View.GONE);

                break;

            case GEAR_SELECTED:
                mAppBar.setExpanded(false);
                context.findViewById(R.id.profilefragment_profilepic_layout).setVisibility(View.GONE);

                break;

            case PROFILE_SELECTED:
                mAppBar.setExpanded(true);
                context.findViewById(R.id.location_icon_toolbar).setVisibility(View.GONE);
                context.findViewById(R.id.profile_layout_toolbar).setVisibility(View.GONE);
                context.findViewById(R.id.back_icon_toolbar).setVisibility(View.VISIBLE);
                context.findViewById(R.id.back_icon_toolbar).setClickable(true);

                context.findViewById(R.id.profilefragment_profilepic_layout).setVisibility(View.VISIBLE);

                break;

            case AROUNDME_SELECTED:
                mAppBar.setExpanded(false);
                context.findViewById(R.id.location_icon_toolbar).setVisibility(View.GONE);
                context.findViewById(R.id.profile_layout_toolbar).setVisibility(View.GONE);
                context.findViewById(R.id.back_icon_toolbar).setVisibility(View.VISIBLE);
                context.findViewById(R.id.back_icon_toolbar).setClickable(true);
                context.findViewById(R.id.profilefragment_profilepic_layout).setVisibility(View.GONE);

                break;

            case CHAT_SELECTED:
                mAppBar.setExpanded(false);
                context.findViewById(R.id.location_icon_toolbar).setVisibility(View.GONE);
                context.findViewById(R.id.profile_layout_toolbar).setVisibility(View.GONE);
                context.findViewById(R.id.back_icon_toolbar).setVisibility(View.VISIBLE);
                context.findViewById(R.id.back_icon_toolbar).setClickable(true);
                context.findViewById(R.id.profilefragment_profilepic_layout).setVisibility(View.GONE);

                break;


        }
    }


    public void addTab(TabLayout tabLayout, String title, boolean isSelected) {
        tabLayout.addTab(tabLayout.newTab().setText(title), isSelected);
    }

    public void attachRoundPic(String url, ImageView imageView, ProgressBar progressBar, int height, int width) {
        if (!url.isEmpty()) {
            if (url.charAt(0) == 'h') {
                Picasso.get().load(Uri.parse(url)).resize(width, height).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new CircleTransform()).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(Uri.parse(url)).resize(width, height).centerCrop().transform(new CircleTransform()).into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                //Try again online if cache failed
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);

                            }
                        });

                    }

                });

            } else {
                storageReference.child(url).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).resize(width, height).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new CircleTransform()).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(uri).resize(width, height).centerCrop().transform(new CircleTransform()).into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                //Try again online if cache failed

                                progressBar.setVisibility(View.GONE);

                            }
                        });

                    }

                }));
            }
        } else {
            progressBar.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.launcher_leash).resize(width,height).centerCrop().transform(new CircleTransform()).into(imageView);
        }
    }


    public void attachRoundPicToPoiTarget(String url, final AroundMeFragment.PoiTarget imageView, int height, int width) {
        if (!url.isEmpty()) {
            if (url.charAt(0) == 'h') {
                Picasso.get().load(Uri.parse(url)).resize(width, height).centerCrop().transform(new CircleTransform()).into(imageView);


            } else {
                storageReference.child(url).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).resize(width, height).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new CircleTransform()).into(imageView));


            }

        }
        else {
            Picasso.get().load(R.drawable.launcher_leash).resize(width, height).centerCrop().transform(new CircleTransform()).into(imageView);

        }
    }

    public void attachPicToPoiTarget(String url, final AroundMeFragment.PoiTarget imageView, int height, int width) {
        if (!url.isEmpty()) {
            if (url.charAt(0) == 'h') {
                Picasso.get().load(Uri.parse(url)).resize(width, height).transform(new RoundedCornersTransform()).into(imageView);


            } else {
                storageReference.child(url).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).resize(width, height).networkPolicy(NetworkPolicy.OFFLINE).transform(new RoundedCornersTransform()).into(imageView));


            }

        }
        else {
            Picasso.get().load(R.drawable.launcher_leash).resize(width, height).transform(new RoundedCornersTransform()).into(imageView);

        }
    }

    public void attachPic(String url, ImageView imageView, ProgressBar progressBar, int width, int height) {
        if (!url.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            if (url.charAt(0) == 'h') {
                Picasso.get().load(Uri.parse(url)).resize(width, height).networkPolicy(NetworkPolicy.OFFLINE).transform(new RoundedCornersTransform()).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();

                        Picasso.get().load(Uri.parse(url)).resize(width, height).transform(new RoundedCornersTransform()).into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                //Try again online if cache failed
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);

                            }
                        });

                    }

                });
            } else {
                storageReference.child(url).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).resize(width, height).networkPolicy(NetworkPolicy.OFFLINE).transform(new RoundedCornersTransform()).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                        Picasso.get().load(uri).resize(width, height).transform(new RoundedCornersTransform()).into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                //Try again online if cache failed
                                e.printStackTrace();

                                progressBar.setVisibility(View.GONE);

                            }
                        });

                    }

                }));


            }
        } else {
            progressBar.setVisibility(View.GONE);

        }
    }

    public void zoomImageFromThumb(final RelativeLayout container, final ImageView expandedImageView, final View thumbView, String url) {
        container.findViewById(R.id.image_enlarge_progressbar).setVisibility(View.VISIBLE);
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        if (!url.isEmpty()) {
            if (url.charAt(0) == 'h') {
                Picasso.get().load(Uri.parse(url))
                        .into(expandedImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                continueZoomAnimation(expandedImageView, thumbView, container);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
            } else {
                storageReference.child(url).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri)
                        .into(expandedImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                continueZoomAnimation(expandedImageView, thumbView, container);

                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        }));


            }
        }

    }


    private void continueZoomAnimation(ImageView expandedImageView, View thumbView, RelativeLayout container){
        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        container
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                container.findViewById(R.id.image_enlarge_progressbar).setVisibility(View.GONE);
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(view -> {
            if (currentAnimator != null) {
                currentAnimator.cancel();
            }

            // Animate the four positioning/sizing properties in parallel,
            // back to their original values.
            AnimatorSet set1 = new AnimatorSet();
            set1.play(ObjectAnimator
                    .ofFloat(expandedImageView, View.X, startBounds.left))
                    .with(ObjectAnimator
                            .ofFloat(expandedImageView,
                                    View.Y,startBounds.top))
                    .with(ObjectAnimator
                            .ofFloat(expandedImageView,
                                    View.SCALE_X, startScaleFinal))
                    .with(ObjectAnimator
                            .ofFloat(expandedImageView,
                                    View.SCALE_Y, startScaleFinal));
            set1.setDuration(shortAnimationDuration);
            set1.setInterpolator(new DecelerateInterpolator());
            set1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    currentAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    currentAnimator = null;
                }
            });
            set1.start();
            currentAnimator = set1;
        });
    }

}
