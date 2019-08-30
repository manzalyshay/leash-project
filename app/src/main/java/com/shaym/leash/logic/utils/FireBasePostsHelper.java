package com.shaym.leash.logic.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.ThemedSpinnerAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.MainApplication;
import com.shaym.leash.R;
import com.shaym.leash.logic.aroundme.CircleTransform;
import com.shaym.leash.logic.aroundme.RoundedCornersTransform;
import com.shaym.leash.logic.chat.ChatMessage;
import com.shaym.leash.logic.chat.Conversation;
import com.shaym.leash.logic.forum.Comment;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.gear.GearPost;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.ui.home.HomeActivity;
import com.shaym.leash.ui.home.aroundme.AroundMeFragment;
import com.shaym.leash.ui.home.chat.ChatDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.shaym.leash.logic.utils.CONSTANT.ALL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.CHAT_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.FORUM_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.POST_COMMENTS;
import static com.shaym.leash.logic.utils.CONSTANT.USED_GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USER_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.USER_POSTS;
import static com.shaym.leash.ui.home.HomeActivity.FROM_UID_KEY;

public class FireBasePostsHelper {

    private static final String TAG = "FireBaseUsersHelper";


    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private RequestQueue mRequestQueue;

    private static FireBasePostsHelper instance = new FireBasePostsHelper();

    private FireBasePostsHelper() {
        mRequestQueue = Volley.newRequestQueue(MainApplication.getInstace().getApplicationContext());
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
    public void writeNewForumPost(final String userId, String body, String category, List<String> images) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String forumkey = mDatabase.child(category).push().getKey();

        Post post = new Post(userId,forumkey, category, body,new Date(), images, 0);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + FORUM_POSTS+ "/" +ALL_POSTS + "/" + forumkey, postValues);
        childUpdates.put("/" + FORUM_POSTS+ "/" + USER_POSTS + "/" + userId + "/" + forumkey, postValues);
        childUpdates.put("/" + FORUM_POSTS+ "/" + category + "/" + forumkey, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    // [START write_fan_out]
    public void addNewConversation(String key, final String initiatorID, String receiverID, Date timestarted) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously

        Conversation conversation = new Conversation(key, initiatorID, receiverID, key, timestarted);
        Map<String, Object> values = conversation.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + CHAT_CONVERSATIONS+ "/" + USER_CONVERSATIONS + "/" + initiatorID + "/" + key, values);
        childUpdates.put("/" + CHAT_CONVERSATIONS+ "/" + USER_CONVERSATIONS + "/" + receiverID + "/" + key, values);

        mDatabase.updateChildren(childUpdates);
    }


    public void deleteForumPost(Post post) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        mDatabase.getRef().child(FORUM_POSTS).child(post.category).child(post.key).setValue(null);
        mDatabase.getRef().child(FORUM_POSTS).child(ALL_POSTS).child(post.key).setValue(null);
        mDatabase.getRef().child(FORUM_POSTS).child(USER_POSTS).child(post.uid).child(post.key).setValue(null);

        deleteImagesFromStorage(post.images);

        deleteForumPostComments(post);

    }

    private void deleteForumPostComments(Post post) {
        mDatabase.getRef().child(POST_COMMENTS).child(post.category).child(post.key).setValue(null);

    }

    public void deleteGearPost(GearPost post) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        mDatabase.getRef().child(GEAR_POSTS).child(USED_GEAR_POSTS).child(post.category).child(post.key).setValue(null);
        mDatabase.getRef().child(GEAR_POSTS).child(USED_GEAR_POSTS).child(ALL_POSTS).child(post.key).setValue(null);
        mDatabase.getRef().child(GEAR_POSTS).child(USED_GEAR_POSTS).child(USER_POSTS).child(post.uid).child(post.key).setValue(null);

        deleteImagesFromStorage(post.images);
        deleteGearPostComments(post);
    }

    private void deleteGearPostComments(GearPost post) {
        mDatabase.getRef().child(POST_COMMENTS).child(post.category).child(post.key).setValue(null);

    }


    public void deleteImagesFromStorage(List<String> images){

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


    public void showProfilePopup(final Profile mUser, final Profile mClickedUser, Fragment fragment) {
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

        ImageView dmpic = mClickedUserDialog.findViewById(R.id.dm_aroundme);
        ImageView shakepic = mClickedUserDialog.findViewById(R.id.shake_user);

        if (mClickedUser.getUid().equals(getUid())){
            dmpic.setVisibility(View.GONE);
            shakepic.setVisibility(View.GONE);
        }
        else {
            dmpic.setOnClickListener(view -> {
                openChatWindow(fragment, mClickedUser.getUid());
                mClickedUserDialog.dismiss();

            });

            shakepic.setOnClickListener(v -> {
                makeMeShake(shakepic, 20, 5);
                HomeActivity activity = (HomeActivity) fragment.getActivity();
                assert activity != null;
                String chatKey = getChatKey(mUser.getUid(), mClickedUser.getUid());
                if (!activity.hasChatWith(chatKey)){
                    FireBasePostsHelper.getInstance().addNewConversation(chatKey, getUid(), mClickedUser.getUid(), new Date());
                }
                postDirectMessage(fragment.getString(R.string.shake), mUser, mClickedUser, FirebaseDatabase.getInstance().getReference()
                        .child(CHAT_CONVERSATIONS).child(CONVERSATIONS).child(chatKey));
            });
        }


        closedialogpic.setOnClickListener(view -> mClickedUserDialog.dismiss());


        Objects.requireNonNull(mClickedUserDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mClickedUserDialog.show();
    }

    private void makeMeShake(View view, int duration, int offset) {
        Animation anim = new TranslateAnimation(-offset,offset,0,0);
        anim.setDuration(duration);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(5);
        view.startAnimation(anim);
        
    }

    public void openChatWindow(Fragment fragment, String uid) {
        try {
            FragmentManager fm = Objects.requireNonNull(fragment.getActivity()).getSupportFragmentManager();
            ChatDialog chatDialog= ChatDialog.newInstance(uid);
            chatDialog.show(fm, chatDialog.getTag());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void postDirectMessage(String text, Profile user, Profile convpartner, DatabaseReference chatref) {
        // Create new message object
        if (!text.isEmpty()) {
            ChatMessage message = new ChatMessage(user.getUid(),chatref.push().getKey(), user.getDisplayname(), text, new Date(), false);

            sendPushNotification(message, convpartner);
            // Push the message, it will appear in the list
            chatref.push().setValue(message);
        }


    }


    public void sendPushNotification(ChatMessage message, Profile convPartner) {

        JSONObject mainObj = new JSONObject();

        try {

            JSONObject dataObj = new JSONObject();

            dataObj.put(FROM_UID_KEY, message.uid);
            dataObj.put("author", message.author);
            dataObj.put("body", message.text);
            mainObj.put("to", convPartner.getPushtoken());
            mainObj.put("priority", "high");

            mainObj.put("data", dataObj);


            String PUSH_URL = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PUSH_URL, mainObj, response -> Log.d(TAG, "onResponse: Success"), error -> Log.e(TAG,  error.toString()))
            {
                @Override
                public Map<String, String> getHeaders() {

                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("Authorization", "key=AIzaSyDtwfuXPakP3Z6c_uP5aG56tbXOyY6c6YQ");
                    return header;
                }
            };

            mRequestQueue.add(request);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }

    public void uploadImage(Context context, String storageAddress, Bitmap selectedBitmap, onPictureUploadedListener listener) {
        WeakReference<Context> contextWeakReference = new WeakReference<>(context);

        if(selectedBitmap != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(contextWeakReference.get());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            String uploadPath = storageAddress + "/" + getUid() + "/" + UUID.randomUUID().toString();
            StorageReference ref = storageReference.child(uploadPath);
            byte[] data = FireBasePostsHelper.getInstance().convertBitmapToByteArray(selectedBitmap);

            ref.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(contextWeakReference.get(), "Uploaded", Toast.LENGTH_SHORT).show();
                        listener.onPictureUploaded(uploadPath);

                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(contextWeakReference.get(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        listener.onUploadFailed();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    });
        }
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

    public byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = null;
        try {
            stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            return stream.toByteArray();
        }finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    Log.e(ThemedSpinnerAdapter.Helper.class.getSimpleName(), "ByteArrayOutputStream was not closed");
                }
            }
        }
    }

    public void deleteForumComment(Comment currentComment, Post post) {

        mDatabase.getRef().child(POST_COMMENTS).child(post.category).child(post.key).child(currentComment.key).removeValue();
    }

    public void deleteGearComment(Comment currentComment, GearPost post) {
        try {
            mDatabase.getRef().child(POST_COMMENTS).child(post.category).child(post.key).child(currentComment.key).removeValue();
        }
        catch (Exception e){
            Log.d(TAG, "deleteGearComment: ");
            e.printStackTrace();
        }
    }

    public void writeNewComment(Profile commentProfile,Profile postProfile, Post post, String commentext) {

        String commentkey = mDatabase.child(POST_COMMENTS).child(post.category).child(post.key).push().getKey();

        Comment comment = new Comment(commentProfile.getUid(), commentkey, new Date(), commentext);
        Map<String, Object> commentvalues = comment.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + POST_COMMENTS+ "/" +post.category + "/" + post.key + "/" + commentkey, commentvalues);


        mDatabase.updateChildren(childUpdates);

        //Send Notification to Post Owner
        ChatMessage message = new ChatMessage(commentProfile.getUid(),null, commentProfile.getDisplayname(), MainApplication.getInstace().getApplicationContext().getString(R.string.new_comment_on_post), new Date(), false);

        sendPushNotification(message, postProfile);

    }

    public void updateForumPost(Post post, String text) {
        post.body = text;
        try {
            mDatabase.getRef().child(FORUM_POSTS).child(post.category).child(post.key).setValue(post);
        }
        catch (Exception e){
            Log.d(TAG, "deleteGearComment: ");
            e.printStackTrace();
        }
    }

    public void updateGearPost(GearPost currentPost, String trim, String trim1) {
        //TODO
    }
}