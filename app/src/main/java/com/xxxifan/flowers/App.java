package com.xxxifan.flowers;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.xxxifan.devbox.library.Devbox;
import com.xxxifan.flowers.net.avos.model.AVPost;

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
        AVObject.registerSubclass(AVPost.class);
        AVOSCloud.initialize(this, Keys.LC_APP_ID, Keys.LC_APP_KEY);
        AVOSCloud.setDebugLogEnabled(true);
    }
}
