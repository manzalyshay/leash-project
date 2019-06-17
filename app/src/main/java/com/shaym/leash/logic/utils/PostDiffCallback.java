package com.shaym.leash.logic.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.shaym.leash.logic.forum.Post;

import java.util.List;

public class PostDiffCallback extends DiffUtil.Callback {

    private final List<Post> oldPosts, newPosts;

    public PostDiffCallback(List<Post> oldPosts, List<Post> newPosts) {
        this.oldPosts = oldPosts;
        this.newPosts = newPosts;
    }

    @Override
    public int getOldListSize() {
        return oldPosts.size();
    }

    @Override
    public int getNewListSize() {
        return newPosts.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldPosts.get(oldItemPosition).key.equals(newPosts.get(newItemPosition).key);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldPosts.get(oldItemPosition).equals(newPosts.get(newItemPosition));
    }
}