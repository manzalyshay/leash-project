    package com.shaym.leash.ui.home.cameras;

    import android.app.Activity;
    import android.content.Context;
    import android.os.Handler;
    import android.os.Parcelable;
    import android.support.v7.widget.RecyclerView;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.MenuInflater;
    import android.view.MenuItem;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.PopupMenu;
    import android.widget.Toast;

    import com.shaym.leash.R;

    import java.util.List;

    public class CamerasAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context mContext;
        private List<Camera> camerasList;
        final Handler mHandler = new Handler();
        public final String WEB_STREAM;
        public final String PLAYER_STREAM;
        private static final String TAG = "CamerasAdapter";


        public CamerasAdapter(Context mContext, List<Camera> cameralist) {
            Log.d(TAG, "CamerasAdapter: ");
            this.mContext = mContext;
            this.camerasList = cameralist;
            WEB_STREAM = mContext.getResources().getString(R.string.web_stream_kind);
            PLAYER_STREAM = mContext.getResources().getString(R.string.web_stream_kind);

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    View PlayerItemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.cameras_grid_player, parent, false);

                    return new PlayerViewHolder(PlayerItemView);
                case 2:
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
            return position % 2 * 2;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            final Camera cam = camerasList.get(position);
            if ((position  % 2) ==  0) {
                ((PlayerViewHolder) holder).getTitle().setText(cam.getBeachName());
                ((PlayerViewHolder) holder).getLocation().setText(cam.getLocation());
                ((PlayerViewHolder) holder).getOverflow().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopupMenu(((PlayerViewHolder) holder).getOverflow());
                    }
                });

                ((PlayerViewHolder) holder).getVideoView().setVideoPath(cam.getUrl()).setFingerprint(position);

                ((PlayerViewHolder) holder).getVideoView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "run: PlayerviewHolder");
                                ((PlayerViewHolder) holder).getVideoView().getPlayer().start();
                            }
                        });
                    }
                });


            }
             else  {
                ((WebViewHolder) holder).getTitle().setText(cam.getBeachName());
                ((WebViewHolder) holder).getLocation().setText(cam.getLocation());
                ((WebViewHolder) holder).getWebView().loadUrl(cam.getUrl());
                ((WebViewHolder) holder).getWebView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "run: WebViewHolder");
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                ((WebViewHolder) holder).getWebView().loadUrl(cam.getUrl());
                            }
                        });
                    }
                });


                ((WebViewHolder) holder).getOverflow().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopupMenu(((WebViewHolder) holder).getOverflow());
                    }
                });


            }


        }

        @Override
        public int getItemCount() {
            return camerasList.size();
        }

        private void showPopupMenu(View view) {
            // inflate menu
            PopupMenu popup = new PopupMenu(mContext, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.cameras_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
            popup.show();
        }


        /**
         * Click listener for popup menu items
         */
        class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

            public MyMenuItemClickListener() {
            }

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_add_favourite:
                        Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.action_play_next:
                        Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                }
                return false;
            }
        }

    }