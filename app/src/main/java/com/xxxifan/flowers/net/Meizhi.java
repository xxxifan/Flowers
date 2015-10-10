package com.xxxifan.flowers.net;

import android.widget.Toast;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xxxifan.devbox.library.callbacks.http.HttpCallback;
import com.xxxifan.devbox.library.tools.HttpUtils;
import com.xxxifan.devbox.library.tools.Log;
import com.xxxifan.flowers.App;
import com.xxxifan.flowers.net.callback.GetMeizhiCallback;
import com.xxxifan.flowers.net.model.MeizhiPost;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by xifan on 15-9-29.
 */
public class Meizhi {
    public static final String MEIZITU = "http://www.meizitu.com/";
    public static final String SUFFIX_HOME_LIST = "a/list_1_%s.html";
    public static final String IMG_URL_BASE = "http://pic.meizitu.com/wp-content/uploads/";

    private Meizhi() {
    }

    public static void get(int page, final GetMeizhiCallback callback) {
        String homeUrl = MEIZITU + String.format(SUFFIX_HOME_LIST, page);
        Toast.makeText(App.get(), "loading page " + homeUrl, Toast.LENGTH_SHORT).show();
        HttpUtils.get(homeUrl, new HttpCallback<List<MeizhiPost>>() {
            @Override
            public void onResponse(Response response) throws IOException {
                String gbStr = new String(response.body().bytes(), Charset.forName("gb2312"));
                Element element = Jsoup.parse(gbStr).body();
                postResult(new TimelineParser(element).toList(), null);
            }

            @Override
            public void done(List<MeizhiPost> result, IOException e) {
                if (callback != null) {
                    if (result != null) {
                        callback.onMeizhi(result);
                    } else {
                        callback.onError(e);
                    }
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                done(null, e);

                Log.e(this, "onFailure");
            }
        });
    }
}
