package com.example.videoplayerdemo.VideoListView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;

/**
 * Created by staqu on 15/6/17.
 */

public class CustomVideoItem {

    private SimpleExoPlayer simpleExoPlayer = null;
    private MediaSource mediaSource = null;

    private boolean isPlaying = false;

    private long currentPosition;
    private int currentWindowIndex;
    private boolean firstVideoPlayed;

    //keeping reference to viewholder
    //just trying out
    //Circuclar refernces
    private CustomRecyclerViewAdapter.VideoViewHolder attachedViewHolder;

    CustomRecyclerViewAdapter.VideoViewHolder getAttachedViewHolder() {
        return attachedViewHolder;
    }

    void setAttachedViewHolder(CustomRecyclerViewAdapter.VideoViewHolder attachedViewHolder) {
        this.attachedViewHolder = attachedViewHolder;
    }

    //trying to track for onBindViewHolder
    boolean isFromScrollListener = false;

    boolean isFromScrollListener() {
        return isFromScrollListener;
    }

    void setFromScrollListener(boolean fromScrollListener) {
        isFromScrollListener = fromScrollListener;
    }


    MediaSource getMediaSource() {
        return mediaSource;
    }

    public void setMediaSource(MediaSource mediaSource) {
        this.mediaSource = mediaSource;
    }

    SimpleExoPlayer getSimpleExoPlayer() {
        return simpleExoPlayer;
    }

    void setSimpleExoPlayer(SimpleExoPlayer simpleExoPlayer) {
        this.simpleExoPlayer = simpleExoPlayer;
    }

    boolean isFirstVideoPlayed() {
        return firstVideoPlayed;
    }

    void setFirstVideoPlayed(boolean firstVideoPlayed) {
        this.firstVideoPlayed = firstVideoPlayed;
    }
}
