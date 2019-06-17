package com.shaym.leash.logic.utils;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.aroundme.CircleTransform;
import com.shaym.leash.logic.aroundme.RoundedCornersTransform;
import com.shaym.leash.logic.chat.Conversation;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.gear.GearPost;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.ui.home.chat.ChatActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.ALL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.CHAT_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.FORUM_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USED_GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USER_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.USER_POSTS;

public class FireBasePostsHelper {

    private static final String TAG = "FireBaseUsersHelper";


    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private static FireBasePostsHelper instance = new FireBasePostsHelper();

    private FireBasePostsHelper() {
    }

    public static FireBasePostsHelper getInstance(){
        return instance;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }

    public int getDaysDifference(Date fromDate)
    {
        if(fromDate==null)
            return 0;

        return (int)( (new Date().getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
    }

    public void writeNewGearPost(String uid, String category, String location, int price, String phonenumber, String description, List<String> mgearpicrefs) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String typekey = mDatabase.child(category).push().getKey();

        GearPost post = new GearPost(uid, typekey,  category,  location,  price,  phonenumber,  description,  new Date(), mgearpicrefs);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + ALL_POSTS + "/" + typekey, postValues);
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + USER_POSTS + "/" + uid + "/" + typekey, postValues);
        childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + category + "/" + typekey, postValues);

        mDatabase.updateChildren(childUpdates);
    }


    // [START write_fan_out]
    public void writeNewForumPost(final String userId, String body, String forum, List<String> images) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String forumkey = mDatabase.child(forum).push().getKey();

        Post post = new Post(userId,forumkey, forum, body,new Date(), images, 0);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + FORUM_POSTS+ "/" +ALL_POSTS + "/" + forumkey, postValues);
        childUpdates.put("/" + FORUM_POSTS+ "/" + USER_POSTS + "/" + userId + "/" + forumkey, postValues);
        childUpdates.put("/" + FORUM_POSTS+ "/" + forum + "/" + forumkey, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    // [START write_fan_out]
    public void addNewConversation(String key, final String initiatorID, String receiverID, String conversationID, Date timestarted) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously

        Conversation conversation = new Conversation(key, initiatorID, receiverID, conversationID, timestarted);
        Map<String, Object> values = conversation.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + CHAT_CONVERSATIONS+ "/" + USER_CONVERSATIONS + "/" + initiatorID + "/" + conversationID, values);
        childUpdates.put("/" + CHAT_CONVERSATIONS+ "/" + USER_CONVERSATIONS + "/" + receiverID + "/" + conversationID, values);

        mDatabase.updateChildren(childUpdates);
    }


    public void deleteForumPost(Post post) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        mDatabase.getRef().child(FORUM_POSTS).child(post.forum).child(post.key).setValue(null);
        mDatabase.getRef().child(FORUM_POSTS).child(ALL_POSTS).child(post.key).setValue(null);
        mDatabase.getRef().child(FORUM_POSTS).child(USER_POSTS).child(post.uid).child(post.key).setValue(null);

        deletePostImages(post.images);

    }

    public void deleteGearPost(GearPost post) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        mDatabase.getRef().child(GEAR_POSTS).child(USED_GEAR_POSTS).child(post.category).child(post.key).setValue(null);
        mDatabase.getRef().child(GEAR_POSTS).child(USED_GEAR_POSTS).child(ALL_POSTS).child(post.key).setValue(null);
        mDatabase.getRef().child(GEAR_POSTS).child(USED_GEAR_POSTS).child(USER_POSTS).child(post.uid).child(post.key).setValue(null);

        deletePostImages(post.images);
    }

    private void deletePostImages(  List<String> images){

        if (images != null) {
            for (int i = 0; i < images.size(); i++) {
                // Create a storage reference from our app
                StorageReference storageRef = storage.getReference();

// Create a reference to the file to delete
                StorageReference desertRef = storageRef.child(images.get(i));

// Delete the file
                desertRef.delete().addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "deleteForumPost: Images Success");
                }).addOnFailureListener(exception -> {
                    Log.d(TAG, "deleteForumPost: Images Error");
                });
            }
        }
    }


    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    public DatabaseReference getDatabase() {
        return mDatabase;
    }


    public void showProfilePopup(final Profile mClickedUser, Fragment fragment) {
        WeakReference<Fragment> mContext = new WeakReference<>(fragment);
        Fragment fragment1 = mContext.get();

        Dialog mClickedUserDialog = new Dialog(Objects.requireNonNull(fragment1.getContext()));
        mClickedUserDialog.setContentView(R.layout.dialog_profile);
// ...Irrelevant code for customizing the buttons and title

        ImageView profilepic = mClickedUserDialog.findViewById(R.id.profilepicaroundme);
        ImageView closedialogpic = mClickedUserDialog.findViewById(R.id.closedialogbtn);

        ProgressBar progressBar = mClickedUserDialog.findViewById(R.id.profilepic_progressbar_aroundme);

        FireBasePostsHelper.getInstance().attachRoundPic(mClickedUser.getAvatarurl(), profilepic, progressBar, 200, 200);

        TextView displayname = mClickedUserDialog.findViewById(R.id.displaynamearoundme);
        displayname.setText(mClickedUser.getDisplayname().trim());

        ImageView mailpic = mClickedUserDialog.findViewById(R.id.mail_aroundme);
        ImageView phonepic = mClickedUserDialog.findViewById(R.id.phone_aroundme);
        ImageView dmpic = mClickedUserDialog.findViewById(R.id.dm_aroundme);

        if (mClickedUser.getUid().equals(getUid())){
            dmpic.setVisibility(View.INVISIBLE);
        }
        else{
            mailpic.setOnClickListener(view -> {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{mClickedUser.getEmail().trim()});
                try {
                    fragment1.startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(fragment1.getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            });
        }


        phonepic.setOnClickListener(view -> {
            Log.d(TAG, "showProfilePopup: Phone Clicked");

        });

        dmpic.setOnClickListener(view -> {
            Intent intent = new Intent(fragment.getActivity(), ChatActivity.class);
            Bundle b = new Bundle();
            b.putString(ChatActivity.EXTRA_PARTNER_KEY, mClickedUser.getUid());
            intent.putExtras(b); //Put your id to your next Intent
            Objects.requireNonNull(fragment.getActivity()).startActivity(intent);
            mClickedUserDialog.dismiss();

        });


        closedialogpic.setOnClickListener(view -> mClickedUserDialog.dismiss());


        Objects.requireNonNull(mClickedUserDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mClickedUserDialog.show();
    }

    public String getChatKey(String fromUID, String mToUid){
        String key;
        int result = fromUID.compareTo(mToUid);

        if (result > 0){
            key = fromUID+mToUid;
        }
        else {
            key = mToUid+fromUID;
        }

        return key;
    }

    public void attachRoundPic(String url, ImageView imageView, ProgressBar progressBar, int height, int width) {
        if (!url.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
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

        }
    }


    public void attachPic(String url, ImageView imageView, ProgressBar progressBar, int width, int height) {
        if (!url.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            if (url.charAt(0) == 'h') {
                Picasso.get().load(Uri.parse(url)).resize(width, height).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new RoundedCornersTransform()).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();

                        Picasso.get().load(Uri.parse(url)).resize(width, height).centerCrop().transform(new RoundedCornersTransform()).into(imageView, new Callback() {
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
                storageReference.child(url).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).resize(width, height).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new RoundedCornersTransform()).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                        Picasso.get().load(uri).resize(width, height).centerCrop().transform(new RoundedCornersTransform()).into(imageView, new Callback() {
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


}