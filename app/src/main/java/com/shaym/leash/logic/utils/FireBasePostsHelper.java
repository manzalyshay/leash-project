package com.shaym.leash.logic.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
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

import androidx.annotation.StringRes;
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
import com.shaym.leash.ui.home.profile.ProfileDialog;
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

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static com.shaym.leash.logic.utils.CONSTANT.ALL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.CHAT_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.FORUM_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.NEW_GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.POST_COMMENTS;
import static com.shaym.leash.logic.utils.CONSTANT.ROLE_STORE_FCS;
import static com.shaym.leash.logic.utils.CONSTANT.ROLE_STORE_INTERSURF;
import static com.shaym.leash.logic.utils.CONSTANT.ROLE_USER;
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


    public int getDaysDifference(Date fromDate)
    {
        if(fromDate==null)
            return 0;

        return (int)( (new Date().getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
    }

    public void writeNewUsedGearPost(String uid, String category, String location, int price, String phonenumber, String description, List<String> mgearpicrefs) {
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
                desertRef.delete().addOnSuccessListener(aVoid -> Log.d(TAG, "deleteForumPost: Images Success")).addOnFailureListener(exception -> {
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









    public void postDirectMessage(String text, Profile user, Profile convpartner, DatabaseReference chatref) {
        // Create new message object
        if (!text.isEmpty()) {
            String key = chatref.push().getKey();
            ChatMessage message = new ChatMessage(user.getUid(),key, user.getDisplayname(), text, new Date(), false);

            sendPushNotification(message, convpartner);
            // Push the message, it will appear in the list
            assert key != null;
            chatref.child(key).setValue(message);
        }


    }


    private void sendPushNotification(ChatMessage message, Profile convPartner) {

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


    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
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

    public void writeNewGearComment(Profile commentProfile,Profile postProfile, GearPost post, String commentext) {

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
            Log.e(TAG, "updateForumPost: ");
            e.printStackTrace();
        }
    }

    public void updateGearPost(GearPost currentPost, String trim, String trim1) {
        //TODO
    }

    public void updateChatMessage(ChatMessage message, String chatkey) {
        try {
            mDatabase.getRef().child(CHAT_CONVERSATIONS).child(CONVERSATIONS).child(chatkey).child(message.key).setValue(message);
        }
        catch (Exception e){
            Log.e(TAG, "updateChatMessage: ");
            e.printStackTrace();
        }
    }

    public void writeNewGearPost(String uid, String category, String location, int price, String phonenumber, String description, List<String> mgearpicrefs) {

        String typekey = mDatabase.child(category).push().getKey();

        GearPost post = new GearPost(uid, typekey,  category,  location,  price,  phonenumber,  description,  new Date(), mgearpicrefs);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + GEAR_POSTS + "/" + NEW_GEAR_POSTS + "/" + ALL_POSTS + "/" + typekey, postValues);
        childUpdates.put("/" + GEAR_POSTS + "/" + NEW_GEAR_POSTS + "/" + USER_POSTS + "/" + uid + "/" + typekey, postValues);
        childUpdates.put("/" + GEAR_POSTS + "/" + NEW_GEAR_POSTS + "/" + category + "/" + typekey, postValues);

        mDatabase.updateChildren(childUpdates);
    }
}