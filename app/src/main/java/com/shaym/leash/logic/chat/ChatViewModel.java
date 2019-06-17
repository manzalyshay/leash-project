package com.shaym.leash.logic.chat;

import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseReference;
import com.shaym.leash.logic.utils.FirebaseQueryLiveData;

public class ChatViewModel extends ViewModel {

    private DatabaseReference CHAT_LIVE_DATA;
    private FirebaseQueryLiveData ChatLiveData;


    public void setCHAT_LIVE_DATA(DatabaseReference CHAT_LIVE_DATA) {
        this.CHAT_LIVE_DATA = CHAT_LIVE_DATA;
        ChatLiveData = new FirebaseQueryLiveData(CHAT_LIVE_DATA);

    }

    public FirebaseQueryLiveData getChatLiveData() {
        return ChatLiveData;
    }
}
