package com.shaym.leash.ui.forum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.shaym.leash.R;
import com.shaym.leash.logic.forum.Comment;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.FireBasePostsHelper;

import java.util.ArrayList;
import java.util.List;


public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private static final String TAG = "CommentsAdapter";
    private List<Comment> mComments = new ArrayList<>();
    private List<Profile> mAllUsers = new ArrayList<>();

    public void setAllUsers(List<Profile> AllUsers) {
        mAllUsers = AllUsers;
        notifyDataSetChanged();
    }


    public void setComments(List<Comment> newComments) {
        if (!mComments.isEmpty()) {
            CommentDiffCallback commentDiffCallback = new CommentDiffCallback(mComments, newComments);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(commentDiffCallback);

            mComments.clear();
            mComments.addAll(newComments);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            mComments = newComments;
            notifyDataSetChanged();

        }


    }


    public CommentsAdapter() {
    }


    @Override
    public int getItemCount() {
        return mComments.size();
    }

    // Inflates the appropriate layout according to the ViewType.
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = mComments.get(position);
        holder.bind(comment, position);
    }


    class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView authorView;
        TextView dateView;
        ImageView authorPic;
        ProgressBar progressBar;
        TextView bodyView;

        CommentViewHolder(View itemView) {
            super(itemView);

            authorView = itemView.findViewById(R.id.comment_author);
            dateView = itemView.findViewById(R.id.comment_date);
            bodyView = itemView.findViewById(R.id.comment_body);
            authorPic = itemView.findViewById(R.id.comment_photo);
            progressBar = itemView.findViewById(R.id.comment_photo_progressbar);

        }

        void bind(Comment comment, int position) {
            bodyView.setText(comment.text);


            int dayspast = FireBasePostsHelper.getInstance().getDaysDifference(comment.date);

            if (dayspast != 0) {
                dateView.setText("לפני " + dayspast + " ימים");
            } else {
                dateView.setText("היום");

            }

            if (!mAllUsers.isEmpty()) {
                Profile profile = getProfileByID(comment.uid);
                assert profile != null;
                authorView.setText(profile.getDisplayname());
                FireBasePostsHelper.getInstance().attachRoundPic(profile.getAvatarurl(), authorPic, progressBar, 100, 100);
            }

        }
    }

    private Profile getProfileByID(String uid) {
        for (int i = 0; i < mAllUsers.size(); i++) {
            if (mAllUsers.get(i).getUid().equals(uid))
                return mAllUsers.get(i);
        }

        return null;
    }

    class CommentDiffCallback extends DiffUtil.Callback {

        private final List<Comment> oldPosts, newPosts;

        public CommentDiffCallback(List<Comment> oldPosts, List<Comment> newPosts) {
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
}


