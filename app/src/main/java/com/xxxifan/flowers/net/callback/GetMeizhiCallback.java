package com.xxxifan.flowers.net.callback;

import com.xxxifan.flowers.net.model.MeizhiPost;

import java.util.List;

/**
 * Created by xifan on 15-10-10.
 */
public interface GetMeizhiCallback {
    void onMeizhi(List<MeizhiPost> meizhiList);

    void onError(Exception e);
}
