package com.xxxifan.flowers.net;

import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xxxifan.devbox.library.AppPref;
import com.xxxifan.devbox.library.callbacks.http.HttpCallback;
import com.xxxifan.devbox.library.tools.HttpUtils;
import com.xxxifan.devbox.library.tools.Log;
import com.xxxifan.flowers.App;
import com.xxxifan.flowers.Keys;
import com.xxxifan.flowers.event.NewPostsEvent;
import com.xxxifan.flowers.net.avos.model.AVPost;
import com.xxxifan.flowers.net.callback.GetMeizhiCallback;
import com.xxxifan.flowers.net.model.MeizhiPost;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by xifan on 15-9-29.
 */
public class Meizhi {
    public static final String MEIZITU = "http://www.meizitu.com/";
    public static final String SUFFIX_HOME_LIST = "a/list_1_%s.html";
    public static final String IMG_URL_BASE = "http://pic.meizitu.com/wp-content/uploads/";

    private static final int PAGE_SIZE = 30;

    private List<MeizhiPost> newMeizhi;
    private boolean noCache;

    public void get(final int page, final GetMeizhiCallback callback) {
        noCache = true;
        if (page == 1) {
            int lastPostId = AppPref.getInt(Keys.LAST_POST_ID, 0);
            // If have last id, load from cache and start from it.
            if (lastPostId > 0) {
                Log.e(this, "loading last post" + lastPostId);
                noCache = false;
                AVQuery<AVPost> query = AVQuery.getQuery(AVPost.class);
                query.setCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK)
                        .orderByDescending(Keys.POST_ID)
                        .whereLessThanOrEqualTo(Keys.POST_ID, lastPostId)
                        .limit(PAGE_SIZE);
                query.findInBackground(new FindCallback<AVPost>() {
                    @Override
                    public void done(List<AVPost> list, AVException e) {
                        if (e == null) {
                            List<MeizhiPost> meizhiList = new ArrayList<>();
                            for (int i = 0; i < list.size(); i++) {
                                meizhiList.add(list.get(i).toMeizhiPost());
                            }
                            if (callback != null) {
                                callback.onMeizhi(meizhiList);
                            }
                        } else {
                            HttpUtils.onAvosException(e.getLocalizedMessage());
                            if (callback != null) {
                                callback.onError(e);
                            }
                        }
                    }
                });
            }
        }
        // finally read from server to update posts.
        String homeUrl = MEIZITU + String.format(SUFFIX_HOME_LIST, page);
        Toast.makeText(App.get(), "loading page " + homeUrl, Toast.LENGTH_SHORT).show();
        HttpUtils.get(homeUrl, new HttpCallback<List<MeizhiPost>>() {
            @Override
            public void onResponse(Response response) throws IOException {
                String gbStr = new String(response.body().bytes(), Charset.forName("gb2312"));
                Element element = Jsoup.parse(gbStr).body();
                List<MeizhiPost> posts = new TimelineParser(element).toList();
                if (noCache && callback != null) {
                    postResult(posts, null);
                }
                // sync latest page to leancloud
                syncLeanCloud(page, posts);
            }

            @Override
            public void done(List<MeizhiPost> result, IOException e) {
                super.done(result, e);
                if (result != null && callback != null) {
                    callback.onMeizhi(result);
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                postResult(null, e);
                Log.e(this, "onFailure");
            }
        });

    }

    private void syncLeanCloud(final int page, final List<MeizhiPost> posts) {
        AVQuery<AVPost> query = AVQuery.getQuery(AVPost.class);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE)
                .orderByDescending(Keys.POST_ID)
                .skip((page - 1) * PAGE_SIZE)
                .limit(PAGE_SIZE);
        query.findInBackground(new FindCallback<AVPost>() {
            @Override
            public void done(List<AVPost> list, AVException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        int postIndex = posts.size() - 1;
                        int currentIndex = AppPref.getInt(Keys.LAST_POST_ID, 0);
                        if (currentIndex < 0) {
                            currentIndex = 0;
                        }
                        MeizhiPost lastNewMeizhi = posts.get(postIndex);
                        for (int i = currentIndex; i < list.size(); i++) {
                            while (list.get(i).getPostId() > lastNewMeizhi.getPostId()) {
                                if (postIndex >= 1) {
                                    lastNewMeizhi = posts.get(--postIndex);
                                }
                            }

                            // save newer in Leancloud
                            if (list.get(i).getPostId() < lastNewMeizhi.getPostId()) {
                                if (page == 1) {
                                    EventBus.getDefault().post(new NewPostsEvent(postIndex));
                                    if (newMeizhi == null) {
                                        newMeizhi = new ArrayList<>();
                                    } else {
                                        newMeizhi.clear();
                                    }
                                }

                                MeizhiPost post;
                                for (int ni = postIndex; ni >= 0; ni--) {
                                    post = posts.get(ni);
                                    if (page == 1) {
                                        newMeizhi.add(post);
                                    }
                                    AVPost.fromMeizhiPost(post).saveInBackground();
                                }
                                break;
                            }
                        }
                    } else {
                        MeizhiPost post;
                        for (int i = 0; i < posts.size(); i++) {
                            post = posts.get(i);
                            AVPost.fromMeizhiPost(post).saveInBackground();
                        }
                    }
                } else {
                    HttpUtils.onAvosException(e.getLocalizedMessage());
                }

                posts.clear();
            }
        });
    }

    public void getNewest(GetMeizhiCallback callback) {
        if (callback != null) {
            callback.onMeizhi(newMeizhi);
        }
    }
}
