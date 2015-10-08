package com.xxxifan.flowers.net;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xxxifan.devbox.library.tools.HttpUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by xifan on 15-9-29.
 */
public class Meizhi {
    public static final String MEIZITU = "http://www.meizitu.com/";
    public static final String IMG_URL_BASE = "http://pic.meizitu.com/wp-content/uploads/";

    private Meizhi(){}

    public static void get() {
        HttpUtils.get(MEIZITU, new Callback() {

            @Override
            public void onResponse(Response response) throws IOException {
                String gbStr = new String(response.body().bytes(), Charset.forName("gb2312"));
                Element element = Jsoup.parse(gbStr).body();
                new TimelineParser(element).toList();
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("","onFailure");
            }
        });
    }
}
