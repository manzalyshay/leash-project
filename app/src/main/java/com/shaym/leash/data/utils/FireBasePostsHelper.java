package com.shaym.leash.data.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.widget.ThemedSpinnerAdapter;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.android.policy.TimeWindow;
import com.cloudinary.android.preprocess.BitmapEncoder;
import com.cloudinary.android.preprocess.ImagePreprocessChain;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shaym.leash.MainApplication;
import com.shaym.leash.R;
import com.shaym.leash.models.ChatMessage;
import com.shaym.leash.models.Comment;
import com.shaym.leash.models.Conversation;
import com.shaym.leash.models.GearPost;
import com.shaym.leash.models.Post;
import com.shaym.leash.models.Profile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.shaym.leash.data.utils.CONSTANT.ALL_POSTS;
import static com.shaym.leash.data.utils.CONSTANT.CHAT_CONVERSATIONS;
import static com.shaym.leash.data.utils.CONSTANT.CONVERSATIONS;
import static com.shaym.leash.data.utils.CONSTANT.FORUM_POSTS;
import static com.shaym.leash.data.utils.CONSTANT.GEAR_POSTS;
import static com.shaym.leash.data.utils.CONSTANT.NEW_GEAR_POSTS;
import static com.shaym.leash.data.utils.CONSTANT.POST_COMMENTS;
import static com.shaym.leash.data.utils.CONSTANT.USED_GEAR_POSTS;
import static com.shaym.leash.data.utils.CONSTANT.USER_CONVERSATIONS;
import static com.shaym.leash.data.utils.CONSTANT.USER_POSTS;
import static com.shaym.leash.ui.home.HomeActivity.FROM_UID_KEY;

public class FireBasePostsHelper {

    private static final String TAG = "FireBaseUsersHelper";


    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private RequestQueue mRequestQueue;
    private PlacesClient mPlacesClient;
    private static FireBasePostsHelper instance = new FireBasePostsHelper();

    private FireBasePostsHelper() {
        mRequestQueue = Volley.newRequestQueue(MainApplication.getInstance().getApplicationContext());
        mPlacesClient = Places.createClient(MainApplication.getInstance().getApplicationContext());

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

    public void writeNewUsedGearPost(Profile profile, String category, String location, int price, String phonenumber, String description, List<String> mgearpicrefs) {
        String typekey = mDatabase.child(category).push().getKey();
        GearpostFactory gearpostFactory = new GearpostFactory(profile,typekey, category, location, price, phonenumber, description,mgearpicrefs, false);

        getCurrentPlace(gearpostFactory);
    }


    public void writeNewForumPost(final Profile profile, String body, String category, List<String> images) {
        String forumkey = mDatabase.child(category).push().getKey();

        ForumpostFactory forumpostFactory = new ForumpostFactory(profile.getUid() ,forumkey, category, body,new Date(), images, profile.getCurrentlocation(), "");
        getCurrentPlace(forumpostFactory);
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


    public void deleteForumPost(Post post) throws Exception {
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

    public void deleteUsedGearPost(GearPost post) throws Exception {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        mDatabase.getRef().child(GEAR_POSTS).child(USED_GEAR_POSTS).child(post.category).child(post.key).setValue(null);
        mDatabase.getRef().child(GEAR_POSTS).child(USED_GEAR_POSTS).child(ALL_POSTS).child(post.key).setValue(null);
        mDatabase.getRef().child(GEAR_POSTS).child(USED_GEAR_POSTS).child(USER_POSTS).child(post.uid).child(post.key).setValue(null);

        deleteImagesFromStorage(post.images);
        deleteGearPostComments(post);
    }

    public void deleteNewGearPost(GearPost post) throws Exception {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        mDatabase.getRef().child(GEAR_POSTS).child(NEW_GEAR_POSTS).child(post.category).child(post.key).setValue(null);
        mDatabase.getRef().child(GEAR_POSTS).child(NEW_GEAR_POSTS).child(ALL_POSTS).child(post.key).setValue(null);
        mDatabase.getRef().child(GEAR_POSTS).child(NEW_GEAR_POSTS).child(USER_POSTS).child(post.uid).child(post.key).setValue(null);

        deleteImagesFromStorage(post.images);
        deleteGearPostComments(post);
    }

    private void deleteGearPostComments(GearPost post) {
        mDatabase.getRef().child(POST_COMMENTS).child(post.category).child(post.key).setValue(null);

    }


    public void deleteImagesFromStorage(List<String> images) throws Exception {

        if (images != null) {
            for (int i = 0; i < images.size(); i++) {


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
        if(selectedBitmap != null)
        {
            WeakReference<Context> contextWeakReference = new WeakReference<>(context);

            final ProgressDialog progressDialog = new ProgressDialog(contextWeakReference.get());
            progressDialog.setTitle(context.getString(R.string.uploading));
            progressDialog.show();
        byte[] data = FireBasePostsHelper.getInstance().convertBitmapToByteArray(selectedBitmap);
        String uploadid = UUID.randomUUID().toString();
        String requestId = MediaManager.get().upload(data).option("folder", storageAddress).option("public_id", uploadid ).preprocess(
                ImagePreprocessChain.limitDimensionsChain(2000, 2000)
                        .saveWith(new BitmapEncoder(BitmapEncoder.Format.WEBP, 80))).unsigned("leash_upload")
                .constrain(TimeWindow.getDefault())
                .option("resource_type", "auto")
                .maxFileSize(100 * 1024 * 1024). // max 100mb
        callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                // your code here
            }
            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                // example code starts here
                double progress = (double) bytes/totalBytes;
                // post progress to app UI (e.g. progress bar, notification)
                // example code ends here
                progressDialog.setMessage("Uploaded "+(int)progress+"%");

            }
            @Override
            public void onSuccess(String requestId, Map resultData) {
                Toast.makeText(contextWeakReference.get(), "Uploaded", Toast.LENGTH_SHORT).show();
                listener.onPictureUploaded(resultData.get("public_id").toString());
                progressDialog.dismiss();
            }
            @Override
            public void onError(String requestId, ErrorInfo error) {
                // your code here
            }
            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                // your code here
            }})
                .dispatch(contextWeakReference.get());



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
        ChatMessage message = new ChatMessage(commentProfile.getUid(),null, commentProfile.getDisplayname(), MainApplication.getInstance().getApplicationContext().getString(R.string.new_comment_on_post), new Date(), false);

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
        ChatMessage message = new ChatMessage(commentProfile.getUid(),null, commentProfile.getDisplayname(), MainApplication.getInstance().getApplicationContext().getString(R.string.new_comment_on_post), new Date(), false);

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

    public void writeNewGearPost(Profile profile, String category, String location, int price, String phonenumber, String description, List<String> mgearpicrefs) {
        String typekey = mDatabase.child(category).push().getKey();
        GearpostFactory gearpostFactory = new GearpostFactory(profile,typekey, category, location, price, phonenumber, description,mgearpicrefs, true);

        getCurrentPlace(gearpostFactory);
    }

    public void getCurrentPlace(placeFound listener){
// Use fields to define the data types to return.
        List<Place.Field> placeFields = Collections.singletonList(Place.Field.NAME);

// Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.newInstance(placeFields);

// Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(MainApplication.getInstance().getApplicationContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = mPlacesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    FindCurrentPlaceResponse response = task.getResult();
                    assert response != null;
                    PlaceLikelihood placeLikelihood = response.getPlaceLikelihoods().get(0);

                    listener.placeFound(placeLikelihood.getPlace().getName());

//                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
//                        Log.i(TAG, String.format("Place '%s' has likelihood: %f",
//                                placeLikelihood.getPlace().getName(),
//                                placeLikelihood.getLikelihood()));
//                    }
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }

                    listener.placeFound(null);

                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            getLocationPermission();
            listener.placeFound(null);

        }

    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: No location Permission for places");
    }

    public class ForumpostFactory implements placeFound {
        Post post;

        public ForumpostFactory(String uid, String forumkey, String category, String body, Date date, List<String> images, HashMap<String, Double> currentlocation, String place) {
            post = new Post(uid, forumkey, category, body, date, images, currentlocation, place);
        }

        @Override
        public void placeFound(String place) {
            if (place != null){
                post.postplace = place;
            }

            Map<String, Object> postValues = post.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/" + FORUM_POSTS+ "/" +ALL_POSTS + "/" + post.key, postValues);
            childUpdates.put("/" + FORUM_POSTS+ "/" + USER_POSTS + "/" + post.uid + "/" + post.key, postValues);
            childUpdates.put("/" + FORUM_POSTS+ "/" + post.category + "/" + post.key, postValues);

            mDatabase.updateChildren(childUpdates);
        }
    }


    public class GearpostFactory implements placeFound {
        GearPost gearPost;
        boolean isnew;

        public GearpostFactory(Profile profile, String key, String category, String location, int price, String phonenumber, String description, List<String> mgearpicrefs, boolean isnew) {
            gearPost = new GearPost(profile.getUid(), key,  category,  location,  price,  phonenumber,  description,  new Date(), mgearpicrefs, profile.getCurrentlocation(), "");
            this.isnew = isnew;
        }

        @Override
        public void placeFound(String place) {
            if (place != null){
                gearPost.postplace = place;
            }

            Map<String, Object> postValues = gearPost.toMap();

            if (isnew) {
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/" + GEAR_POSTS + "/" + NEW_GEAR_POSTS + "/" + ALL_POSTS + "/" + gearPost.key, postValues);
                childUpdates.put("/" + GEAR_POSTS + "/" + NEW_GEAR_POSTS + "/" + USER_POSTS + "/" + gearPost.uid + "/" + gearPost.key, postValues);
                childUpdates.put("/" + GEAR_POSTS + "/" + NEW_GEAR_POSTS + "/" + gearPost.category + "/" + gearPost.key, postValues);

                mDatabase.updateChildren(childUpdates);
            }else {


                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + ALL_POSTS + "/" + gearPost.key, postValues);
                childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + USER_POSTS + "/" + gearPost.uid + "/" + gearPost.key, postValues);
                childUpdates.put("/" + GEAR_POSTS + "/" + USED_GEAR_POSTS + "/" + gearPost.category + "/" + gearPost.key, postValues);

                mDatabase.updateChildren(childUpdates);
            }

        }
    }


}