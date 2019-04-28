    package com.shaym.leash.ui.home.cameras;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;

    import com.shaym.leash.R;
    import com.squareup.picasso.Picasso;

    import java.util.List;

    public class CamerasAdapter extends RecyclerView.Adapter<CameraItemViewHolder> {

        private onCameraSelectedListener mListener;
        private List<Camera> camerasList;

        private static final String TAG = "CamerasAdapter";
        int selected_position = 0; // You have to set this globally in the Adapter class


        CamerasAdapter(onCameraSelectedListener listener, List<Camera> cameralist) {
            Log.d(TAG, "CamerasAdapter: ");
            this.mListener = listener;
            this.camerasList = cameralist;

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
            final Camera cam = camerasList.get(position);
            Picasso.get().load(cam.getPicRef()).into(holder.getCover());
            holder.getTitle().setText(cam.getBeachName());


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