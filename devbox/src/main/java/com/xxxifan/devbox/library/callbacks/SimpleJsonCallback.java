package com.xxxifan.devbox.library.callbacks;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.Response;
import com.xxxifan.devbox.library.entity.BaseEntity;

import java.io.IOException;

/**
 * Created by xifan on 15-7-26.
 */
public class SimpleJsonCallback extends HttpCallback<BaseEntity> {
    @Override
    public void onResponse(Response response) throws IOException {
        BaseEntity result = JSON.parseObject(response.body().string(), BaseEntity.class);
        if (result == null) {
            postResult(null, new IOException("No available result"));
        } else {
            postResult(result, null);
        }
    }
}
