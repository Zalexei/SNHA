package com.trigger.snha.helpers;

import com.trigger.snha.BuildConfig;

/**
 * Class for analytic events
 * Could be used for sending analytics + error messages
 * Currently used just for logging
 */
public class Analytics {

    /**
     * Stub method for debugging
     * @param tag
     * @param message
     */
    static public void lg(String tag, String message) {
        if(BuildConfig.DEBUG) {
            System.out.println(tag + ": " + message);
        }
    }
}
