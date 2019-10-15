package com.shaym.leash.models;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.shaym.leash.models.Post;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// [START comment_class]
@IgnoreExtraProperties
public class Conversation extends Post {

    public String key;

    public String initiatorUID;
    public String receiverUID;
    public String conversationID;
    public Date timeStarted;

    public Conversation(){
    }

    public Conversation(String key, String initiatorUID, String receiverUID, String conversationID, Date timeStarted) {
        this.key = key;
        this.initiatorUID = initiatorUID;
        this.receiverUID = receiverUID;
        this.conversationID = conversationID;
        this.timeStarted = timeStarted;
    }

    public String getInitiatorUID() {
        return initiatorUID;
    }

    public String getReceiverUID() {
        return receiverUID;
    }

    public String getConversationID() {
        return conversationID;
    }

    public Date getTimeStarted() {
        return timeStarted;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("initiatorUID", initiatorUID);
        result.put("receiverUID", receiverUID);
        result.put("conversationID", conversationID);
        result.put("timeStarted", timeStarted);


        return result;
    }
}