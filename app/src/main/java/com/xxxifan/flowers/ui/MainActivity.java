package com.xxxifan.flowers.ui;

import android.view.View;

import com.xxxifan.devbox.library.helpers.ActivityConfig;
import com.xxxifan.devbox.library.ui.BaseActivity;
import com.xxxifan.flowers.R;
import com.xxxifan.flowers.ui.main.MeizhiFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected void onConfigureActivity(ActivityConfig config) {
        config.setShowHomeAsUpKey(false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_container;
    }

    @Override
    protected void initView(View rootView) {
        MeizhiFragment fragment = new MeizhiFragment();
        showFragment(fragment);
    }
}
