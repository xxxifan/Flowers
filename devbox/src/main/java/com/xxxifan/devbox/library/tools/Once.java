package com.xxxifan.devbox.library.tools;

import android.content.SharedPreferences;

import com.xxxifan.devbox.library.AppPref;

/**
 * Idea from https://github.com/drakeet/Meizhi/blob/master/app/src/main/java/me/drakeet/meizhi/util/Once.java
 * Created by xifan on 15-8-23.
 */
public class Once {

    private Once() {
    }

    public static void check(String key, OnceCallback callback) {
        if (callback != null) {
            SharedPreferences pref = AppPref.getPrefs("once");
            if (!pref.getBoolean(key, false)) {
                callback.onOnce();
                pref.edit().putBoolean(key, true).apply();
            }
        }
    }

    public interface OnceCallback {
        void onOnce();
    }
}
