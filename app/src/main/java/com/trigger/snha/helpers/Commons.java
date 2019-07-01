package com.trigger.snha.helpers;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Helper class
 */
public class Commons {
    public static int dpToPx(float dp, Context context){
        return (int) (dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
