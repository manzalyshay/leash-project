package com.shaym.leash.ui.forum;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.shaym.leash.R;
import com.shaym.leash.models.Comment;
import com.shaym.leash.models.Post;
import com.shaym.leash.models.GearPost;
import com.shaym.leash.models.Profile;
import com.shaym.leash.data.utils.FireBasePostsHelper;
import com.shaym.leash.data.utils.FireBaseUsersHelper;
import com.shaym.leash.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private static final String TAG = "CommentsAdapter";
    private List<Comment> mComments = new ArrayList<>();
    private List<Profile> mAllUsers = new ArrayList<>();
    private Post mCurrentPost;
    private Fragment mFragment;

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


    public CommentsAdapter(Post post, Fragment fragment) {
        mCurrentPost = post;
        mFragment = fragment;
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
        holder.bind(comment);
    }


    class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        TextView authorView;
        TextView dateView;
        ImageView authorPic;
        ProgressBar progressBar;
        TextView bodyView;
        ImageView settings;
        Comment currentComment;
        Profile commentAuthor;

        CommentViewHolder(View itemView) {
            super(itemView);

            authorView = itemView.findViewById(R.id.comment_author);
            dateView = itemView.findViewById(R.id.comment_date);
            bodyView = itemView.findViewById(R.id.comment_body);
            authorPic = itemView.findViewById(R.id.comment_photo);
            progressBar = itemView.findViewById(R.id.comment_photo_progressbar);
            settings = itemView.findViewById(R.id.settings);
            settings.setOnClickListener(this);
            authorPic.setOnClickListener(this);

        }

        void bind(Comment comment) {
            currentComment = comment;
            bodyView.setText(comment.text);


            int dayspast = FireBasePostsHelper.getInstance().getDaysDifference(comment.date);

            if (dayspast != 0) {
                if (Locale.getDefault().getLanguage().equals("en")) {
                    dateView.setText(bodyView.getContext().getString(R.string.days_ego_en, dayspast));
                }
                else
                {
                    dateView.setText(bodyView.getContext().getString(R.string.days_ego_he, dayspast));

                }
            } else {
                dateView.setText(bodyView.getContext().getString(R.string.today));

            }

            if (!mAllUsers.isEmpty()) {
                Profile profile = getProfileByID(comment.uid);
                assert profile != null;
                commentAuthor = profile;
                authorView.setText(profile.getDisplayname());
                UIHelper.getInstance().attachRoundPic(profile.getAvatarurl(), authorPic, progressBar, 100, 100);
            }

        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.comment_photo){
                FireBaseUsersHelper.getInstance().showProfilePopup(commentAuthor, mFragment);
            }
            else {
                showPopup(v);

            }
        }

        void showPopup(View v) {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.setOnMenuItemClickListener(this);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.forumpost_settings_menu, popup.getMenu());
            if (FireBaseUsersHelper.getInstance().getUid().equals(currentComment.uid)){
                popup.getMenu().findItem(R.id.delete).setVisible(true);
            }
            popup.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.delete) {
                if (mCurrentPost instanceof GearPost) {
                    FireBasePostsHelper.getInstance().deleteGearComment(currentComment, (GearPost) mCurrentPost);
                } else {
                    FireBasePostsHelper.getInstance().deleteForumComment(currentComment, mCurrentPost);

                }
                return true;
            }
            return false;
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

        CommentDiffCallback(List<Comment> oldPosts, List<Comment> newPosts) {
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


