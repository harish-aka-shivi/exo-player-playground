package com.example.videoplayerdemo.finalApp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.videoplayerdemo.*;
import com.example.videoplayerdemo.R;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.*;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiInterstitial;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

import static com.example.videoplayerdemo.finalApp.VideoFeedAdapter.LOG_TAG;

public class ExoPlayerListActivity extends AppCompatActivity
        implements View.OnTouchListener,VideoFeedAdapter.OnInteractionWithContainingActivity,
        SimpleExoPlayer.EventListener, AudioManager.OnAudioFocusChangeListener,
        com.google.android.exoplayer2.ui.VideoTimeCallback {

    private RecyclerView mRecyclerView;
    private int mScrolledPosition = 0;
    VideoFeedAdapter mVideoFeedAdapter;
    private SnapHelper mSnapHelper;
    private ArrayList<VideoItem> mDataSet;
    private ProgressDialog mProgressDialog;

    private int mCurrentFirstCompleteVisiblePosition = -1;
    private VideoItem mCurrentVideoItem = null;

    private LinearLayoutManager mLinearLayoutManager;

    private SparseBooleanArray mMapAdTimingAndPlayed = new SparseBooleanArray();

    private RelativeLayout mRelativeLayoutRoot = null;

    private static final float TRANSPARENT_ALPHA = 0.0f;
    private static final float OPAQUE_ALPHA = 1.0f;

    private ExtractorMediaSource mExtractorMediaSource = null;
    private  DefaultHttpDataSourceFactory mDefaultHttpDataSourceFactory=  new DefaultHttpDataSourceFactory("ua");
    private DefaultExtractorsFactory mDefaultExtractorsFactory = new DefaultExtractorsFactory();

    private int mFirstVisibleItem;
    private int mLastVisibleItem;

    private int demoY = 0;

    private SimpleExoPlayerView mAdSimpleExoPlayerView = null;
    private SimpleExoPlayer mAdExoPlayer = null;

    private SparseArray<Long> sparseArray = new SparseArray<>();
    private ArrayList<Integer> mAdsList = new ArrayList<>();

    //variable tracking whether ad is played
    private boolean isAdPlaying = false;

    //Ad Event listener
    //private MyAdEventListener mMyAdEventListener = new MyAdEventListener();

    //My Interstitial Ad listener
    private MyInterstitialAdListener mInterstitialAdListener = new MyInterstitialAdListener();

    private InMobiInterstitial mCurrentInterstitialAd = null;

    private TextView mTextViewTimer = null;


    // Watching the text containing the timing information
    // Not working when we the timebar is not available
    // Now modified the code
    private TextWatcher mCurrentTimeChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            Log.e(LOG_TAG,"time is -->" +  s.toString());
            String  time = s.toString();
            String timeSec = "";
            String timeMin = "";

            try {
                //char[] charTime = time.toCharArray();
                String[] result = time.split(":");

                timeSec = result[1];
                timeMin = result[0];

                Log.i(LOG_TAG, "Time new is --> " + timeSec + "time min is -->" + timeMin);

                long timeSecInIntMilli = Long.parseLong(timeSec) * 1000;
                long timeMinInIntMilli = Long.parseLong(timeMin) * 1000 * 60;

                long finalTime = timeMinInIntMilli + timeSecInIntMilli;
                TimeBar simpleTimeBar = ((TimeBar)mCurrentVideoItem.getAttachedViewHolder().
                        simpleExoPlayerView.findViewById(com.example.videoplayerdemo.R.id.exo_progress));

                //mCurrentVideoItem.getAttachedViewHolder().simpleExoPlayerView
//                if (mAdsList.size() > 0 && mAdsList.get(0) == finalTime) {
//                    //Log.i(LOG_TAG, "show ad time reached");
//                    //Toast.makeText(ExoPlayerListActivity.this, "Show ad time reached", Toast.LENGTH_SHORT).show();
//                    mCurrentVideoItem.getSimpleExoPlayer().setPlayWhenReady(false);
//                    attachAdPlayerAndPlay(mCurrentVideoItem);
//                }
            } catch (Exception e)  {
                Log.e(LOG_TAG,e.toString());
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exo_player);
        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        mRelativeLayoutRoot = (RelativeLayout) findViewById(R.id.rootRelativeLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //mRecyclerView.setOnTouchListener(this);
        mVideoFeedAdapter = new VideoFeedAdapter(this);
        VerticalSpaceItemDecoration verticalSpaceItemDecoration = new
                VerticalSpaceItemDecoration(32);
        mRecyclerView.addItemDecoration(verticalSpaceItemDecoration);
        mRecyclerView.setClipToPadding(false);

        mRecyclerView.setAdapter(mVideoFeedAdapter);

        mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(mRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        // listener to play and pause the appropriate videos
        // Assuming there are three view on the screen.
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

//                Log.i(LOG_TAG,"state of scrolling is =->" + newState);

                // When the rv has settled, do the initialization of video player
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                    int firstCompleteVisible = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();

                    // Check in case user has actually scrolled to the next position
                    // Don't pause when user is playing with the scrolling of video player
                    // And is still on the same position
                    if(firstCompleteVisible == -1 || mCurrentFirstCompleteVisiblePosition == firstCompleteVisible) {
                        return;
                    }

                    mCurrentFirstCompleteVisiblePosition = firstCompleteVisible;
                    demoY = 0;
                    int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();

                    mFirstVisibleItem = firstVisibleItem;
                    mLastVisibleItem = lastVisibleItem;

                    // loop through all the players on screen and play first one
                    // Animation effective for three player viewable and middle one playing the video
                    for(int i = firstVisibleItem; i <= lastVisibleItem; i++) {

                        //stop this player
                        //stop buffering of all player which we on screen
                        //TODO:for now not releasing them.
                        //stopVideoBufferingAndRelease(mDataSet.get(i),i);

                        stopVideoBufferingAndPlayback(mDataSet.get(i));
                        if(i == firstCompleteVisible) {
                            mCurrentVideoItem = mDataSet.get(i);
                            //setUpNPlayVideo(mCurrentVideoItem,i);
                            setUpNPlayVideoNew(mCurrentVideoItem,i);
                            FrameLayout overlayFrameLayout =mDataSet.get(mCurrentFirstCompleteVisiblePosition).
                                    getAttachedViewHolder().simpleExoPlayerView.getOverlayFrameLayout();

                            startAnimation(overlayFrameLayout, OPAQUE_ALPHA,TRANSPARENT_ALPHA);
                        }
                    }

                    // extra computation, Necessary for fast scroll
                    // TODO: Check if we can track the player running and close that one or ensure not great overhead of this method
                    // ensuring we don't have player running in memory and offscreen
                    stopPlayersOnScroll();
                } else  {}
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setIndeterminate(true);

        mTextViewTimer = new TextView(this);
        mTextViewTimer.setTextColor(ContextCompat.getColor(this,android.R.color.white));
        mTextViewTimer.setBackgroundColor(Color.parseColor("#616161"));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //layoutParams.gravity = Gravity.TOP|Gravity.CENTER_HORIZONTAL;

        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mTextViewTimer.setPadding(PixelUtil.dpToPx(this,4),PixelUtil.dpToPx(this,4),
                PixelUtil.dpToPx(this,4),PixelUtil.dpToPx(this,4));
        layoutParams.setMargins(PixelUtil.dpToPx(this,4),PixelUtil.dpToPx(this,4),
                PixelUtil.dpToPx(this,4),PixelUtil.dpToPx(this,4));
        mTextViewTimer.setLayoutParams(layoutParams);
    }

    /**Callled from adapter to play first video*/
    @Override
    public void playFirstVideo() {
        mCurrentFirstCompleteVisiblePosition = 0;
        mCurrentVideoItem = mDataSet.get(0);
        setUpNPlayVideo(mDataSet.get(0),0);
        startAnimation(mCurrentVideoItem.getAttachedViewHolder().simpleExoPlayerView.
                getOverlayFrameLayout(), OPAQUE_ALPHA, TRANSPARENT_ALPHA);
    }


    @Override
    public void setDataSet(ArrayList<VideoItem> videos) {
        mDataSet = videos;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction()  == MotionEvent.ACTION_MOVE) {
            mRecyclerView.smoothScrollToPosition(mScrolledPosition + 1);
            mScrolledPosition = mScrolledPosition + 1;
            return true;
        }
        return false;
    }


    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {
        final int mVerticalSpaceHeight;

        VerticalSpaceItemDecoration(int mVerticalSpaceHeight) {
            this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = mVerticalSpaceHeight;
        }
    }

    //TODO : restore from the before closing state of recycler view
    /**Also implementing the our custom method of simpleExoPlayer view to start getting callback of events*/
    @Override
    protected void onStart() {
        super.onStart();
        // if android does not persist variables start from top
        if(mCurrentFirstCompleteVisiblePosition == -1 || mCurrentVideoItem == null) {
            if(mRecyclerView != null) {
                mRecyclerView.smoothScrollToPosition(0);
            }
        } else if(mCurrentVideoItem.getAttachedViewHolder() != null){
//            setUpNPlayVideo(mCurrentVideoItem, mCurrentFirstCompleteVisiblePosition);
            setUpNPlayVideoNew(mCurrentVideoItem,mCurrentFirstCompleteVisiblePosition);
            mCurrentVideoItem.getAttachedViewHolder().simpleExoPlayerView.onStartCalled();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //onStopCalled our on method made to make
        if(mCurrentVideoItem != null&& mCurrentVideoItem.getAttachedViewHolder() != null) {
            mCurrentVideoItem.getAttachedViewHolder().simpleExoPlayerView.onStopCalled();
        }
        releaseCurrentPlayerOnStop();
        releasePlayersOnStop();
    }

    private void releaseCurrentPlayerOnStop() {

       if(mCurrentVideoItem != null && mCurrentVideoItem.getSimpleExoPlayer() != null) {
           releasePlayer(mCurrentVideoItem.getSimpleExoPlayer(),this);

           //releaseAdVideoPlayer();

           mCurrentVideoItem.setSimpleExoPlayer(null);
          // mCurrentVideoItem.setAttachedViewHolder(null);
       }
    }

    /**Release the player and set also remove the listeners*/
    private void releasePlayer(SimpleExoPlayer simpleExoPlayer, ExoPlayer.EventListener eventListener) {
        simpleExoPlayer.removeListener(eventListener);
        simpleExoPlayer.release();
    }

    private void stopVideoBufferingAndPlayback(VideoItem videoItem) {
        if(videoItem.getSimpleExoPlayer() != null) {
            videoItem.setCurrentWindowIndex(videoItem.getSimpleExoPlayer().getCurrentWindowIndex());
            videoItem.setCurrentPosition(videoItem.getSimpleExoPlayer().getCurrentPosition());
            videoItem.getSimpleExoPlayer().stop();

            // animate the players going out
            FrameLayout frameLayoutOverlay = videoItem.getAttachedViewHolder().
                    simpleExoPlayerView.getOverlayFrameLayout();
            startAnimation(frameLayoutOverlay,
                    TRANSPARENT_ALPHA, OPAQUE_ALPHA);

            // Remove listener from to stop out of view players from getting events
            videoItem.getSimpleExoPlayer().removeListener(this);
        }
    }

    //stop buffer and free up other resources
    private void stopVideoBufferingAndRelease(VideoItem videoItem, int position) {
        if(videoItem.getSimpleExoPlayer() != null) {
            releasePlayer(videoItem.getSimpleExoPlayer(),this);

            videoItem.setSimpleExoPlayer(null);
            //videoItem.getAttachedViewHolder().adViewContainer.setAlpha(0.5f);

            FrameLayout frameLayoutOverlay = videoItem.getAttachedViewHolder().
                    simpleExoPlayerView.getOverlayFrameLayout();
            //frameLayoutOverlay.clearAnimation();
            startAnimation(frameLayoutOverlay,
                    TRANSPARENT_ALPHA, OPAQUE_ALPHA);


            videoItem.getAttachedViewHolder().simpleExoPlayerView.setPlayer(null);

            //Flag can be used in case when we have to show ad specific to
            // individual video item
            videoItem.setToShowInteristialAd(false);

            //hide the top view in case it is visible, or showing timer
            videoItem.getAttachedViewHolder().adViewContainer.setVisibility(View.GONE);
            videoItem.getAttachedViewHolder().adViewContainer.removeAllViews();
        }
    }

    private void setUpNPlayVideoNew(VideoItem currentVideoItem, int firstCompleteVisiblePosition) {
        if(currentVideoItem == null || firstCompleteVisiblePosition == -1) {
            return;
        }

        // set video player to video item
        if(currentVideoItem.getSimpleExoPlayer() == null) {

            //Initialize exo player
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);
            SimpleExoPlayer simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this,trackSelector);
            currentVideoItem.setSimpleExoPlayer(simpleExoPlayer);

        }

        //set listener to the current video item
        currentVideoItem.getSimpleExoPlayer().addListener(this);

        // If the media source is null the make a new media source
        if(currentVideoItem.getMediaSource() == null) {
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                    Util.getUserAgent(this, "VideoPlayerDemo" + firstCompleteVisiblePosition));
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            MediaSource videoSource;

            // TODO : check for a flag whether to concatenate
            // TODO : Remove when we have real data
            // Demo Added Concatenate Media source to the first source
            // Setting video source
            if(firstCompleteVisiblePosition == 0) {
                videoSource = new ExtractorMediaSource(currentVideoItem.getVideoUri(),
                        dataSourceFactory, extractorsFactory, null, null);
                MediaSource videoSource2 = new ExtractorMediaSource(currentVideoItem.getSecondUri(),
                        dataSourceFactory,extractorsFactory,null,null);
                videoSource = new ConcatenatingMediaSource
                        (videoSource2,videoSource);

            } else if(firstCompleteVisiblePosition == 2) {
                Uri adUri = Uri.parse("https://archive.org/download/OrangeBeachOffshoreBoatRaces/MVI_0321.mp4");
                videoSource = new ExtractorMediaSource(adUri,mDefaultHttpDataSourceFactory,
                        mDefaultExtractorsFactory,null,null);
            } else {
                videoSource = new ExtractorMediaSource(currentVideoItem.getVideoUri(),
                        mDefaultHttpDataSourceFactory , mDefaultExtractorsFactory, null, null);
            }

            if(firstCompleteVisiblePosition == 1) {
                currentVideoItem.setContainsAd(true);
            }
            currentVideoItem.setMediaSource(videoSource);
        }

        //TODO: uncomment
        // pause all other sound source
        //askAudioFocus();

        currentVideoItem.getSimpleExoPlayer().prepare(currentVideoItem.getMediaSource(),false,false);
        currentVideoItem.getSimpleExoPlayer().seekTo(currentVideoItem.getCurrentWindowIndex(),
                currentVideoItem.getCurrentPosition());
        currentVideoItem.getSimpleExoPlayer().setPlayWhenReady(true);

        //set player to simple player view
        if(currentVideoItem.getAttachedViewHolder() != null &&
                currentVideoItem.getAttachedViewHolder().simpleExoPlayerView != null) {
            currentVideoItem.getAttachedViewHolder().simpleExoPlayerView.setPlayer
                    (currentVideoItem.getSimpleExoPlayer());
        }
    }

    private void setUpNPlayVideo(VideoItem currentVideoItem, int firstCompleteVisiblePosition) {

        if(currentVideoItem == null || firstCompleteVisiblePosition == -1) {
            return;
        }

        //make and set media source to the video item
        if(currentVideoItem.getMediaSource() == null) {
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                    Util.getUserAgent(this, "VideoPlayerDemo" + firstCompleteVisiblePosition));
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            MediaSource videoSource;

            // TODO : check for a flag whether to concatenate
            // TODO : Remove when we have real data
            // Demo Added Concatenate Media source to the first source
            // Setting video source
            if(firstCompleteVisiblePosition == 0) {
                videoSource = new ExtractorMediaSource(currentVideoItem.getVideoUri(),
                        dataSourceFactory, extractorsFactory, null, null);
                MediaSource videoSource2 = new ExtractorMediaSource(currentVideoItem.getSecondUri(),
                        dataSourceFactory,extractorsFactory,null,null);
                videoSource = new ConcatenatingMediaSource
                        (videoSource2,videoSource);

            } else if(firstCompleteVisiblePosition == 2) {
                Uri adUri = Uri.parse("https://archive.org/download/OrangeBeachOffshoreBoatRaces/MVI_0321.mp4");
                videoSource = new ExtractorMediaSource(adUri,mDefaultHttpDataSourceFactory,
                        mDefaultExtractorsFactory,null,null);
            } else {
                videoSource = new ExtractorMediaSource(currentVideoItem.getVideoUri(),
                        mDefaultHttpDataSourceFactory , mDefaultExtractorsFactory, null, null);
            }

            if(firstCompleteVisiblePosition == 1) {
                currentVideoItem.setContainsAd(true);
            }
            currentVideoItem.setMediaSource(videoSource);
        }

        // Check implemented due recycling the player during view recycled.
        // Now always null
        if(currentVideoItem.getSimpleExoPlayer() == null) {

            //Initialize exo player
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);
            SimpleExoPlayer simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this,trackSelector);
            currentVideoItem.setSimpleExoPlayer(simpleExoPlayer);
        }

        // pause all other sound source
        //askAudioFocus();

        if(currentVideoItem.isContainsAd() && !currentVideoItem.isInteristialStartingLoaded()) {

            //currentVideoItem.setAdStartTime(2000);
            //mMapAdTimingAndPlayed.put(2000,false);
            Uri adUri = Uri.parse("https://archive.org/download/OrangeBeachOffshoreBoatRaces/MVI_0321.mp4");

            //prepareAdExoPlayer(adUri);
            currentVideoItem.setToShowInteristialAd(true);
            currentVideoItem.setInteristialStartingLoaded(true);
            createInterstitialAd();

//            if(mTextViewTimer.getParent() == null) {
//                mCurrentVideoItem.getAttachedViewHolder().adViewContainer.addView(mTextViewTimer);
//            } else {
//                ((RelativeLayout)mTextViewTimer.getParent()).removeAllViews();
//                currentVideoItem.getAttachedViewHolder().adViewContainer.addView(mTextViewTimer);
//            }
            mCurrentInterstitialAd.load();

        }


        // play video
        currentVideoItem.getSimpleExoPlayer().prepare(currentVideoItem.getMediaSource(),false,false);

        currentVideoItem.getSimpleExoPlayer().setPlayWhenReady(true);
        //currentVideoItem.getSimpleExoPlayer().setPlayWhenReady(false);

        //set player to simple player view
        if(currentVideoItem.getAttachedViewHolder() != null &&
                currentVideoItem.getAttachedViewHolder().simpleExoPlayerView != null) {
            currentVideoItem.getAttachedViewHolder().simpleExoPlayerView.setResizeMode
                    (AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
            currentVideoItem.getAttachedViewHolder().simpleExoPlayerView.setPlayer
                    (currentVideoItem.getSimpleExoPlayer());
        }

        //set listener to the current video item
        currentVideoItem.getSimpleExoPlayer().addListener(this);

        // adding ad breaks
        // TODO : Remove after original data
//        long [] adBreakTimeOut = new long[] {20000, 8000};
//        mAdsList.clear();
//        mAdsList.add(8);
//        mAdsList.add(20);
//
//        // setting yellow breakpoints for ads
//        TimeBar simpleTimeBar = ((TimeBar)currentVideoItem.getAttachedViewHolder().
//                simpleExoPlayerView.findViewById(R.id.exo_progress));
//        simpleTimeBar.setAdBreakTimesMs(adBreakTimeOut,2);
    }


//    private void prepareAdExoPlayer(Uri uri) {
//        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
//                Util.getUserAgent(this, "VideoPlayerDemoAd"));
//
//        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//
//        if(mAdExoPlayer== null) {
//
//            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//
//            TrackSelection.Factory videoTrackSelectionFactory =
//                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
//
//            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
//            mAdExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
//        }
//
//        MediaSource mediaSource = new ExtractorMediaSource(uri,
//                dataSourceFactory, extractorsFactory, null, null);
//        mAdExoPlayer.addListener(mMyAdEventListener);
//        mAdExoPlayer.prepare(mediaSource);
//    }

//    private void attachAdPlayerAndPlay(VideoItem videoItem) {
//
//        if (mAdExoPlayer == null) {
//            return;
//        }
//
//        if(mAdSimpleExoPlayerView == null) {
//            mAdSimpleExoPlayerView = new SimpleExoPlayerView(this);
//            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
//                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            mAdSimpleExoPlayerView.setLayoutParams(layoutParams);
//        }
//
//        videoItem.getAttachedViewHolder().adViewContainer.removeAllViews();
//
//        videoItem.getAttachedViewHolder().adViewContainer.addView(mAdSimpleExoPlayerView);
//
//        mAdSimpleExoPlayerView.setResizeMode
//                (AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
//
//        videoItem.getAttachedViewHolder().adViewContainer.setVisibility(View.VISIBLE);
//
//        mAdSimpleExoPlayerView.setPlayer(mAdExoPlayer);
//
//        mAdExoPlayer.setPlayWhenReady(true);
//    }

//    private void removeAdPlayerFromView(VideoItem videoItem) {
////        videoItem.getAttachedViewHolder().simpleExoPlayerView.getOverlayFrameLayout().removeAllViews();
//        videoItem.getAttachedViewHolder().adViewContainer.removeAllViews();
//        videoItem.getAttachedViewHolder().adViewContainer.setVisibility(View.GONE);
//    }

//    private void releaseAdVideoPlayer() {
//        if(mAdExoPlayer != null) {
//            mAdExoPlayer.release();
//        }
//    }

    private boolean askAudioFocus() {
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int result = am.requestAudioFocus(this,AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true;
        } else return false;
    }

    // scroll sometimes skips the idle condition for fast scroll
    // Hence some players remain in memory while not in view
    // so releasing all the remaining players in memory
    private void stopPlayersOnScroll() {
        int pos = 0;
        for(VideoItem videoItem : mDataSet) {
            //trying out keeping the current player in memory
//            if(videoItem != mCurrentVideoItem && videoItem.getSimpleExoPlayer() != null
//                    && pos != mCurrentFirstCompleteVisiblePosition-1 &&
//                    pos != mCurrentFirstCompleteVisiblePosition+1 ) {

            if(videoItem != mCurrentVideoItem && videoItem.getSimpleExoPlayer() != null) {

                videoItem.getSimpleExoPlayer().stop();

                ///releasePlayer(videoItem.getSimpleExoPlayer(),this);
                //videoItem.setSimpleExoPlayer(null);
            }
            pos++;
        }
    }

    private void releasePlayersOnStop() {
        int pos = 0;
        for(VideoItem videoItem : mDataSet) {

            if(videoItem != mCurrentVideoItem && videoItem.getSimpleExoPlayer() != null) {

                //videoItem.getSimpleExoPlayer().stop();
                releasePlayer(videoItem.getSimpleExoPlayer(),this);
                videoItem.setSimpleExoPlayer(null);
            }
            pos++;
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {}

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {}

    @Override
    public void onLoadingChanged(boolean isLoading) {}

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        if(playbackState == ExoPlayer.STATE_BUFFERING) {
            showProgressBarInVideo(true);
        } else {
            showProgressBarInVideo(false);

            if(playbackState == ExoPlayer.STATE_ENDED) {
                mRecyclerView.smoothScrollToPosition(mCurrentFirstCompleteVisiblePosition +1);
            }
        }

        //set listener when we are sure that we have set the video view
        if (playbackState == ExoPlayer.STATE_READY) {
            mCurrentVideoItem.getAttachedViewHolder().
                    simpleExoPlayerView.getController().setVideoCallBackListener(this);

            //TODO: remove it demo to add like twitter
            if (mDataSet.size() < 10) {
                for (int i = 1; i < 10; i++) {
                    VideoItem videoItem = new VideoItem();
                    if (i == 0) {
                        videoItem.setFirstVideoPlayed(false);
                    }
                    mDataSet.add(videoItem);
                }
                mVideoFeedAdapter.notifyItemRangeInserted(1, 10);
            }
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {}

    @Override
    public void onPositionDiscontinuity() {}

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {}

    @Override
    public void onAudioFocusChange(int focusChange) {}


    private void showProgressBarInVideo(boolean toShow) {
        if (mCurrentVideoItem.getAttachedViewHolder()!= null) {
            if (toShow) {
                mCurrentVideoItem.getAttachedViewHolder().progressBar.setVisibility(View.VISIBLE);
            } else {
                mCurrentVideoItem.getAttachedViewHolder().progressBar.setVisibility(View.GONE);
            }
        }
    }

    // Animate visibility of the exo video player overlay view
    private void startAnimation(final View view, float startValue, final float endValue) {
        final AlphaAnimation animation1 = new AlphaAnimation(startValue, endValue);
        animation1.setDuration(1000);
        //view.setAlpha(1f);
        //view.setAlpha(endValue);
        animation1.setFillAfter(true);
        //animation1.setFillBefore(true);
        view.startAnimation(animation1);
    }




    // Ad Listener for custom ad
    // not being used now
//    private class MyAdEventListener implements SimpleExoPlayer.EventListener {
//        @Override
//        public void onTimelineChanged(Timeline timeline, Object manifest) {
//
//        }
//
//        @Override
//        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
//
//        }
//
//        @Override
//        public void onLoadingChanged(boolean isLoading) {
//
//        }
//
//        @Override
//        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//
////            if(playbackState == ExoPlayer.STATE_BUFFERING) {
////                showProgressBarInVideo(true);
////            } else {
////                showProgressBarInVideo(false);
////            }
//
//            if(playbackState == ExoPlayer.STATE_ENDED) {
//                removeAdPlayerFromView(mCurrentVideoItem);
//
//                mCurrentVideoItem.getSimpleExoPlayer().setPlayWhenReady(true);
//
//                //sparseArray.removeAt(0);
//
//                if(mAdsList.size() > 0) {
//                    mAdsList.remove(0);
//                }
//
//                if(mAdsList.size() > 0) {
//                    Uri adUri = Uri.parse("https://archive.org/download/OrangeBeachOffshoreBoatRaces/MVI_0321.mp4");
//                    prepareAdExoPlayer(adUri);
//                }
//
//                isAdPlaying = false;
//                releaseAdVideoPlayer();
////              mCurrentVideoItem.getAttachedViewHolder().simpleExoPlayerView.
////                      getOverlayFrameLayout().setAlpha(0);
//            }
//        }
//
//        @Override
//        public void onPlayerError(ExoPlaybackException error) {}
//
//        @Override
//        public void onPositionDiscontinuity() {}
//
//        @Override
//        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {}
//    }
//

    //Modified callback mathod used to get
    // events from the exoplayer code
    @Override
    public void timeReached(long timeReached) {
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder, Locale.getDefault());

        int timeInSec = (int) (timeReached/1000);
        Log.i(LOG_TAG,"formatted text is -->" + Util.getStringForTime(stringBuilder, formatter, timeReached));
        timeReached = (timeReached + 500)/1000;

        Log.i(LOG_TAG,"time Reached is -->" + timeReached );

//        if (mAdsList.size() > 0 && mAdsList.get(0) == timeInSec && !isAdPlaying) {
//            Log.i(LOG_TAG,"playing ad at  -->" + timeInSec);
//            isAdPlaying = true;
//            mCurrentVideoItem.getSimpleExoPlayer().setPlayWhenReady(false);
//            attachAdPlayerAndPlay(mCurrentVideoItem);
//        }
    }

    private void createInterstitialAd() {
        if(mCurrentInterstitialAd == null) {
            mCurrentInterstitialAd = new InMobiInterstitial
                    (this, MainActivity.MY_VIDEO_DEMO_INTERSTIAL_AD_PLACEMENT_ID, mInterstitialAdListener);
        }
    }


    private class MyInterstitialAdListener implements InMobiInterstitial.InterstitialAdListener2 {
        @Override
        public void onAdLoadFailed(InMobiInterstitial inMobiInterstitial, InMobiAdRequestStatus inMobiAdRequestStatus) {
            Log.e(LOG_TAG,"ad load failed  --> " + inMobiAdRequestStatus);
        }

        @Override
        public void onAdReceived(InMobiInterstitial inMobiInterstitial) {

        }

        @Override
        public void onAdLoadSucceeded(InMobiInterstitial inMobiInterstitial) {
            //mCurrentVideoItem.getSimpleExoPlayer().setPlayWhenReady(false);

            setUpTimer();
            //inMobiInterstitial.show();
        }

        @Override
        public void onAdRewardActionCompleted(InMobiInterstitial inMobiInterstitial, Map<Object, Object> map) {

        }

        @Override
        public void onAdDisplayFailed(InMobiInterstitial inMobiInterstitial) {

        }

        @Override
        public void onAdWillDisplay(InMobiInterstitial inMobiInterstitial) {

        }

        @Override
        public void onAdDisplayed(InMobiInterstitial inMobiInterstitial) {

        }

        @Override
        public void onAdInteraction(InMobiInterstitial inMobiInterstitial, Map<Object, Object> map) {

        }

        @Override
        public void onAdDismissed(InMobiInterstitial inMobiInterstitial) {

        }

        @Override
        public void onUserLeftApplication(InMobiInterstitial inMobiInterstitial) {

        }
    }

    private void setUpTimer() {
        // Make it visible while we show notification of timer
        //mCurrentVideoItem.getAttachedViewHolder().adViewContainer.setVisibility(View.VISIBLE);
        mRelativeLayoutRoot.addView(mTextViewTimer);
        new CountDownTimer(10000,1000) {
            @Override
            public void onFinish() {
                mRelativeLayoutRoot.removeView(mTextViewTimer);
                if(mCurrentVideoItem != null && mCurrentVideoItem.getSimpleExoPlayer() != null
                        && mCurrentVideoItem.getAttachedViewHolder() != null) {
                    mCurrentVideoItem.getSimpleExoPlayer().setPlayWhenReady(false);
//                mCurrentVideoItem.getAttachedViewHolder().simpleExoPlayerView.
//                        getOverlayFrameLayout().removeAllViews();
//                mCurrentVideoItem.getAttachedViewHolder().simpleExoPlayerView.
//                        getOverlayFrameLayout().setAlpha(TRANSPARENT_ALPHA);

                    mCurrentVideoItem.getAttachedViewHolder().adViewContainer.setVisibility(View.GONE);
                    mCurrentVideoItem.getAttachedViewHolder().adViewContainer.removeAllViews();
                }

                if(mTextViewTimer != null) {
                    mTextViewTimer.setText("");
                }

                if(mCurrentInterstitialAd != null) {
                    mCurrentInterstitialAd.show();
                    mCurrentInterstitialAd = null;
                }
            }

            @Override
            public void onTick(long millisUntilFinished) {
                if(mTextViewTimer != null) {
                    mTextViewTimer.setText("Showing ad in " + millisUntilFinished / 1000);
                }
            }
        }.start();
    }

}
