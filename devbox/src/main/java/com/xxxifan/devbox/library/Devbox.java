package com.xxxifan.devbox.library;

import android.app.Application;
import android.os.HandlerThread;

/**
 * Created by xifan on 15-7-16.
 */
public class Devbox {
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_DATA = "data";
    public static final String PREF_LAST_UPDATE = "last_update";
    public static final String PREF_READ_SET_TIP = "read_set_tip";

    private static Application sApplication;
    private static HandlerThread sWorkerThread;

    public static void install(Application application) {
        sApplication = application;
    }

    public static Application getAppDelegate() {
        if (sApplication == null) {
            throw new IllegalStateException("Application instance is null, please check you have " +
                    "correct config");
        }
        return sApplication;
    }

    public static HandlerThread getWorkerThread() {
        if (sWorkerThread == null) {
            sWorkerThread = new HandlerThread("DevBoxTask", 3);
        }
        return sWorkerThread;
    }

}
