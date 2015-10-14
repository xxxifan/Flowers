package com.xxxifan.flowers.ui.main;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;

import com.xxxifan.devbox.library.helpers.ActivityConfig;
import com.xxxifan.devbox.library.ui.BaseActivity;
import com.xxxifan.flowers.R;

public class MeizhiViewActivity extends BaseActivity {

    @Override
    protected void onConfigureActivity(ActivityConfig config) {
        config.setUseToolbar(false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_meizhi_view;
    }

    @Override
    protected void initView(View rootView) {

    }

}
