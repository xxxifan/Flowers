package com.xxxifan.flowers;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.xxxifan.devbox.library.Devbox;

/**
 * Created by xifan on 15-9-28.
 */
public class App extends Application {
    private static App sApp;

    public static App get() {
        return sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;

        Devbox.install(this);
        initLeanCloud();
    }

    private void initLeanCloud() {
        AVOSCloud.initialize(this, Keys.LC_APP_ID, Keys.LC_APP_KEY);
        AVOSCloud.setDebugLogEnabled(true);
    }
}
