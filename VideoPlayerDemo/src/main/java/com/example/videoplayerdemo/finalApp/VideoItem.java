package com.example.videoplayerdemo.finalApp;

import android.net.Uri;
import android.view.View;

import com.example.videoplayerdemo.finalApp.VideoFeedAdapter;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

import java.io.Serializable;

/**
 * Created by staqu on 7/6/17.
 */

public class VideoItem implements Serializable{
    transient private Uri videoUri = Uri.parse("https://archive.org/download/ksnn_compilation_master_the_internet" +
            "/ksnn_compilation_master_the_internet_512kb.mp4");
    transient private Uri secondUri  = Uri.parse("https://ia800805.us.archive.org/11/items/Clasico5-0" +
            "/1st%20Half%20-%20Barcelona%20x%20Real%20Madrid%20--%20Forcabarca.com%20%28Xavi%20NaBLuS%29.mp4");

    private Uri generalUri = null;
    private String thumbnailUri = "";
    private String name = "";

    private SimpleExoPlayer simpleExoPlayer = null;
    private MediaSource mediaSource = null;

    private boolean isPlaying = false;

    // keep track of the info of video played per item
    private long currentPosition = 0;

    private int currentWindowIndex = 0;

    // not playing the first video due to no scrolling
    // manually play the video
    private boolean firstVideoPlayed = false;

    private boolean containsAd = false;

    transient private Uri adUri = Uri.parse("https://archive.org/download/ksnn_compilation_master_the_internet" +
            "/ksnn_compilation_master_the_internet_512kb.mp4");


    ///transient private PollingThread pollingThread;
    private int adStartTime = 0;
    private long[] adMarkerTimeInMilli = new long[] {8000, 20000,  30000};


    //trying to track for onBindViewHolder
    private boolean isFromScrollListener = false;

    //keeping reference to view holder
    //just trying out
    //Circular references
    transient private VideoFeedAdapter.VideoViewHolder attachedViewHolder;

    //flag keeping track of whether to show interestial ad.
    private boolean toShowInteristialAd = false;

    // flag tracks whether ad is know corresponding to particular video item
    private boolean isInteristialStartingLoaded = false;

    public boolean isInteristialStartingLoaded() {
        return isInteristialStartingLoaded;
    }

    public void setInteristialStartingLoaded(boolean interistialStartingLoaded) {
        isInteristialStartingLoaded = interistialStartingLoaded;
    }

    public boolean isToShowInteristialAd() {
        return toShowInteristialAd;
    }

    public void setToShowInteristialAd(boolean toShowInteristialAd) {
        this.toShowInteristialAd = toShowInteristialAd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setThumbnailUri(String thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public void setGeneralUriFromUrl(String url) {
        generalUri = Uri.parse(url);
    }

    public VideoFeedAdapter.VideoViewHolder getAttachedViewHolder() {
        return attachedViewHolder;
    }

    public void setAttachedViewHolder(VideoFeedAdapter.VideoViewHolder attachedViewHolder) {
        this.attachedViewHolder = attachedViewHolder;
    }

    public Uri getGeneralUri() {
        return generalUri;
    }

    public long[] getAdMarkerTimeInMilli() {
        return adMarkerTimeInMilli;
    }

    public void setAdMarkerTimeInMilli(long[] adMarkerTimeInMilli) {
        this.adMarkerTimeInMilli = adMarkerTimeInMilli;
    }

    public int getAdStartTime() {
        return adStartTime;
    }

    public void setAdStartTime(int adStartTime) {
        this.adStartTime = adStartTime;
    }

    public boolean isContainsAd() {
        return containsAd;
    }

    public Uri getAdUri() {
        return adUri;
    }

    public void setContainsAd(boolean containsAd) {
        this.containsAd = containsAd;
    }

    public boolean isFromScrollListener() {
        return isFromScrollListener;
    }

    public void setFromScrollListener(boolean fromScrollListener) {
        isFromScrollListener = fromScrollListener;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public Uri getSecondUri() {
        return secondUri;
    }

    public void setSecondUri(Uri secondUri) {
        this.secondUri = secondUri;
    }

    public long getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(long currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getCurrentWindowIndex() {
        return currentWindowIndex;
    }

    public void setCurrentWindowIndex(int currentWindowIndex) {
        this.currentWindowIndex = currentWindowIndex;
    }

    public boolean isFirstVideoPlayed() {
        return firstVideoPlayed;
    }

    public void setFirstVideoPlayed(boolean firstVideoPlayed) {
        this.firstVideoPlayed = firstVideoPlayed;
    }

    public MediaSource getMediaSource() {
        return mediaSource;
    }

    public void setMediaSource(MediaSource mediaSource) {
        this.mediaSource = mediaSource;
    }

    public Uri getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }

    public SimpleExoPlayer getSimpleExoPlayer() {
        return simpleExoPlayer;
    }

    public void setSimpleExoPlayer(SimpleExoPlayer simpleExoPlayer) {
        this.simpleExoPlayer = simpleExoPlayer;
    }



//
//    @Override
//    public void onTimelineChanged(Timeline timeline, Object manifest) {}
//
//    @Override
//    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {}
//
//    @Override
//    public void onLoadingChanged(boolean isLoading) {}
//
//    @Override
//    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//
//        if(playbackState == ExoPlayer.STATE_BUFFERING) {
//            showProgressBarInVideo(true);
//        } else {
//            showProgressBarInVideo(false);
//
//            if(playbackState == ExoPlayer.STATE_ENDED) {
//                mRecyclerView.smoothScrollToPosition(mCurrentFirstCompleteVisiblePosition +1);
//            }
//        }
//
//        //set listener when we are sure that we have set the video view
//        if (playbackState == ExoPlayer.STATE_READY) {
//            mCurrentVideoItem.getAttachedViewHolder().
//                    simpleExoPlayerView.getController().setVideoCallBackListener(this);
//
////            //TODO: remove it demo to add like twitter
////            if (mDataSet.size() < 10) {
////                for (int i = 1; i < 10; i++) {
////                    VideoItem videoItem = new VideoItem();
////                    if (i == 0) {
////                        videoItem.setFirstVideoPlayed(false);
////                    }
////                    mDataSet.add(videoItem);
////                }
////                mVideoFeedAdapter.notifyItemRangeInserted(1, 10);
////            }
//        }
//    }
//
//    @Override
//    public void onPlayerError(ExoPlaybackException error) {}
//
//    @Override
//    public void onPositionDiscontinuity() {}
//
//    @Override
//    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {}
//
//    private void showProgressBarInVideo(boolean toShow) {
//        if (mCurrentVideoItem.getAttachedViewHolder()!= null) {
//            if (toShow) {
//                mCurrentVideoItem.getAttachedViewHolder().progressBar.setVisibility(View.VISIBLE);
//            } else {
//                mCurrentVideoItem.getAttachedViewHolder().progressBar.setVisibility(View.GONE);
//            }
//        }
//    }
//

}
