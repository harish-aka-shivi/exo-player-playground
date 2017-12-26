package com.example.videoplayerdemo.VideoListView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import java.util.ArrayList;


/**
 * Created by Harish 14/6/17.
 */

public class CustomRecyclerView extends RecyclerView  {
    private Context mContext;
    private LinearLayoutManager mLinearLayoutManager;
    private static final String LOG_TAG = "CustomRecyclerView";
    private int mCurrentFirstVisiblePosition = -1;
    private ArrayList<CustomVideoItem> mDataSet;
    private CustomRecyclerViewAdapter mAdapter;
    private CustomVideoItem mCurrentVideoItem = null;

    private MyEventListener mMyEventListener;
    private CustomRecyclerViewAdapter.VideoViewHolder mCurrentViewHolder = null;
    private PagerSnapHelper mSnapHelper;

    public CustomRecyclerView(Context context) {
        super(context);
        this.mContext = context;
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attributeSet) {
        super(context,attributeSet);
        mContext = context;
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attributeSet,int defStyle) {
        super(context,attributeSet,defStyle);
        mContext = context;
    }

    // initialize and play video
    private void playVideo(CustomVideoItem videoItem, int position) {
        if(videoItem == null || position == -1) {
            return;
        }

        if(videoItem.getMediaSource() == null) {
            Log.e(LOG_TAG,"No Media Source added to Custom Video Item");
            return;
        }

        // Check implemented due recycling the player during view recycled.
        if(videoItem.getSimpleExoPlayer() == null) {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);
            SimpleExoPlayer simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext,trackSelector);

            videoItem.setSimpleExoPlayer(simpleExoPlayer);

        }

        videoItem.getSimpleExoPlayer().prepare(videoItem.getMediaSource());

        videoItem.getSimpleExoPlayer().setPlayWhenReady(true);

        mCurrentViewHolder = videoItem.getAttachedViewHolder();


        if(videoItem.getAttachedViewHolder().simpleExoPlayerView != null) {
            videoItem.getAttachedViewHolder().simpleExoPlayerView.setPlayer(videoItem.getSimpleExoPlayer());
        }

        mMyEventListener = new MyEventListener();
        videoItem.getSimpleExoPlayer().addListener(mMyEventListener);
    }


    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if(layout instanceof LinearLayoutManager) {
            mSnapHelper = new PagerSnapHelper();
            mSnapHelper.attachToRecyclerView(this);
            mLinearLayoutManager = (LinearLayoutManager) layout;
        } else {
            Log.e(LOG_TAG,"This recycler view only supports Linear Layout Manager. Stay tuned for more updates");
            throw new ClassCastException();
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {

        if(adapter instanceof  CustomRecyclerViewAdapter) {
            mDataSet =  ((CustomRecyclerViewAdapter)adapter).getmVideoItems();
            mAdapter = (CustomRecyclerViewAdapter) adapter;
            mAdapter.setListenerRecyclerView(this);
            this.addOnScrollListener(new MyScrollListener());
        } else {
            Log.e(LOG_TAG,"Adapter not instance of CustomRecyclerViewAdapter");
         //    mAdapter = (CustomRecyclerViewAdapter) adapter;
        }

        super.setAdapter(adapter);
    }



    public class MyScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                int firstCompleteVisible = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();

                //Check in case user has not actually scrolled to the next position
                // And is still on the same position
                if(firstCompleteVisible == -1 || mCurrentFirstVisiblePosition == firstCompleteVisible) {
                    return;
                }

                mCurrentFirstVisiblePosition = firstCompleteVisible;
                int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();

                Log.i(LOG_TAG,"top item is -->"+mLinearLayoutManager.findFirstVisibleItemPosition()+
                        "middle position  is --> " + mLinearLayoutManager.findFirstCompletelyVisibleItemPosition() +
                        "last pos is  =--->" + mLinearLayoutManager.findLastVisibleItemPosition());


                for(int i = firstVisibleItem; i <= lastVisibleItem; i++) {
                    //stop this player
                    stopVideoBuffering(mDataSet.get(i),i);
                    if(i == firstCompleteVisible) {
                        playVideo(mDataSet.get(i),i);
                        mCurrentVideoItem = mDataSet.get(i);
                    }
                }
                releasePlayersOnScroll();

            }
        }
    }

    public void playFirstVideo() {
        mCurrentFirstVisiblePosition = 0;
        mCurrentVideoItem = mDataSet.get(0);
        playVideo(mDataSet.get(0),0);
    }


    // scroll sometimes skips the idle condition for fast scroll
    // Hence some players remain in memory while not in view
    // so releasing all the remaining players in memory
    private void releasePlayersOnScroll() {
        for(CustomVideoItem videoItem : mDataSet) {
            if(videoItem != mCurrentVideoItem && videoItem.getSimpleExoPlayer() != null) {
                videoItem.getSimpleExoPlayer().removeListener(mMyEventListener);
                videoItem.getSimpleExoPlayer().release();
                videoItem.setSimpleExoPlayer(null);

            }
        }
    }

    //stop buffer and free up other resources
    private void stopVideoBuffering(CustomVideoItem videoItem,int position) {
        if(videoItem.getSimpleExoPlayer() != null) {
            videoItem.getSimpleExoPlayer().removeListener(mMyEventListener);
            videoItem.getSimpleExoPlayer().release();
            videoItem.setSimpleExoPlayer(null);
            videoItem.getAttachedViewHolder().simpleExoPlayerView.setPlayer(null);
        }
    }


    private void showProgressBarInVideo(boolean toShow) {
        if (mCurrentViewHolder != null) {
            if (toShow) {
                mCurrentViewHolder.progressBar.setVisibility(View.VISIBLE);
            } else {
                mCurrentViewHolder.progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void releasePlayer() {

        if(mCurrentVideoItem != null && mCurrentVideoItem.getSimpleExoPlayer() != null) {
            mCurrentVideoItem.getSimpleExoPlayer().removeListener(mMyEventListener);
            mCurrentVideoItem.getSimpleExoPlayer().release();
            mCurrentVideoItem.setSimpleExoPlayer(null);
            // mCurrentVideoItem.setAttachedViewHolder(null);
        }
    }


    public void onStopCalled() {
        releasePlayer();
    }

    public void onStartCalled() {
        if(mCurrentFirstVisiblePosition == -1 || mCurrentVideoItem == null) {
            smoothScrollToPosition(0);
        } else playVideo(mCurrentVideoItem,mCurrentFirstVisiblePosition);
    }


    public class MyEventListener implements ExoPlayer.EventListener {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            if(playbackState == ExoPlayer.STATE_BUFFERING) {
                showProgressBarInVideo(true);
            } else {
                showProgressBarInVideo(false);

                if(playbackState == ExoPlayer.STATE_ENDED) {
                    CustomRecyclerView.this.smoothScrollToPosition(mCurrentFirstVisiblePosition+1);
                }
            }


        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity() {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }
    }


}
