package com.xxxifan.devbox.library.tools;

/**
 * Created by xifan on 15-7-21.
 */
public class ImageLoader {
    private static ImageLoadHandler mHandler;

    public static void setLoadHandler(ImageLoadHandler handler) {
        mHandler = handler;
    }

    public static void load(String url, Callback callback) {
        mHandler.load(url, null, callback);
    }

    public interface ImageLoadHandler {
        void load(String url, Config config, Callback callback);
    }

    public static class Config {

    }

    public static class Callback {

    }
}
