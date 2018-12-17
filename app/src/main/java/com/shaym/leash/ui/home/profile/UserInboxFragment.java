package com.shaym.leash.ui.home.profile;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shaym.leash.R;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;

import java.util.List;

import static com.shaym.leash.logic.utils.FireBaseUsersHelper.BROADCAST_USER_CONVERSATIONS;

public class UserInboxFragment extends Fragment {
    private static final String TAG = "UserInboxFragment";

    private InboxAdapter mAdapter;
    private RecyclerView mRecycler;
    private List<String> mUserConversations;

    public UserInboxFragment() {}

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_inbox, container, false);

        v.setOnTouchListener((v1, event) -> true);

        mRecycler = v.findViewById(R.id.user_inbox_list);
        mRecycler.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecycler.setLayoutManager(mLayoutManager);

        LocalBroadcastManager.getInstance(v.getContext()).registerReceiver(mUserConversationsReceiver,
                new IntentFilter(BROADCAST_USER_CONVERSATIONS));

        FireBaseUsersHelper.getInstance().loadUserConversations();

        // specify an adapter (see also next example)

        return v;

    }


    private BroadcastReceiver mUserConversationsReceiver = new BroadcastReceiver() {
        @SuppressLint("LongLogTag")
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Log.d(TAG + "receiver", "Got message: ");
            try {
                mUserConversations = FireBaseUsersHelper.getInstance().pullUserConversations();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                mAdapter = new InboxAdapter(mUserConversations, getFragmentManager());
                mRecycler.setAdapter(mAdapter);
            }

        }
    };

    @Override
    public void onResume() {
        super.onResume();

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }



}

