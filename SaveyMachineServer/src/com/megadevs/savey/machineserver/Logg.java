package com.megadevs.savey.machineserver;

import android.util.Log;

public class Logg {

    private static String TAG = "Logg";
    private static boolean ENABLED = true;

    public static void setTag(String tag) {
        TAG = tag;
    }

    public static void setEnabled(boolean enabled) {
        ENABLED = enabled;
    }

    public static void v(String message, Object... args) {
        if (ENABLED) {
            Log.v(TAG, String.format(message, args));
        }
    }

    public static void d(String message, Object... args) {
        if (ENABLED) {
            Log.d(TAG, String.format(message, args));
        }
    }

    public static void i(String message, Object... args) {
        if (ENABLED) {
            Log.i(TAG, String.format(message, args));
        }
    }

    public static void w(String message, Object... args) {
        if (ENABLED) {
            Log.w(TAG, String.format(message, args));
        }
    }

    public static void e(String message, Object... args) {
        if (ENABLED) {
            Log.e(TAG, String.format(message, args));
        }
    }

}
