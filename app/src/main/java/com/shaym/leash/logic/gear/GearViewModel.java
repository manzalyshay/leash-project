package com.shaym.leash.logic.gear;

import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shaym.leash.logic.utils.CONSTANT;
import com.shaym.leash.logic.utils.FirebaseQueryLiveData;

import static com.shaym.leash.logic.utils.CONSTANT.BOARDS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.CLOTHING_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.FINS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.LEASHES_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.OTHER_POSTS;

    public class GearViewModel extends ViewModel {

        private static final DatabaseReference BOARDS_POSTS_REF =
                FirebaseDatabase.getInstance().getReference().child(GEAR_POSTS).child(CONSTANT.USED_GEAR_POSTS).child(BOARDS_POSTS);
        private static final DatabaseReference LEASH_POSTS_REF =
                FirebaseDatabase.getInstance().getReference().child(GEAR_POSTS).child(CONSTANT.USED_GEAR_POSTS).child(LEASHES_POSTS);
        private static final DatabaseReference FINS_POSTS_REF =
                FirebaseDatabase.getInstance().getReference().child(GEAR_POSTS).child(CONSTANT.USED_GEAR_POSTS).child(FINS_POSTS);
        private static final DatabaseReference CLOTHING_POSTS_REF =
                FirebaseDatabase.getInstance().getReference().child(GEAR_POSTS).child(CONSTANT.USED_GEAR_POSTS).child(CLOTHING_POSTS);
        private static final DatabaseReference OTHER_POSTS_REF =
                FirebaseDatabase.getInstance().getReference().child(GEAR_POSTS).child(CONSTANT.USED_GEAR_POSTS).child(OTHER_POSTS);

        private final FirebaseQueryLiveData boardPostsLiveData = new FirebaseQueryLiveData(BOARDS_POSTS_REF);
        private final FirebaseQueryLiveData leashPostsLiveData = new FirebaseQueryLiveData(LEASH_POSTS_REF);
        private final FirebaseQueryLiveData finsPostsLiveData = new FirebaseQueryLiveData(FINS_POSTS_REF);
        private final FirebaseQueryLiveData clothingPostsLiveData = new FirebaseQueryLiveData(CLOTHING_POSTS_REF);

        public FirebaseQueryLiveData getBoardPostsLiveData() {
            return boardPostsLiveData;
        }

        public FirebaseQueryLiveData getLeashPostsLiveData() {
            return leashPostsLiveData;
        }

        public FirebaseQueryLiveData getFinsPostsLiveData() {
            return finsPostsLiveData;
        }

        public FirebaseQueryLiveData getClothingPostsLiveData() {
            return clothingPostsLiveData;
        }

        public FirebaseQueryLiveData getOtherPostsLiveData() {
            return otherPostsLiveData;
        }

        private final FirebaseQueryLiveData otherPostsLiveData = new FirebaseQueryLiveData(OTHER_POSTS_REF);


    }
