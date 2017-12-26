package com.example.videoplayerdemo.VideoListView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.videoplayerdemo.R;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import java.util.ArrayList;

/**
 *Created by Harish on 15/6/17.
 */

public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.VideoViewHolder> {
    private static final String LOG_TAG = "CustomRecyclerView";
    private Context mContext;
    private ArrayList<CustomVideoItem> mVideoItems;
    private CustomRecyclerView listenerRecyclerView;

    public CustomRecyclerViewAdapter(Context context, ArrayList<CustomVideoItem> customVideoItems) {
        mContext = context;
        mVideoItems = customVideoItems;
    }

    ArrayList<CustomVideoItem> getmVideoItems() {
        return mVideoItems;
    }


    void setListenerRecyclerView(CustomRecyclerView listenerRecyclerView) {
        this.listenerRecyclerView = listenerRecyclerView;
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

        CustomVideoItem videoItem = mVideoItems.get(position);
        Log.e(LOG_TAG,"bind position -- >" + position);

        videoItem.setAttachedViewHolder(holder);
        if(!videoItem.isFirstVideoPlayed() && position == 0) {
            listenerRecyclerView.playFirstVideo();
            videoItem.setFirstVideoPlayed(true);
        }
    }


    @Override
    public void onViewRecycled(VideoViewHolder holder) {
        CustomVideoItem videoItem = mVideoItems.get(holder.getAdapterPosition());
        videoItem.setAttachedViewHolder(null);
        super.onViewRecycled(holder);
    }


    @Override
    public int getItemCount() {
        return mVideoItems.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        final SimpleExoPlayerView simpleExoPlayerView;
        private SimpleExoPlayer simpleExoPlayer  = null;
        final ProgressBar progressBar;
        //final RelativeLayout adViewContainer;

        VideoViewHolder(View view) {
            super(view);
            view.getLayoutParams().height = (getScreenHeight(mContext)/3)*2;
            simpleExoPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.player_view);
            progressBar = (ProgressBar) view.findViewById(R.id.exoProgressBar);
            //adViewContainer = (RelativeLayout) view.findViewById(R.id.adViewContainer);
        }
    }

    static int getScreenHeight(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }
}
