package com.example.videoplayerdemo.finalApp;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.videoplayerdemo.R;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import java.util.ArrayList;


/**
 * Created by Harish on 6/6/17.
 * Binding number of ExoPlayer to the viewholder instances.
 */

public class VideoFeedAdapter extends RecyclerView.Adapter<VideoFeedAdapter.VideoViewHolder> {
    Context mContext;
    public static final String LOG_TAG = "Video Demo";
    private ArrayList<VideoItem> mVideoItems;
    private ArrayList<SimpleExoPlayer> mPlayersPool = new ArrayList<>();
    //private Map<SimpleExoPlayer,ComponentListener> mPlayerNListenerPool = new HashMap<>();
    private OnInteractionWithContainingActivity mListener;
    private int lastPosition = -1;
    public static final int FADE_DURATION = 500;

    public VideoFeedAdapter(Context context) {
        mContext = context;
        mListener = (OnInteractionWithContainingActivity) context;
        mVideoItems = new ArrayList<>();
        //prepare demo dataset
        for(int i = 0; i < 1;i++) {
            VideoItem videoItem = new VideoItem();
            if(i == 0) {
                videoItem.setFirstVideoPlayed(false);
            }
            mVideoItems.add(videoItem);
        }

        //send data set to the activity
        mListener.setDataSet(mVideoItems);
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.item_exo_player,parent,false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        // 1. Create a default TrackSelector

        VideoItem videoItem = mVideoItems.get(position);
//        Log.e(LOG_TAG,"bind position -- >" + position);

        videoItem.setAttachedViewHolder(holder);
        if(!videoItem.isFirstVideoPlayed() && position == 0) {
            mListener.playFirstVideo();
            videoItem.setFirstVideoPlayed(true);
        }
//        holder.simpleExoPlayerView.getOverlayFrameLayout().setAlpha(1);
        setAnimation(holder.itemView,position);

    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
           /* Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;*/

            AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(FADE_DURATION);
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }


    @Override
    public void onViewRecycled(VideoViewHolder holder) {
        //holder.getSimpleExoPlayer().release();
        VideoItem videoItem = mVideoItems.get(holder.getAdapterPosition());
        holder.itemView.clearAnimation();
        if(holder.simpleExoPlayer  != null) {

        }
        videoItem.setAttachedViewHolder(null);
        //videoItem.setSimpleExoPlayer(null);
        /*holder.simpleExoPlayer.release();
        holder.simpleExoPlayer = null;
        holder.simpleExoPlayerView.setPlayer(null);*/
        super.onViewRecycled(holder);
    }


    @Override
    public int getItemCount() {
        return mVideoItems.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public final SimpleExoPlayerView simpleExoPlayerView;
        private SimpleExoPlayer simpleExoPlayer  = null;
        public final ProgressBar progressBar;
        public final RelativeLayout adViewContainer;

        public VideoViewHolder(View view) {
            super(view);
//            BlankFragment blankFragment = BlankFragment.getInstance();
//            FragmentManager fragmentManager =((ExoPlayerListActivity)mContext).getSupportFragmentManager();
//            fragmentManager.beginTransaction().add(R.id.relativeLayoutExoPlayerContainer,blankFragment).commit();

            view.getLayoutParams().height = (getScreenHeight(mContext)/3)*2;
            simpleExoPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.player_view);
            progressBar = (ProgressBar) view.findViewById(R.id.exoProgressBar);
            simpleExoPlayerView.getOverlayFrameLayout().setBackgroundColor
                    (ContextCompat.getColor(mContext,android.R.color.darker_gray));
            simpleExoPlayerView.getOverlayFrameLayout().setAlpha(1.0f);
            simpleExoPlayerView.getOverlayFrameLayout().setClickable(false);
            simpleExoPlayerView.getOverlayFrameLayout().setEnabled(false);
            simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
            adViewContainer = (RelativeLayout) view.findViewById(R.id.adViewContainer);

            /*simpleExoPlayerView.getOverlayFrameLayout().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });*/

        }

    }


    public static int getScreenHeight(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    interface OnInteractionWithContainingActivity {
        void setDataSet(ArrayList<VideoItem> videos);
        void playFirstVideo();
    }

}
