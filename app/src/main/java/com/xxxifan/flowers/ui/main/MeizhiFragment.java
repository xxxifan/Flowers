package com.xxxifan.flowers.ui.main;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xxxifan.devbox.library.AppPref;
import com.xxxifan.devbox.library.tools.Utils;
import com.xxxifan.devbox.library.ui.BaseFragment;
import com.xxxifan.flowers.Keys;
import com.xxxifan.flowers.R;
import com.xxxifan.flowers.event.NewPostsEvent;
import com.xxxifan.flowers.net.Meizhi;
import com.xxxifan.flowers.net.callback.GetMeizhiCallback;
import com.xxxifan.flowers.net.model.MeizhiPost;

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

    @Bind(R.id.meizhi_intro)
    TextView mIntroText;

    private Meizhi mMeizhi;
    private List<MeizhiPost> mPosts;
    private int mCount;
    private boolean mIsLoadingPage;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_meizhi;
    }

    @Override
    protected void initView(View rootView) {
        ButterKnife.bind(this, rootView);
        if (Utils.isLollipop()) {
            mMeizhiView.setTransitionName(Keys.TRANSITION_MEIZHI);
        }
        mMeizhi = new Meizhi();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerEventBus(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterEventBus(this);
    }

    @Override
    protected boolean onDataLoad() {
        setIsLoadingPage(true);
        mMeizhi.get(new MeizhiPageCallback());
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPosts != null) {
            MeizhiPost lastPost = mPosts.get(mCount);
            if (lastPost != null) {
                AppPref.putInt(Keys.LAST_POST_ID, lastPost.getPostId());
            }
            if (mMeizhi != null) {
                AppPref.putInt(Keys.LAST_PAGE, mMeizhi.getPage());
            }
        }
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.meizhi_like)
    public void onLikeClick(View view) {
        // TODO: 15-10-11 mark as like
        nextMeizhi();
    }

    @OnClick(R.id.meizhi_more)
    public void onMoreClick(View view) {
        Intent intent = new Intent(getContext(), MeizhiViewActivity.class);
        intent.putExtra(Keys.EXTRA_MEIZHI, mPosts.get(mCount));
//        if (Utils.isLollipop()) {
//            ActivityOptions options =
//                    ActivityOptions.makeSceneTransitionAnimation(getActivity(), mMeizhiView,
//                            Keys.TRANSITION_MEIZHI);
//            getActivity().startActivity(intent, options.toBundle());
//        } else {
            startActivity(intent);
//        }
    }

    @OnClick(R.id.meizhi_unlike)
    public void onUnlikeClick(View view) {
        // TODO: 15-10-11 mark as unlike
        nextMeizhi();
    }

    public void onEventMainThread(NewPostsEvent event) {
        Snackbar.make(mTitleText, getString(R.string.tip_new_flowers_ready, event.num), Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_view, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mMeizhi == null) {
                            mMeizhi = new Meizhi();
                        }
                        mMeizhi.getNewest(new MeizhiNewestCallback());
                    }
                })
                .show();
    }

    private void nextMeizhi() {
        if (mPosts != null && !isLoadingPage()) {
            if (mCount < mPosts.size() - 1) {
                mCount++;
                setupMeizhi(mPosts.get(mCount));
            } else {
                setIsLoadingPage(true);
                mMeizhi.get(new MeizhiPageCallback());
                Toast.makeText(getContext(), R.string.msg_wait_page, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupMeizhi(MeizhiPost post) {
        mIntroText.setText(mPosts.get(mCount).coverUrl);
        Glide.with(getContext()).load(post.coverUrl).into(mMeizhiView);
        mTitleText.setText(mPosts.get(mCount).title);
    }

    private boolean isLoadingPage() {
        return mIsLoadingPage;
    }

    private void setIsLoadingPage(boolean loading) {
        mIsLoadingPage = loading;
    }

    private class MeizhiPageCallback implements GetMeizhiCallback {

        @Override
        public void onMeizhi(List<MeizhiPost> meizhiList) {
            setIsLoadingPage(false);

            if (mPosts != null) {
                mPosts.clear();
            }
            mPosts = meizhiList;

            mCount = 0;
            MeizhiPost post = meizhiList.get(mCount);
            setupMeizhi(post);
        }

        @Override
        public void onError(Exception e) {
            e.printStackTrace();
        }
    }

    private class MeizhiNewestCallback implements GetMeizhiCallback {

        @Override
        public void onMeizhi(List<MeizhiPost> meizhiList) {
            setIsLoadingPage(false);

            if (meizhiList != null) {
                if (mPosts == null) {
                    mPosts = meizhiList;
                } else {
                    mPosts.addAll(0, meizhiList);
                }

                mCount = 0;
                MeizhiPost post = meizhiList.get(mCount);
                setupMeizhi(post);
            }
        }

        @Override
        public void onError(Exception e) {
            e.printStackTrace();
        }
    }
}
