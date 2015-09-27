package com.xxxifan.devbox.library.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.xxxifan.devbox.library.R;
import com.xxxifan.devbox.library.adapter.DrawerAdapter;
import com.xxxifan.devbox.library.entity.CustomEvent;
import com.xxxifan.devbox.library.helpers.ActivityConfig;
import com.xxxifan.devbox.library.helpers.SystemBarTintManager;
import com.xxxifan.devbox.library.tools.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/5/6.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;

    private ActivityConfig mConfig;
    private SystemBarTintManager mSystemBarManager;
    private List<UiController> mUiControllers;
    private DrawerLayout mDrawerLayout;

    /**
     * get ActivityConfig, for visual configs, call it before super.onCreate()
     */
    protected ActivityConfig getConfig() {
        if (mConfig == null) {
            mConfig = ActivityConfig.newInstance(this);
        }
        return mConfig;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        onConfigureActivity(getConfig());
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(layoutResID, getConfig());
    }

    protected void setContentView(int layoutResID, ActivityConfig config) {
        if (config.useToolbar()) {
            // set root layout
            super.setContentView(config.getRootResId());

            View containerView = findViewById(R.id.toolbar_container);
            if (containerView == null) {
                throw new IllegalStateException("Cannot find toolbar_container");
            }
            containerView.setFitsSystemWindows(config.isFitSystemWindow());

            // attach user layout
            View view = getLayoutInflater().inflate(layoutResID, null, false);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -1);
            params.topMargin = getResources().getDimensionPixelSize(R.dimen.toolbar_height);
            ((FrameLayout) containerView).addView(view, 0, params);

            // setup toolbar if needed
            ViewStub toolbarStub = ButterKnife.findById(this, R.id.toolbar_stub);
            if (toolbarStub != null) {
                toolbarStub.setLayoutResource(config.isDarkToolbar() ? R.layout.view_toolbar_dark
                        : R.layout.view_toolbar_light);
                View toolbarStubView = toolbarStub.inflate();
                if (toolbarStubView != null) {
                    Toolbar toolbar = (Toolbar) toolbarStubView;
                    setupToolbar(toolbar);
                    // setup drawer layout if needed, called before initView avoid of NPE
                    if (config.isDrawerLayout()) {
                        setupDrawerLayout();
                    }
                }
            }
        } else {
            super.setContentView(layoutResID);
        }

        initView(getWindow().getDecorView());
    }

    private void setupDrawerLayout() {
        mDrawerLayout = ButterKnife.findById(this, R.id.drawer_layout);
        if (mDrawerLayout == null) {
            Log.e(this, "Cannot find DrawerLayout!");
            return;
        }

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0);
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        View headerView = getLayoutInflater().inflate(getConfig().getDrawerHeaderResId(), null);
        ListView drawerListView = ButterKnife.findById(this, R.id.drawer_item_list);
        setDrawerAdapter(drawerListView, headerView);
    }

    /**
     * setup drawer item list. You can override it to use a custom adapter.
     */
    protected void setDrawerAdapter(ListView drawerListView, View headerView) {
        final DrawerAdapter drawerAdapter = new DrawerAdapter(drawerListView, getConfig());
        drawerListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        drawerListView.setDivider(new ColorDrawable(getResources().getColor(R.color.transparent)));
        drawerListView.setDividerHeight(0);
        drawerListView.setBackgroundColor(getResources().getColor(R.color.white));
        drawerListView.setCacheColorHint(Color.TRANSPARENT);
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private int lastCheckPosition;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                if (listView.getHeaderViewsCount() > 0) {
                    position--; // fix wrong pos.
                }
                listView.getCheckedItemPosition();
                if (view.getId() != R.id.drawer_divider && lastCheckPosition != position) {
                    listView.setItemChecked(position, true);
                    lastCheckPosition = position;

                    if (getConfig().getDrawerMenuClickListener() != null) {
                        getConfig().getDrawerMenuClickListener().onMenuClick(view, position);
                    }
                }
            }
        });
        drawerListView.addHeaderView(headerView, null, false);
        drawerListView.setAdapter(drawerAdapter);
        drawerListView.setItemChecked(0, true);
    }

    /**
     * setup toolbar
     */
    protected void setupToolbar(@NonNull Toolbar toolbar) {
        ActivityConfig config = getConfig();
        toolbar.setBackgroundColor(config.getToolbarColor());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(getConfig().isShowHomeAsUpKey());
        }

        // set compat status color in kitkat or later devices
        if (config.isFitSystemWindow() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (mSystemBarManager == null) {
                mSystemBarManager = new SystemBarTintManager(this);
            }
            mSystemBarManager.setStatusBarTintEnabled(true);
            mSystemBarManager.setTintColor(config.getToolbarColor());
        }
    }

    /**
     * set fragment will be attach to this activity right now
     */
    protected void showFragment(Fragment fragment) {
        showFragment(fragment, getConfig().getContainerId());
    }

    /**
     * set fragment will be attach to this activity right now
     *
     * @param containerId the target containerId will be attached to.
     */
    protected void showFragment(Fragment fragment, @IdRes int containerId) {
        // get tag name
        String tag = fragment.getTag();
        if (tag == null) {
            tag = fragment.getClass().getSimpleName();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null && fragmentList.size() > 0) {
            boolean hasFragment = false;
            // hide other fragment and check if fragment is exist
            for (Fragment oldFragment : fragmentList) {
                if (oldFragment.isVisible()) {
                    transaction.hide(oldFragment);
                }
                if (tag.equals(oldFragment.getTag())) {
                    hasFragment = true;
                }
            }

            // if this fragment is not exist int manager, add it
            if (!hasFragment) {
                transaction.add(containerId, fragment, tag);
            }

            transaction.show(fragment);
        } else {
            transaction.add(containerId, fragment, tag);
            transaction.show(fragment);
        }

        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getConfig().isDrawerLayout() && mDrawerLayout != null) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            } else {
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mUiControllers != null && mUiControllers.size() > 0) {
            for (int i = 0; i < mUiControllers.size(); i++) {
                mUiControllers.get(i).onResume();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mUiControllers != null && mUiControllers.size() > 0) {
            for (int i = 0; i < mUiControllers.size(); i++) {
                mUiControllers.get(i).onPause();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unregister ui controllers
        if (mUiControllers != null && !mUiControllers.isEmpty()) {
            for (int i = 0; i < mUiControllers.size(); i++) {
                mUiControllers.get(i).onDestroy();
            }
            mUiControllers.clear();
            mUiControllers = null;
        }
    }

    /**
     * register controllers, so that BaseActivity can do some work automatically
     */
    protected void registerUiController(UiController controller) {
        if (mUiControllers == null) {
            mUiControllers = new ArrayList<>();
        }
        mUiControllers.add(controller);
    }

    protected void hideToolBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    protected void showToolBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }

    protected void postEvent(CustomEvent event, Class target) {
        EventBus.getDefault().post(event);
    }

    protected void postStickyEvent(CustomEvent event, Class target) {
        EventBus.getDefault().postSticky(event);
    }

    protected Context getContext() {
        return mContext;
    }

    /**
     * Set ActivityConfig, called before super.onCreate()
     */
    protected abstract void onConfigureActivity(ActivityConfig config);

    /**
     * @return activity layout id, called while setContentView()
     */
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * @param rootView the root of user layout
     */
    protected abstract void initView(View rootView);
}
