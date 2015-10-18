package com.xxxifan.flowers.ui.main;

import android.view.View;
import android.widget.ImageView;

import com.xxxifan.devbox.library.helpers.ActivityConfig;
import com.xxxifan.devbox.library.ui.BaseActivity;
import com.xxxifan.flowers.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MeizhiViewActivity extends BaseActivity {

    @Bind(R.id.meizhi_view)
    ImageView mMeizhiView;

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
        ButterKnife.bind(this);
//        if (Utils.isLollipop()) {
//            mMeizhiView.setTransitionName(Keys.TRANSITION_MEIZHI);
//        }
    }

}
