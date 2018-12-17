    package com.shaym.leash.ui.home.cameras;

    import android.app.Activity;
    import android.content.Context;
    import android.support.annotation.NonNull;
    import android.support.v4.app.FragmentManager;
    import android.support.v4.app.FragmentTransaction;
    import android.support.v7.widget.RecyclerView;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.MenuItem;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.PopupMenu;
    import android.widget.Toast;

    import com.shaym.leash.R;
    import com.squareup.picasso.Callback;
    import com.squareup.picasso.NetworkPolicy;
    import com.squareup.picasso.Picasso;

    import java.util.List;

    import tcking.github.com.giraffeplayer2.VideoInfo;

    public class CamerasAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context mContext;
        private List<Camera> camerasList;
        static  final String WEB_STREAM = "WEB_STREAM";
        static final String PLAYER_STREAM = "PLAYER_STREAM";
        private static final String HILTONA = "hiltona";
        private static final String HILTONB = "hiltonb";
        private static final String GORDON = "gordon";
        private final static int FULL_SCREEN_ID = 01;
        private final static int LANDSCAPE_ID = 02;
        private FragmentManager mFragmentManager;

        private static final String TAG = "CamerasAdapter";


        CamerasAdapter(Context mContext, List<Camera> cameralist, FragmentManager fm) {
            Log.d(TAG, "CamerasAdapter: ");
            this.mContext = mContext;
            this.camerasList = cameralist;
            this.mFragmentManager = fm;

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            switch (viewType) {
                case 0:
                    View PlayerItemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.cameras_grid_player, parent, false);

                    return new PlayerViewHolder(PlayerItemView);

                case 1:
                    View webItemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.cameras_grid_webview, parent, false);

                    return new WebViewHolder(webItemView);
                default:
                    return null;
            }
        }

        @Override
        public int getItemViewType(int position) {
            // Just as an example, return 0 or 2 depending on position
            // Note that unlike in ListView adapters, types don't have to be contiguous
            String type =  camerasList.get(position).getStreamKind();
            int typenum = -1;
            switch (type){
                case PLAYER_STREAM:
                    typenum = 0;
                    break;
                case WEB_STREAM:
                    typenum = 1;
                    break;
            }
            return typenum;
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            final Camera cam = camerasList.get(position);
            switch (cam.getStreamKind()) {
                case PLAYER_STREAM:
                    ((PlayerViewHolder) holder).getTitle().setText(cam.getBeachName());
                    ((PlayerViewHolder) holder).getLocation().setText(cam.getLocation());
                    ((PlayerViewHolder) holder).getOverflow().setOnClickListener(view -> showPopupMenu(((PlayerViewHolder) holder).getOverflow(), holder, PLAYER_STREAM, cam) );

                    ImageView path = ((PlayerViewHolder) holder).getVideoView().getCoverView();
                    switch (cam.getPicName()){
                        case HILTONA:
                            Picasso.get().load(R.drawable.hiltona).resize(200,200).networkPolicy(NetworkPolicy.OFFLINE).into(path, new Callback() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onError(Exception e) {
                                    //Try again online if cache failed

//                                    Picasso.get().load(R.drawable.hiltona).resize(200,200).into(path);

                                }
                            });
                            break;
                        case HILTONB:
                            Picasso.get().load(R.drawable.hiltonb).resize(200,200).networkPolicy(NetworkPolicy.OFFLINE).into(path, new Callback() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onError(Exception e) {
                                    //Try again online if cache failed

//                                    Picasso.get().load(R.drawable.hiltonb).resize(200,200).into(path);

                                }
                            });
                            break;
                        case GORDON:
                            Picasso.get().load(R.drawable.gordon).resize(200,200).networkPolicy(NetworkPolicy.OFFLINE).into(path, new Callback() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onError(Exception e) {
                                    //Try again online if cache failed

//                                    Picasso.get().load(R.drawable.gordon).resize(200,200).into(path);

                                }
                            });
                            break;

                    }


                    ((PlayerViewHolder) holder).getVideoView().getVideoInfo().setPortraitWhenFullScreen(false).setAspectRatio(VideoInfo.AR_MATCH_PARENT);
                    ((PlayerViewHolder) holder).getVideoView().setVideoPath(cam.getUrl()).setFingerprint(position);

                    ((PlayerViewHolder) holder).getVideoView().setOnClickListener(view -> ((Activity) mContext).runOnUiThread(() -> {
                        Log.d(TAG, "run: PlayerviewHolder");
                        ((PlayerViewHolder) holder).getVideoView().getPlayer().start();
                    }));


                    break;

                case WEB_STREAM:
                    ((WebViewHolder) holder).getTitle().setText(cam.getBeachName());
                    ((WebViewHolder) holder).getLocation().setText(cam.getLocation());
                    ((WebViewHolder) holder).getWebView().loadUrl(cam.getUrl());

                    ((WebViewHolder) holder).getOverflow().setOnClickListener(view -> showPopupMenu(((WebViewHolder) holder).getOverflow(), holder, WEB_STREAM, cam));
                    break;
            }

            }




        @Override
        public int getItemCount() {
            return camerasList.size();
        }

        private void showPopupMenu(View view, final RecyclerView.ViewHolder holder, String streamKind, Camera camera) {
            // inflate menu
            PopupMenu popup = new PopupMenu(mContext, view);
            popup.getMenu().add(0, FULL_SCREEN_ID, 0, mContext.getString(R.string.action_fullscreen));
            popup.getMenu().add(0, LANDSCAPE_ID, 1, mContext.getString(R.string.action_landscape));

            popup.setOnMenuItemClickListener(new MyMenuItemClickListener(holder, streamKind, camera));
            popup.show();
        }




        /**
         * Click listener for popup menu items
         */
        class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
            private RecyclerView.ViewHolder holder;
            private  String streamKind;
            private Camera mCamera;
            MyMenuItemClickListener(RecyclerView.ViewHolder holder, String streamKind, Camera cam) {
                this.holder = holder;
                this.streamKind = streamKind;
                this.mCamera = cam;
            }

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case FULL_SCREEN_ID:
                        if (streamKind.equals(PLAYER_STREAM))     {
                            ((PlayerViewHolder) holder).getVideoView().getPlayer().toggleFullScreen();
                        }
                        else {
                            FullScreenWebviewFragment mFullscreenWebviewFragment = FullScreenWebviewFragment.newInstance(mCamera.getUrl());

                            FragmentTransaction transaction = mFragmentManager.beginTransaction();

                            // Replace whatever is in the fragment_container view with this fragment,
                            // and add the transaction to the back stack if needed
                            transaction.replace(R.id.relLayout2, mFullscreenWebviewFragment, "FULL-SCREEN-WEBVIEW");

                            transaction.addToBackStack(null);

                            transaction.commit();

                        }
                        break;
                    case LANDSCAPE_ID:
                        Toast.makeText(mContext, "Landscape", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return false;
            }
        }



    }