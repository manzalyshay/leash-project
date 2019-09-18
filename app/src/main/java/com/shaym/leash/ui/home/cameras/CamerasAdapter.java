    package com.shaym.leash.ui.home.cameras;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;

    import com.shaym.leash.R;
    import com.shaym.leash.logic.cameras.CameraObject;
    import com.shaym.leash.logic.utils.FireBasePostsHelper;
    import com.shaym.leash.ui.utils.UIHelper;
    import com.squareup.picasso.Picasso;

    import java.util.ArrayList;
    import java.util.List;

    public class CamerasAdapter extends RecyclerView.Adapter<CameraItemViewHolder> {

        private onCameraSelectedListener mListener;
        private List<CameraObject> camerasList = new ArrayList<>();

        private static final String TAG = "CamerasAdapter";
        int selected_position = 0; // You have to set this globally in the Adapter class
        void setCamerasList(List<CameraObject> camerasList) {
            this.camerasList = camerasList;
            selected_position = 0;
            notifyDataSetChanged();
        }

        CamerasAdapter(onCameraSelectedListener listener) {
            Log.d(TAG, "CamerasAdapter: ");
            this.mListener = listener;

        }

        @NonNull
        @Override
        public CameraItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View CameraItemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cameras_grid_item, parent, false);
            return new CameraItemViewHolder(CameraItemView, this);
        }

        @Override
        public void onBindViewHolder(@NonNull final CameraItemViewHolder holder, final int position) {
            final CameraObject cam = camerasList.get(position);
            UIHelper.getInstance().attachPic(cam.mPicRef, holder.getCover(), holder.getProgressbar(), 150, 150);
            holder.getTitle().setText(cam.getLocation());


            if (selected_position == position){
                holder.itemView.setBackgroundResource(R.drawable.underline_cameras);
                holder.getPlayCover().setVisibility(View.INVISIBLE);
                mListener.onCameraSelected(cam);
            }
            else {
                holder.itemView.setBackgroundResource(0);
                holder.getPlayCover().setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return camerasList.size();
        }

    }