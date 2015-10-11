package com.xxxifan.flowers.ui.main;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xxxifan.devbox.library.ui.BaseFragment;
import com.xxxifan.flowers.App;
import com.xxxifan.flowers.R;
import com.xxxifan.flowers.net.Meizhi;
import com.xxxifan.flowers.net.callback.GetMeizhiCallback;
import com.xxxifan.flowers.net.model.MeizhiPost;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xifan on 15-9-28.
 */
public class MeizhiFragment extends BaseFragment {

    @Bind(R.id.meizhi_view)
    ImageView mMeizhiView;
    @Bind(R.id.meizhi_title)
    TextView mTitleText;
    @Bind(R.id.meizhi_like)
    Button mLikeBtn;
    @Bind(R.id.meizhi_more)
    Button mMoreBtn;
    @Bind(R.id.meizhi_unlike)
    Button nextBtn;

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

    @OnClick(R.id.meizhi_like)
    public void onLikeClick(View view) {
        // TODO: 15-10-11 mark as like
        nextMeizhi();
    }

    @OnClick(R.id.meizhi_more)
    public void onMoreClick(View view) {
        // TODO: 15-10-11 mark as like and show more
    }

    @OnClick(R.id.meizhi_unlike)
    public void onUnlikeClick(View view) {
        // TODO: 15-10-11 mark as unlike
        nextMeizhi();
    }

    private void nextMeizhi() {
        if (mMeizhi != null) {
            if (count < mMeizhi.size() - 1) {
                count++;
                setupMeizhi(mMeizhi.get(count));
            } else {
                Meizhi.get(++page, new MeizhiPageCallback());
            }
        }
    }

    private void setupMeizhi(MeizhiPost post) {
        Toast.makeText(App.get(), "loading meizhi" + mMeizhi.get(count).coverUrl, Toast.LENGTH_SHORT).show();
        Glide.with(getContext()).load(post.coverUrl).into(mMeizhiView);
        mTitleText.setText(mMeizhi.get(count).titile);
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
            setupMeizhi(post);
        }

        @Override
        public void onError(IOException e) {
            e.printStackTrace();
        }
    }
}
