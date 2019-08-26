package com.shaym.leash.logic.user;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shaym.leash.logic.utils.FirebaseQueryLiveData;

import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.USERS_TABLE;

public class UsersViewModel extends ViewModel {

    private static final DatabaseReference CURRENT_USER_REF =
            FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(getUid());
    private static final DatabaseReference ALL_USERS_REF = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE);
    private static DatabaseReference USER_BYID_REF;

    private final FirebaseQueryLiveData currentUserLiveData = new FirebaseQueryLiveData(CURRENT_USER_REF);
    private final FirebaseQueryLiveData allUsersLiveData = new FirebaseQueryLiveData(ALL_USERS_REF);


    private FirebaseQueryLiveData userByIDLiveData;



    public void setUserByidRef(DatabaseReference userByidRef) {
        USER_BYID_REF = userByidRef;
        userByIDLiveData = new FirebaseQueryLiveData(USER_BYID_REF);
    }

    @NonNull
    public MutableLiveData<DataSnapshot> getCurrentUserDataSnapshotLiveData() {
        return currentUserLiveData;
    }

    @NonNull
    public MutableLiveData<DataSnapshot> getAllUsersDataSnapshotLiveData() {
        return allUsersLiveData;
    }

    public FirebaseQueryLiveData getUserByIDLiveData() {
        return userByIDLiveData;
    }


    public static String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

}