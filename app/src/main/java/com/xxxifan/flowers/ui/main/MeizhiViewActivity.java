package com.xxxifan.flowers.ui.main;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xxxifan.devbox.library.helpers.ActivityConfig;
import com.xxxifan.devbox.library.ui.BaseActivity;
import com.xxxifan.flowers.Keys;
import com.xxxifan.flowers.R;
import com.xxxifan.flowers.net.model.MeizhiPost;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MeizhiViewActivity extends BaseActivity {

    @Bind(R.id.meizhi_view)
    ImageView mMeizhiView;

    private MeizhiPost mMeizhiPost;

    @Override
    protected void onConfigureActivity(ActivityConfig config) {
        config.setUseToolbar(false).setTranslucentNavBar(true);
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

        mMeizhiPost = getIntent().getParcelableExtra(Keys.EXTRA_MEIZHI);
        Glide.with(this).load(mMeizhiPost.coverUrl).into(mMeizhiView);
    }

}
