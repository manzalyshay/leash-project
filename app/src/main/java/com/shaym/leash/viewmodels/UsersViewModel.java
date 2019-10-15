package com.shaym.leash.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shaym.leash.data.utils.FirebaseQueryLiveData;

import java.util.Objects;

import static com.shaym.leash.data.utils.CONSTANT.USERS_TABLE;

public class UsersViewModel extends ViewModel {

    private static final DatabaseReference CURRENT_USER_REF =
            FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(getUid());
    private static final DatabaseReference ALL_USERS_REF = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE);
    private final FirebaseQueryLiveData currentUserLiveData = new FirebaseQueryLiveData(CURRENT_USER_REF);
    private final FirebaseQueryLiveData allUsersLiveData = new FirebaseQueryLiveData(ALL_USERS_REF);


    private FirebaseQueryLiveData userByIDLiveData;



    public void setUserByidRef(DatabaseReference userByidRef) {
        userByIDLiveData = new FirebaseQueryLiveData(userByidRef);
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