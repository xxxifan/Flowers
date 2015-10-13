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
    private final int mLastPage;

    private List<MeizhiPost> newMeizhi;
    private List<MeizhiPost> cacheMeizhi;
    private boolean mIsInitial;
    private int mPage;

    public Meizhi() {
        mLastPage = AppPref.getInt(Keys.LAST_PAGE, 0);
    }

    public int getPage() {
        return mPage;
    }

    public void get(GetMeizhiCallback callback) {
        if (mPage == 0) {
            mIsInitial = true;
            mPage = mLastPage == 0 ? 1 : mLastPage;
            loadCachePage(mPage, callback);
        } else {
            mPage++;
            mIsInitial = false;
            loadPage(mPage, callback);
        }
    }

    private void loadCachePage(int page, final GetMeizhiCallback callback) {
        final int lastPostId = AppPref.getInt(Keys.LAST_POST_ID, 0);
        if (lastPostId > 0) {
            if (cacheMeizhi == null) {
                cacheMeizhi = new ArrayList<>();
                AVQuery<AVPost> query = AVQuery.getQuery(AVPost.class);
                query.setCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK)
                        .orderByDescending(Keys.POST_ID)
                        .skip((page - 1) * PAGE_SIZE)
                        .limit(PAGE_SIZE);
                query.findInBackground(new FindCallback<AVPost>() {
                    @Override
                    public void done(List<AVPost> list, AVException e) {
                        if (e == null) {
                            MeizhiPost meizhi;
                            for (int i = 0; i < list.size(); i++) {
                                meizhi = list.get(i).toMeizhiPost();
                                if (lastPostId <= 0 || meizhi.getPostId() <= lastPostId) {
                                    cacheMeizhi.add(meizhi);
                                }
                            }

                            if (!cacheMeizhi.isEmpty()) {
                                if (callback != null) {
                                    callback.onMeizhi(cacheMeizhi);
                                }
                            } else {
                                mIsInitial = false;
                                loadPage(1, callback);
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
        } else {
            mIsInitial = false;
            loadPage(1, callback);
        }
    }

    private void loadPage(final int page, final GetMeizhiCallback callback) {
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
                        int cacheIndex = list.size() - 1;
                        MeizhiPost latestMeizhi = posts.get(postIndex);
                        AVPost cachePost;
                        while (cacheIndex >= 0) {
                            cachePost = list.get(cacheIndex);
                            while (cachePost.getPostId() > latestMeizhi.getPostId()) {
                                if (postIndex >= 1) {
                                    latestMeizhi = posts.get(--postIndex);
                                }
                            }
                            if (cachePost.getPostId() >= latestMeizhi.getPostId()) {
                                cacheIndex--;
                            } else if (cachePost.getPostId() < latestMeizhi.getPostId()) {
                                if (mIsInitial) {
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
                                    if (mIsInitial) {
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
            }
        });
    }

    public void getNewest(GetMeizhiCallback callback) {
        if (callback != null) {
            callback.onMeizhi(newMeizhi);
        }
    }
}
