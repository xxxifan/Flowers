package com.xxxifan.flowers.ui.main;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xxxifan.devbox.library.ui.BaseFragment;
import com.xxxifan.flowers.R;
import com.xxxifan.flowers.net.Meizhi;
import com.xxxifan.flowers.net.callback.GetMeizhiCallback;
import com.xxxifan.flowers.net.model.MeizhiPost;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xifan on 15-9-28.
 */
public class MeizhiFragment extends BaseFragment {

    @Bind(R.id.meizhi_view)
    ImageView meizhiView;
    private List<MeizhiPost> mMeizhi;
    private int count;
    private int page = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_meizhi;
    }

    @Override
    protected void initView(View rootView) {
        ButterKnife.bind(this, rootView);
        meizhiView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMeizhi != null) {
                    if (count < mMeizhi.size() - 1) {
                        count++;
                        Glide.with(getContext()).load(mMeizhi.get(count).coverUrl).into(meizhiView);
                    } else {
                        Meizhi.get(++page, new MeizhiPageCallback());
                    }
                }
            }
        });
    }

    @Override
    protected boolean onDataLoad() {
        Meizhi.get(page, new MeizhiPageCallback());
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private class MeizhiPageCallback implements GetMeizhiCallback {

        @Override
        public void onMeizhi(List<MeizhiPost> meizhiList) {
            if (mMeizhi == null) {
                mMeizhi = meizhiList;
            } else {
                mMeizhi.addAll(meizhiList);
            }
            MeizhiPost post = meizhiList.get(count);
            Glide.with(getContext()).load(post.coverUrl).into(meizhiView);
        }

        @Override
        public void onError(IOException e) {
            e.printStackTrace();
        }
    }
}
