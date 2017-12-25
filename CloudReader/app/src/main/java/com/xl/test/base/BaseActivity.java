package com.xl.test.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xl.test.R;
import com.xl.test.databinding.ActivityBaseBinding;
import com.xl.test.utils.CommonUtils;
import com.xl.test.utils.PerfectClickListener;
import com.xl.test.view.webView.statusbar.StatusBarUtil;

public abstract class BaseActivity<SV extends ViewDataBinding> extends AppCompatActivity {
    public static String TAG = "";
    public  SV bindingView;
    private ActivityBaseBinding mBaseBinding;
    private AnimationDrawable mAnimationDrawable;
    private LinearLayout llProgressBar;
    private View refresh;

    protected <T extends View> T getView(int id) {
        return findViewById(id);
    }


    @Override
    public void setContentView(int layoutResID) {
        Log.d(TAG, "setContentView: ");
        mBaseBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_base, null,
                false);
        bindingView = DataBindingUtil.inflate(getLayoutInflater(), layoutResID, null, false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bindingView.getRoot().setLayoutParams(params);
        RelativeLayout mContainer = mBaseBinding.getRoot().findViewById(R.id.container);
        mContainer.addView(bindingView.getRoot());
        getWindow().setContentView(mBaseBinding.getRoot());
        StatusBarUtil.setColor(this, CommonUtils.getColor(R.color.colorTheme),0);
        llProgressBar = getView(R.id.ll_progress_bar);
        refresh = getView(R.id.ll_error_refresh);
        ImageView img = getView(R.id.img_progress);
        // 加载动画
        mAnimationDrawable = (AnimationDrawable) img.getDrawable();
        // 默认进入页面就开启动画
        if (!mAnimationDrawable.isRunning()) {
            mAnimationDrawable.start();
        }
        setToolBar();
        // 点击加载失败布局
        refresh.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                showLoading();
                onRefresh();
            }
        });
        bindingView.getRoot().setVisibility(View.GONE);
    }
    /**
     * 设置titlebar
     */
    protected void setToolBar() {
        setSupportActionBar(mBaseBinding.toolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //去除默认Title显示
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back);
        }
        mBaseBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    public void setTitle(CharSequence text) {
        mBaseBinding.toolBar.setTitle(text);
    }

    protected void showLoading() {
        if (llProgressBar.getVisibility() != View.VISIBLE) {
            llProgressBar.setVisibility(View.VISIBLE);
        }
        // 开始动画
        if (!mAnimationDrawable.isRunning()) {
            mAnimationDrawable.start();
        }
        if (bindingView.getRoot().getVisibility() != View.GONE) {
            bindingView.getRoot().setVisibility(View.GONE);
        }
        if (refresh.getVisibility() != View.GONE) {
            refresh.setVisibility(View.GONE);
        }
    }

    protected void showContentView() {
        if (llProgressBar.getVisibility() != View.GONE) {
            llProgressBar.setVisibility(View.GONE);
        }
        // 停止动画
        if (mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        if (refresh.getVisibility() != View.GONE) {
            refresh.setVisibility(View.GONE);
        }
        if (bindingView.getRoot().getVisibility() != View.VISIBLE) {
            bindingView.getRoot().setVisibility(View.VISIBLE);
        }
    }

    protected void showError() {
        if (llProgressBar.getVisibility() != View.GONE) {
            llProgressBar.setVisibility(View.GONE);
        }
        // 停止动画
        if (mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        if (refresh.getVisibility() != View.VISIBLE) {
            refresh.setVisibility(View.VISIBLE);
        }
        if (bindingView.getRoot().getVisibility() != View.GONE) {
            bindingView.getRoot().setVisibility(View.GONE);
        }
    }

    /**
     * 失败后点击刷新
     */
    protected void onRefresh() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView1(savedInstanceState);
        TAG = getClass().getSimpleName();
        initView();
        getIntentData();
        loadData();

    }

    protected abstract void setContentView1(Bundle savedInstanceState);

    protected abstract void initView();

    protected abstract void loadData();


    protected void getIntentData() {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

            );

        }
    }

}
