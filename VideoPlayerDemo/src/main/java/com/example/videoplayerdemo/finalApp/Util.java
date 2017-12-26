package com.example.videoplayerdemo.finalApp;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by staqu on 28/6/17.
 */

public class Util {

    public static final String LOG_TAG  = "EXO_PLAYER_DEMO";

    public static int getScreenHeight(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

}
