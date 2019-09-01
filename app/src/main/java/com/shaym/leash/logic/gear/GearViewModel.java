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

        private static final DatabaseReference USED_BOARDS_POSTS_REF =
                FirebaseDatabase.getInstance().getReference().child(GEAR_POSTS).child(CONSTANT.USED_GEAR_POSTS).child(BOARDS_POSTS);
        private static final DatabaseReference USED_LEASH_POSTS_REF =
                FirebaseDatabase.getInstance().getReference().child(GEAR_POSTS).child(CONSTANT.USED_GEAR_POSTS).child(LEASHES_POSTS);
        private static final DatabaseReference USED_FINS_POSTS_REF =
                FirebaseDatabase.getInstance().getReference().child(GEAR_POSTS).child(CONSTANT.USED_GEAR_POSTS).child(FINS_POSTS);
        private static final DatabaseReference USED_CLOTHING_POSTS_REF =
                FirebaseDatabase.getInstance().getReference().child(GEAR_POSTS).child(CONSTANT.USED_GEAR_POSTS).child(CLOTHING_POSTS);
        private static final DatabaseReference USED_OTHER_POSTS_REF =
                FirebaseDatabase.getInstance().getReference().child(GEAR_POSTS).child(CONSTANT.USED_GEAR_POSTS).child(OTHER_POSTS);


        private static final DatabaseReference NEW_BOARDS_POSTS_REF =
                FirebaseDatabase.getInstance().getReference().child(GEAR_POSTS).child(CONSTANT.NEW_GEAR_POSTS).child(BOARDS_POSTS);
        private static final DatabaseReference NEW_LEASH_POSTS_REF =
                FirebaseDatabase.getInstance().getReference().child(GEAR_POSTS).child(CONSTANT.NEW_GEAR_POSTS).child(LEASHES_POSTS);
        private static final DatabaseReference NEW_FINS_POSTS_REF =
                FirebaseDatabase.getInstance().getReference().child(GEAR_POSTS).child(CONSTANT.NEW_GEAR_POSTS).child(FINS_POSTS);
        private static final DatabaseReference NEW_CLOTHING_POSTS_REF =
                FirebaseDatabase.getInstance().getReference().child(GEAR_POSTS).child(CONSTANT.NEW_GEAR_POSTS).child(CLOTHING_POSTS);
        private static final DatabaseReference NEW_OTHER_POSTS_REF =
                FirebaseDatabase.getInstance().getReference().child(GEAR_POSTS).child(CONSTANT.NEW_GEAR_POSTS).child(OTHER_POSTS);


        private final FirebaseQueryLiveData usedboardPostsLiveData = new FirebaseQueryLiveData(USED_BOARDS_POSTS_REF);
        private final FirebaseQueryLiveData UsedleashPostsLiveData = new FirebaseQueryLiveData(USED_LEASH_POSTS_REF);
        private final FirebaseQueryLiveData UsedfinsPostsLiveData = new FirebaseQueryLiveData(USED_FINS_POSTS_REF);
        private final FirebaseQueryLiveData usedclothingPostsLiveData = new FirebaseQueryLiveData(USED_CLOTHING_POSTS_REF);
        private final FirebaseQueryLiveData usedotherPostsLiveData = new FirebaseQueryLiveData(USED_OTHER_POSTS_REF);

        private final FirebaseQueryLiveData NewboardPostsLiveData = new FirebaseQueryLiveData(NEW_BOARDS_POSTS_REF);
        private final FirebaseQueryLiveData NewleashPostsLiveData = new FirebaseQueryLiveData(NEW_LEASH_POSTS_REF);
        private final FirebaseQueryLiveData NewfinsPostsLiveData = new FirebaseQueryLiveData(NEW_FINS_POSTS_REF);
        private final FirebaseQueryLiveData NewclothingPostsLiveData = new FirebaseQueryLiveData(NEW_CLOTHING_POSTS_REF);
        private final FirebaseQueryLiveData NewotherPostsLiveData = new FirebaseQueryLiveData(NEW_OTHER_POSTS_REF);

        public FirebaseQueryLiveData getUsedBoardPostsLiveData() {
            return usedboardPostsLiveData;
        }

        public FirebaseQueryLiveData getUsedLeashPostsLiveData() {
            return UsedleashPostsLiveData;
        }

        public FirebaseQueryLiveData getUsedFinsPostsLiveData() {
            return UsedfinsPostsLiveData;
        }

        public FirebaseQueryLiveData getUsedClothingPostsLiveData() {
            return usedclothingPostsLiveData;
        }

        public FirebaseQueryLiveData getUsedOtherPostsLiveData() {
            return usedotherPostsLiveData;
        }


        public FirebaseQueryLiveData getNewBoardPostsLiveData() {
            return NewboardPostsLiveData;
        }

        public FirebaseQueryLiveData getNewleashPostsLiveData() {
            return NewleashPostsLiveData;
        }

        public FirebaseQueryLiveData getNewfinsPostsLiveData() {
            return NewfinsPostsLiveData;
        }

        public FirebaseQueryLiveData getNewclothingPostsLiveData() {
            return NewclothingPostsLiveData;
        }

        public FirebaseQueryLiveData getNewotherPostsLiveData() {
            return NewotherPostsLiveData;
        }



    }
