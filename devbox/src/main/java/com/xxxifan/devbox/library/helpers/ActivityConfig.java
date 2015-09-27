package com.xxxifan.devbox.library.helpers;

import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.WindowManager;

import com.xxxifan.devbox.library.Devbox;
import com.xxxifan.devbox.library.R;
import com.xxxifan.devbox.library.tools.Log;
import com.xxxifan.devbox.library.ui.BaseActivity;

import java.lang.ref.WeakReference;

/**
 * Visual configs for BaseActivity, set all customizations here.
 */
public class ActivityConfig {

    private WeakReference<BaseActivity> mActivity;

    private DrawerMenuClickListener mMenuClickListener;

    @ColorInt
    private int mToolbarColor;
    @LayoutRes
    private int mDrawerHeaderResId;
    private int mDrawerIconId;
    private int mDrawerMenuId;
    private int mContainerId;
    private boolean mUseToolbar;
    private boolean mIsDarkToolbar;
    private boolean mShowHomeAsUpKey;
    private boolean mIsFitSystemWindow;
    private boolean mIsDrawerLayout;

    private ActivityConfig(BaseActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

    public static ActivityConfig newInstance(BaseActivity activity) {
        ActivityConfig config = new ActivityConfig(activity);
        config.setToolbarColor(Devbox.getAppDelegate().getResources().getColor(R.color.colorPrimary));
        config.setUseToolbar(true);
        config.setIsDarkToolbar(true);
        config.setShowHomeAsUpKey(true);
        config.setTranslucentStatusBar(true);
        config.setFitSystemWindow(true);
        config.setContainerId(R.id.fragment_container);
        return config;
    }

    public int getToolbarColor() {
        return mToolbarColor;
    }

    public ActivityConfig setToolbarColor(@ColorInt int toolbarColor) {
        mToolbarColor = toolbarColor;
        return this;
    }

    public boolean useToolbar() {
        return mUseToolbar;
    }

    /**
     * configure to use toolbar, default true.
     */
    public ActivityConfig setUseToolbar(boolean useToolbar) {
        mUseToolbar = useToolbar;
        return this;
    }

    /**
     * set toolbar theme, true is dark theme and it's default, else light theme
     */
    public ActivityConfig setIsDarkToolbar(boolean isDarkToolbar) {
        mIsDarkToolbar = isDarkToolbar;
        return this;
    }

    /**
     * @return use dark theme toolbar or not
     */
    public boolean isDarkToolbar() {
        return mIsDarkToolbar;
    }

    public boolean isShowHomeAsUpKey() {
        return mShowHomeAsUpKey;
    }

    /**
     * configure to show home as up key, default true
     */
    public ActivityConfig setShowHomeAsUpKey(boolean enable) {
        mShowHomeAsUpKey = enable;
        return this;
    }

    /**
     * whether enable translucent status bar, default true
     */
    public ActivityConfig setTranslucentStatusBar(boolean enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && mActivity != null && mActivity.get()
                != null) {
            if (enable) {
                mActivity.get().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else {
                mActivity.get().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        } else if (mActivity == null || mActivity.get() == null) {
            Log.e(this, "Activity is null! translucent statusbar is not set");
        }
        return this;
    }

    /**
     * whether enable translucent nav bar, default false
     */
    public ActivityConfig setTranslucentNavBar(boolean enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && mActivity != null && mActivity.get()
                != null) {
            if (enable) {
                mActivity.get().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            } else {
                mActivity.get().getWindow().clearFlags(WindowManager.LayoutParams
                        .FLAG_TRANSLUCENT_NAVIGATION);
            }
        } else if (mActivity == null || mActivity.get() == null) {
            Log.e(this, "Activity is null! translucent navbar is not set");
        }
        return this;
    }

    public boolean isFitSystemWindow() {
        return mIsFitSystemWindow;
    }

    /**
     * @return set container layout to use fit system window, default false
     */
    public ActivityConfig setFitSystemWindow(boolean value) {
        mIsFitSystemWindow = value;
        return this;
    }

    public ActivityConfig setTheme(int resId) {
        if (mActivity != null && mActivity.get() != null) {
            mActivity.get().setTheme(resId);
        } else if (mActivity == null || mActivity.get() == null) {
            Log.e(this, "Activity is null! theme is not set");
        }
        return this;
    }

    public int getDrawerHeaderResId() {
        return mDrawerHeaderResId;
    }

    public int getDrawerMenuIconId() {
        return mDrawerIconId;
    }

    public int getDrawerMenuItemId() {
        return mDrawerMenuId;
    }

    /**
     * set root layout id with DrawerLayout and enable drawerLayout, it will enable toolbar too.
     */
    public ActivityConfig setDrawerResId(@LayoutRes int headerLayoutId, int menuIcons, int menuItems) {
        mDrawerHeaderResId = headerLayoutId;
        mDrawerIconId = menuIcons;
        mDrawerMenuId = menuItems;
        mIsDrawerLayout = true;
        useToolbar();
        return this;
    }

    public boolean isDrawerLayout() {
        return mIsDrawerLayout;
    }

    public DrawerMenuClickListener getDrawerMenuClickListener() {
        return mMenuClickListener;
    }

    public ActivityConfig setDrawerMenuClickListener(DrawerMenuClickListener listener) {
        mMenuClickListener = listener;
        return this;
    }

    /**
     * @return preset root layout id
     */
    public int getRootResId() {
        return isDrawerLayout() ? R.layout.activity_drawer : R.layout.activity_toolbar;
    }

    /**
     * @return preset fragment container id
     */
    public int getContainerId() {
        return mContainerId;
    }

    public ActivityConfig setContainerId(int resId) {
        mContainerId = resId;
        return this;
    }

    @Override
    public String toString() {
        return super.toString() + "\nmToolbarColor=" + mToolbarColor + "\nmDrawerHeaderResId" +
                "=" + mDrawerHeaderResId + "\nmUseToolbar=" + mUseToolbar + "\nmShowHomeAsUpKey=" +
                mShowHomeAsUpKey + "\nmIsFitSystemWindow=" +
                mIsFitSystemWindow;
    }

    public interface DrawerMenuClickListener {
        void onMenuClick(View v, int position);
    }
}
