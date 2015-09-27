package com.xxxifan.devbox.library.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.xxxifan.devbox.library.callbacks.CommandCallback;
import com.xxxifan.devbox.library.callbacks.http.ImageDownloadCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by xifan on 15-7-22.
 */
public class IOUtils {
    public static boolean saveToDisk(InputStream source, File targetFile) {
        return saveToDisk(Okio.buffer(Okio.source(source)), targetFile);
    }

    public static boolean saveToDisk(BufferedSource source, File targetFile) {
        BufferedSink sink = null;
        try {
            sink = Okio.buffer(Okio.sink(targetFile));
            source.readAll(sink);
            sink.emit();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (sink != null) {
                try {
                    sink.close();
                } catch (IOException ignore) {
                }
            }
            if (source != null) {
                try {
                    source.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void loadBitmap(ImageDownloadCallback callback) {
        callback.onLoadImage();
    }

    public static File saveBitmapJPG(Bitmap bitmap, String filename) {
        return saveBitmap(bitmap, Utils.getTempFile(filename), Bitmap.CompressFormat.JPEG);
    }

    public static File saveBitmapPNG(Bitmap bitmap, String filename) {
        return saveBitmap(bitmap, Utils.getTempFile(filename), Bitmap.CompressFormat.PNG);
    }

    public static File saveBitmap(Bitmap bitmap, File target, Bitmap.CompressFormat format) {
        if (bitmap != null && target != null) {
            try {
                if (target.exists()) {
                    target.delete();
                }

                FileOutputStream stream = new FileOutputStream(target);
                bitmap.compress(format, 90, stream);
                stream.flush();
                stream.close();
                bitmap.recycle();
                return target;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static void runCmd(String[] cmd, CommandCallback callback) {
        Process p;
        String result;
        try {
            p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
            BufferedSource source = Okio.buffer(Okio.source(p.getInputStream()));
            result = source.readUtf8().trim();
            if (callback != null) {
                callback.done(result, null);
            }
            p.destroy();
            source.close();
        } catch (IOException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.done(null, e);
            }
        }
    }
}
