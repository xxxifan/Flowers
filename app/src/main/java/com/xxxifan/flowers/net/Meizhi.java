package com.xxxifan.flowers.net;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xxxifan.devbox.library.AppPref;
import com.xxxifan.devbox.library.callbacks.http.HttpCallback;
import com.xxxifan.devbox.library.tools.HttpUtils;
import com.xxxifan.devbox.library.tools.Log;
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
import java.util.Collections;
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
    // last cached page
    private final int mLastPage;
    private final int mLastPostId;
    private List<MeizhiPost> newMeizhi;
    private boolean mIsInitial;
    private int mPage;

    public Meizhi() {
        mLastPage = AppPref.getInt(Keys.LAST_PAGE, 0);
        mLastPostId = AppPref.getInt(Keys.LAST_POST_ID, 0);
    }

    public void get(GetMeizhiCallback callback) {
        if (mPage == 0) {
            mIsInitial = true;
            mPage = mLastPage == 0 ? 1 : mLastPage;
            loadPage(mPage, callback);
        } else {
            mPage++;
            mIsInitial = false;
            loadPage(mPage, callback);
        }
    }


    public void loadNewMeizhi(GetMeizhiCallback callback) {
        if (callback != null) {
            callback.onMeizhi(newMeizhi);
        }
    }

    private void loadPage(int page, final GetMeizhiCallback callback) {
        // trim by lastPostId
        AVQuery<AVPost> query = AVQuery.getQuery(AVPost.class);
        query.setCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK)
                .orderByDescending(Keys.POST_ID)
                .skip((page - 1) * PAGE_SIZE)
                .limit(PAGE_SIZE)
                .findInBackground(new FindCallback<AVPost>() {
                    @Override
                    public void done(List<AVPost> list, AVException e) {
                        if (e == null) {
                            List<MeizhiPost> cacheMeizhi = new ArrayList<>();
                            MeizhiPost meizhi;
                            for (int i = 0; i < list.size(); i++) {
                                meizhi = list.get(i).toMeizhiPost();
                                // no last post or older than last post
                                if (mLastPostId <= 0 || meizhi.getPostId() <= mLastPostId) {
                                    cacheMeizhi.add(meizhi);
                                }
                            }

                            if (!cacheMeizhi.isEmpty()) {
                                if (callback != null) {
                                    callback.onMeizhi(cacheMeizhi);
                                }
                            } else {
                                mIsInitial = false;
                                loadServerPage(1, callback);
                            }

                            if (mIsInitial) {
                                fetchNewMeizhi();
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

    private void loadServerPage(final int page, final GetMeizhiCallback callback) {
        String homeUrl = MEIZITU + String.format(SUFFIX_HOME_LIST, page);
        HttpUtils.get(homeUrl, new HttpCallback<List<MeizhiPost>>() {
            @Override
            public void onResponse(Response response) throws IOException {
                String gbStr = new String(response.body().bytes(), Charset.forName("gb2312"));
                Element element = Jsoup.parse(gbStr).body();
                List<MeizhiPost> posts = new TimelineParser(element).toList();
                if (!mIsInitial && callback != null) {
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

    private void fetchNewMeizhi() {
        HttpUtils.get(MEIZITU, new HttpCallback<List<MeizhiPost>>() {
            @Override
            public void onResponse(Response response) throws IOException {
                String gbStr = new String(response.body().bytes(), Charset.forName("gb2312"));
                Element element = Jsoup.parse(gbStr).body();
                List<MeizhiPost> posts = new HomeParser(element).toList();
                syncLeanCloud(1, posts);
            }

            @Override
            public void onFailure(Request request, IOException e) {
                postResult(null, e);
                Log.e(this, "onFailure");
            }
        });
    }

    private void syncLeanCloud(final int page, final List<MeizhiPost> posts) {
        if (posts == null || posts.size() < 1) {
            return;
        }
        AVQuery<AVPost> query = AVQuery.getQuery(AVPost.class);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE)
                .orderByDescending(Keys.POST_ID)
                .skip((page - 1) * PAGE_SIZE)
                .limit(PAGE_SIZE);
        query.findInBackground(new FindCallback<AVPost>() {
            @Override
            public void done(List<AVPost> list, AVException e) {
                if (e == null) {
                    int cacheSize = list.size();
                    if (cacheSize > 0) {
                        // loop start from oldest post
                        int postIndex = posts.size() - 1;
                        int cacheIndex = 0;
                        MeizhiPost latestMeizhi = posts.get(postIndex);
                        AVPost cachePost;
                        while (cacheIndex < cacheSize) {
                            cachePost = list.get(cacheIndex);
                            // find latest Meizhi, once it newer than cache posts, loop end.
                            while (cachePost.getPostId() >= latestMeizhi.getPostId()) {
                                if (postIndex >= 1) {
                                    latestMeizhi = posts.get(--postIndex);
                                } else {
                                    break;
                                }
                            }

                            // if not found, then no newer posts, else new posts start from here.
                            if (cachePost.getPostId() >= latestMeizhi.getPostId()) {
                                break;
                            } else if (cachePost.getPostId() < latestMeizhi.getPostId()) {
                                if (mIsInitial) {
                                    if (newMeizhi == null) {
                                        newMeizhi = new ArrayList<>();
                                    } else {
                                        newMeizhi.clear();
                                    }

                                    EventBus.getDefault().post(new NewPostsEvent());
                                }

                                MeizhiPost post;
                                for (int ni = postIndex; ni >= 0; ni--) {
                                    post = posts.get(ni);
                                    if (mIsInitial) {
                                        newMeizhi.add(post);
                                    }
                                    AVPost.fromMeizhiPost(post).saveInBackground();
                                }
                                Collections.sort(newMeizhi);
                                break;
                            }
                        }
                    } else {
                        MeizhiPost post;
                        for (int i = 0; i < posts.size(); i++) {
                            post = posts.get(i);
                            AVPost.fromMeizhiPost(post).saveInBackground();
                        }
                        newMeizhi = posts;
                        EventBus.getDefault().post(new NewPostsEvent());
                    }
                } else {
                    HttpUtils.onAvosException(e.getLocalizedMessage());
                }
            }
        });
    }

    public int getPage() {
        return mPage;
    }

}
