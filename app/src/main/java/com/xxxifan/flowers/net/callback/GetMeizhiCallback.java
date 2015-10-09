package com.xxxifan.flowers.net.callback;

import com.xxxifan.flowers.net.model.MeizhiPost;

import java.io.IOException;
import java.util.List;

/**
 * Created by xifan on 15-10-10.
 */
public interface GetMeizhiCallback {
    void onMeizhi(List<MeizhiPost> meizhiList);

    void onError(IOException e);
}
