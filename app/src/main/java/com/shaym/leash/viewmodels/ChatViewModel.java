package com.shaym.leash.viewmodels;

import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseReference;
import com.shaym.leash.data.utils.FirebaseQueryLiveData;

public class ChatViewModel extends ViewModel {

    private FirebaseQueryLiveData ChatLiveData;

    public void setCHAT_LIVE_DATA(DatabaseReference CHAT_LIVE_DATA) {
        ChatLiveData = new FirebaseQueryLiveData(CHAT_LIVE_DATA);
    }

    public FirebaseQueryLiveData getChatLiveData() {
        return ChatLiveData;
    }
}
