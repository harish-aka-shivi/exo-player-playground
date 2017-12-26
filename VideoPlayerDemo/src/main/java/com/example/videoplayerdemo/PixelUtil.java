/*
Copyright 2014 Yahoo Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.example.videoplayerdemo;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Util class for converting between dp, px and other magical pixel units
 */
public class PixelUtil {
    private static final int IMAGE_SIZE_150 = 150;
    private static final int IMAGE_SIZE_225 = 225;
    private static final int IMAGE_SIZE_300 = 300;
    private static final int IMAGE_SIZE_450 = 450;
    private static final int IMAGE_SIZE_600 = 600;

    private PixelUtil() {
    }

    public static int dpToPx(Context context, int dp) {
        int px = Math.round(dp * getPixelScaleFactor(context));
        return px;
    }

    public static int pxToDp(Context context, int px) {
        int dp = Math.round(px / getPixelScaleFactor(context));
        return dp;
    }

    private static float getPixelScaleFactor(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int getImageSizeCode(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        switch (displayMetrics.densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                return IMAGE_SIZE_150;
            case DisplayMetrics.DENSITY_MEDIUM:
            case DisplayMetrics.DENSITY_TV:
                return IMAGE_SIZE_225;
            case DisplayMetrics.DENSITY_HIGH:
            case DisplayMetrics.DENSITY_280:
                return IMAGE_SIZE_300;
            case DisplayMetrics.DENSITY_XHIGH:
            case DisplayMetrics.DENSITY_360:
            case DisplayMetrics.DENSITY_400:
            case DisplayMetrics.DENSITY_420:
                return IMAGE_SIZE_450;
            case DisplayMetrics.DENSITY_XXHIGH:
            case DisplayMetrics.DENSITY_560:
            case DisplayMetrics.DENSITY_XXXHIGH:
                return IMAGE_SIZE_600;
        }
        return IMAGE_SIZE_300;
    }

    public static Point getScreenDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}