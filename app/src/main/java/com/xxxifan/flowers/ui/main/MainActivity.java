package com.xxxifan.flowers.ui.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.xxxifan.devbox.library.helpers.ActivityConfig;
import com.xxxifan.devbox.library.ui.BasePagerActivity;
import com.xxxifan.flowers.net.Meizhi;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BasePagerActivity {

    @Override
    protected void onConfigureActivity(ActivityConfig config) {
        config.setShowHomeAsUpKey(false);
    }

    @Override
    protected List<Fragment> initFragments(Bundle savedInstanceState) {
        List<Fragment> fragments = super.initFragments(savedInstanceState);
        if (fragments == null) {
            fragments = new ArrayList<>();

            Meizhi.get(1);
        }
        return fragments;
    }

    @Override
    protected void setupViewPager(List<Fragment> fragments) {
        super.setupViewPager(fragments);
        setPagerStripEnabled(false);
    }
}
