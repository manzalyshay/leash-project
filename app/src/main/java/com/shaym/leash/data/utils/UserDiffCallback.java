package com.shaym.leash.data.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.shaym.leash.models.Profile;

import java.util.List;

public class UserDiffCallback extends DiffUtil.Callback {

    private final List<Profile> oldusers, newusers;

    public UserDiffCallback(List<Profile> oldusers, List<Profile> newusers) {
        this.oldusers = oldusers;
        this.newusers = newusers;
    }

    @Override
    public int getOldListSize() {
        return oldusers.size();
    }

    @Override
    public int getNewListSize() {
        return newusers.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldusers.get(oldItemPosition).getUid().equals(newusers.get(newItemPosition).getUid());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldusers.get(oldItemPosition).equals(newusers.get(newItemPosition));
    }
}