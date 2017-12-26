package com.example.videoplayerdemo.customview;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.videoplayerdemo.R;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


public class ContentFragment extends Fragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_POSITION = "position";

    private String mTitle = "";
    private int mPosition = 0;
    private SimpleExoPlayer mPlayer;
    private OnFragmentInteractionListener mListener;
    private DataSource.Factory mDataSourceFactory;
    private Uri mUri;

    public ContentFragment() {
        // Required empty public constructor
    }

    public static ContentFragment newInstance(String title , int position) {
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
            mPosition = getArguments().getInt(ARG_POSITION);
        }

        if(mPlayer == null) {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            mUri = Uri.parse("https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4");
            Log.i(this.getClass().toString(),"content type is "+Util.inferContentType(mUri));

            mDataSourceFactory = new DefaultDataSourceFactory(getActivity(),
                    Util.getUserAgent(getActivity(), "VideoPlayerDemo" + mPosition));

            mPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(),trackSelector);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_content, container, false);
        SimpleExoPlayerView simpleExoPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.player_view);
        view.getLayoutParams().height = 300;

        // 2. Create the player

        // Bind the player to the view.
        simpleExoPlayerView.setPlayer(mPlayer);

        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource videoSource = new ExtractorMediaSource(mUri,
                mDataSourceFactory, extractorsFactory, null, null);
        mPlayer.prepare(videoSource);

        return view;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public String getTitle() {
        return mTitle;
    }

    public int getPosition() {
        return mPosition;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //utState.putSerializable("PLAYER",mPlayer);
        //MediaPlayer
        super.onSaveInstanceState(outState);
    }
}
