package com.shaym.leash.ui.forum;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shaym.leash.R;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.ui.forum.fragments.ForumFragment;
import com.shaym.leash.ui.forum.fragments.GeneralFragment;
import com.shaym.leash.ui.forum.fragments.SpotsFragment;
import com.shaym.leash.ui.forum.fragments.TripsFragment;
import com.shaym.leash.ui.home.SectionPagerAdapter;
import com.shaym.leash.ui.utils.BottomNavigationViewHelper;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

import static com.shaym.leash.logic.CONSTANT.ALL_POSTS;
import static com.shaym.leash.logic.CONSTANT.GENERAL_POSTS;
import static com.shaym.leash.logic.CONSTANT.POST_COMMENTS;
import static com.shaym.leash.logic.CONSTANT.SPOTS_POSTS;
import static com.shaym.leash.logic.CONSTANT.TRIPS_POSTS;
import static com.shaym.leash.logic.CONSTANT.USERS_TABLE;
import static com.shaym.leash.logic.CONSTANT.USER_POSTS;
import static com.shaym.leash.logic.CONSTANT.USER_POSTS_AMOUNT;

/**
 * Created by shaym on 2/14/18.
 */

public class ForumActivity extends AppCompatActivity {
    private static final String TAG = "ForumActivity";
    private static final int ACTIVITY_NUM = 2;
    private BottomNavigationViewHelper mBottomNavHelper;
    private SectionPagerAdapter mSectionPagerAdapter;
    private ViewPager mVpager;
    private DatabaseReference mDatabase;
    private DatabaseReference mUserPostsRef;

    public static FloatingActionButton mFab;
    public static boolean GeneralPostOpened = false;
    public static boolean SpotsPostOpened = false;
    public static boolean TripsPostOpened = false;
    private ValueEventListener mUserPostsEventListener;

    @Override
    protected void onPause() {
        super.onPause();
        if (mUserPostsRef != null && mUserPostsEventListener != null) {
            mUserPostsRef.removeEventListener(mUserPostsEventListener);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        mBottomNavHelper = new BottomNavigationViewHelper(this, ACTIVITY_NUM);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setupFab();
        setupViewPager();

    }

    private void setupFab() {
        mFab = findViewById(R.id.fab_new_post);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mVpager.getCurrentItem()) {
                    case 0:
                        showPostDialog(GENERAL_POSTS);
                        break;
                    case 1:
                        showPostDialog(SPOTS_POSTS);
                        break;
                    case 2:
                        showPostDialog(TRIPS_POSTS);
                        break;
                }

            }
        });
    }

    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    private void setupViewPager() {
        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        mSectionPagerAdapter.AddFragment(new GeneralFragment());
        mSectionPagerAdapter.AddFragment(new SpotsFragment());
        mSectionPagerAdapter.AddFragment(new TripsFragment());

        mVpager = findViewById(R.id.container_forum);

        mVpager.setAdapter(mSectionPagerAdapter);
        mVpager.setOffscreenPageLimit(2);


        TabLayout tabLayout = findViewById(R.id.tabsforum);
        tabLayout.setupWithViewPager(mVpager);
        tabLayout.getTabAt(0).setText("General");
        tabLayout.getTabAt(1).setText("Spots");
        tabLayout.getTabAt(2).setText("Trips");

        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        Log.e(TAG, "selected page = " + position);
                        break;

                    default:

                        Log.e(TAG, "selected page = " + position);
                        break;
                }
                fabLogic(position);

            }
        };
        mVpager.addOnPageChangeListener(pageChangeListener);
        pageChangeListener.onPageSelected(0);
    }

    private void fabLogic(int pos) {
        switch (pos) {
            case 0:
                if (GeneralPostOpened)
                    mFab.setVisibility(View.INVISIBLE);
                else
                    mFab.setVisibility(View.VISIBLE);
                break;
            case 1:
                if (SpotsPostOpened)
                    mFab.setVisibility(View.INVISIBLE);
                else
                    mFab.setVisibility(View.VISIBLE);
                break;
            case 2:
                if (TripsPostOpened)
                    mFab.setVisibility(View.INVISIBLE);
                else
                    mFab.setVisibility(View.VISIBLE);
                break;
        }

    }

    // [START write_fan_out]
    private void writeNewPost(final String userId, String username, String title, String body, String forum) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String forumkey = mDatabase.child(forum).push().getKey();

        Post post = new Post(userId, forum, username, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + ALL_POSTS + "/" + forumkey, postValues);
        childUpdates.put("/" + USER_POSTS + "/" + userId + "/" + forumkey, postValues);
        childUpdates.put("/" + forum + "/" + forumkey, postValues);


        mDatabase.updateChildren(childUpdates);
    }

    private void showPostDialog(final String forum) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForumActivity.this);
        //you should edit this to fit your needs
        builder.setTitle(R.string.NewPostLabel);

        final EditText title = new EditText(ForumActivity.this);
        title.setHint(R.string.PostTitleLabel);//optional
        final EditText body = new EditText(ForumActivity.this);
        body.setHint(R.string.PostBodyLabel);//optional

        //in my example i use TYPE_CLASS_NUMBER for input only numbers
        title.setInputType(InputType.TYPE_CLASS_TEXT);
        body.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        body.setMinLines(5);

        LinearLayout lay = new LinearLayout(ForumActivity.this);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.addView(title);
        lay.addView(body);
        builder.setView(lay);

        // Set up the buttons
        builder.setPositiveButton(R.string.SubmitPostLabel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });


        builder.setNegativeButton(R.string.CancelPostLabel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the two inputs
                if (title.getText().length() == 0 || body.getText().length() == 0) {
                    Toast.makeText(ForumActivity.this, "All fields are required.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    final String userId = getUid();
                    mDatabase.child(USERS_TABLE).child(userId).addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Get user value
                                    Profile user = dataSnapshot.getValue(Profile.class);
                                    // [START_EXCLUDE]
                                    if (user == null) {
                                        // User is null, error out
                                        Log.e(TAG, "User " + userId + " is unexpectedly null");
                                        Toast.makeText(ForumActivity.this,
                                                "Error: could not fetch user.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Write new post
                                        writeNewPost(userId, user.getDisplayname(), title.getText().toString(), body.getText().toString(), forum);
                                        dialog.dismiss();
                                    }

                                    // Finish this Activity, back to the stream
//                                        setEditingEnabled(true);
                                    // [END_EXCLUDE]

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                    // [START_EXCLUDE]
//                                        setEditingEnabled(true);
                                    // [END_EXCLUDE]
                                }
                            });
                    // [END single_value_read]
                }

            }
        });


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switch (mVpager.getCurrentItem()) {
            case 0:
                GeneralPostOpened = false;
                break;
            case 1:
                SpotsPostOpened = false;
                break;
            case 2:
                TripsPostOpened = false;
                break;
        }
        mFab.setVisibility(View.VISIBLE);
    }

}







