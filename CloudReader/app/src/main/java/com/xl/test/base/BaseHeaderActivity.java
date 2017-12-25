package com.xl.test.base;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.xl.test.R;
import com.xl.test.databinding.BaseHeaderTitleBarBinding;
import com.xl.test.utils.CommonUtils;
import com.xl.test.utils.GlideApp;
import com.xl.test.utils.PerfectClickListener;
import com.xl.test.view.MyNestedScrollView;
import com.xl.test.view.StatusBarUtils;
import com.xl.test.view.webView.statusbar.StatusBarUtil;

import java.lang.reflect.Method;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by hushendian on 2017/11/27.
 */

public abstract class BaseHeaderActivity<HV extends ViewDataBinding, SV extends ViewDataBinding>
        extends AppCompatActivity {

    private BaseHeaderTitleBarBinding bindingTitleView;
    public HV bindingHeaderView;
    public SV bindingContentView;
    private LinearLayout loading;
    private LinearLayout loadingFailed;
    // 动画
    private AnimationDrawable mAnimationDrawable;
    private CompositeDisposable mCompositeDisposable;
    // 这个是高斯图背景的高度
    private int imageBgHeight;
    // 滑动多少距离后标题不透明
    private int slidingDistance;

    protected <T extends View> T getView(int id) {
        return findViewById(id);
    }

    public static String TAG = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void setContentView(int layoutResID) {
        Log.d(TAG, "setContentView: ");
        View ll = getLayoutInflater().inflate(R.layout.activity_header_base, null, false);
        bindingTitleView = DataBindingUtil.inflate(getLayoutInflater(), R.layout
                .base_header_title_bar, null, false);
        bindingContentView = DataBindingUtil.inflate(getLayoutInflater(), layoutResID, null, false);

        bindingHeaderView = DataBindingUtil.inflate(getLayoutInflater(), setHeaderLayout(), null,
                false);
        //contetHead
        setContentView(R.id.header_container, bindingHeaderView, ll);
        //content
        setContentView(R.id.container, bindingContentView, ll);
        //title
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bindingTitleView.getRoot().setLayoutParams(params);
        RelativeLayout titleView = ll.findViewById(R.id.title_container);
        titleView.addView(bindingTitleView.getRoot());
        getWindow().setContentView(ll);
        loading = findViewById(R.id.ll_progress_bar);
        loadingFailed = findViewById(R.id.ll_error_refresh);
        Log.d(TAG, "setToolBar: " + setHeadImgUrl());
        setToolBar();
        ImageView imageView = getView(R.id.img_progress);
        mAnimationDrawable = (AnimationDrawable) imageView.getDrawable();
        if (!mAnimationDrawable.isRunning()) {
            mAnimationDrawable.start();
        }
        loadingFailed.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                showLoading();
                onRefresh();
            }
        });
        bindingContentView.getRoot().setVisibility(View.GONE);

    }

    private void setContentView(int id, ViewDataBinding dataBinding, View ll) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dataBinding.getRoot().setLayoutParams(params);
        RelativeLayout titleContainer = ll.findViewById(id);
        titleContainer.addView(dataBinding.getRoot());
        getWindow().setContentView(ll);
    }


    protected abstract int setHeaderLayout();

    protected abstract String setHeadImgUrl();

    protected abstract ImageView setHeaderImageView();

    protected ImageView setHeaderPicView() {
        return new ImageView(this);
    }

    public void setTitle(CharSequence text) {
        bindingTitleView.tbBaseTitle.setTitle(text);
    }

    protected void setSubTitle(CharSequence text) {
        bindingTitleView.tbBaseTitle.setSubtitle(text);
    }

    protected abstract void setTitleClickMore();

    protected abstract void onRefresh();

    protected abstract void getIntentData();

//    protected abstract void loadData();

    protected abstract void setMovieContentView(Bundle savedInstanceState);

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setToolBar() {
        setSupportActionBar(bindingTitleView.tbBaseTitle);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        bindingTitleView.tbBaseTitle.setTitleTextAppearance(this, R.style.ToolBar_Title);
        bindingTitleView.tbBaseTitle.setSubtitleTextAppearance(this, R.style.Toolbar_SubTitle);
        bindingTitleView.tbBaseTitle.inflateMenu(R.menu.base_header_menu);
        bindingTitleView.tbBaseTitle.setOnMenuItemClickListener(new Toolbar
                .OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.actionbar_more:
                        setTitleClickMore();
                }
                return false;
            }
        });
        bindingTitleView.tbBaseTitle.setNavigationOnClickListener(new PerfectClickListener() {

            @Override
            protected void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });

//        initSlideShapeTheme(setHeadImgUrl(), setHeaderImageView());
    }

    /**
     * 加载完成
     */
    public void showContentView() {

        if (loading.getVisibility() != View.GONE) {
            loading.setVisibility(View.GONE);
        }
        if (mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        if (loadingFailed.getVisibility() != View.GONE) {
            loadingFailed.setVisibility(View.GONE);
        }
        if (bindingContentView.getRoot().getVisibility() == View.GONE) {
            bindingContentView.getRoot().setVisibility(View.VISIBLE);
        }
    }

    /**
     * 加载失败
     */

    public void LoadingFailed() {
        if (loading.getVisibility() != View.GONE) {
            loading.setVisibility(View.GONE);
        }

        if (mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        if (loadingFailed.getVisibility() != View.VISIBLE) {
            loadingFailed.setVisibility(View.VISIBLE);
        }
        if (bindingContentView.getRoot().getVisibility() != View.GONE) {
            bindingContentView.getRoot().setVisibility(View.GONE);
        }
    }

    /**
     * 加载中
     */
    public void showLoading() {
        //加载中布局不存在，就直接显示；加载中动画如果没有启动，则设为启动；把布局清除掉；把加载失败的布局清除掉
        if (loading.getVisibility() != View.VISIBLE) {
            loading.setVisibility(View.VISIBLE);
        }
        if (!mAnimationDrawable.isRunning()) {
            mAnimationDrawable.start();
        }
        if (bindingContentView.getRoot().getVisibility() != View.GONE) {
            bindingContentView.getRoot().setVisibility(View.GONE);
        }
        if (loadingFailed.getVisibility() != View.GONE) {
            loadingFailed.setVisibility(View.GONE);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void initSlideShapeTheme(String imgUrl, ImageView mHeaderBg) {
        setImgHeaderBg(imgUrl);
        //toolbar的高

        int toolbarHeight = bindingTitleView.tbBaseTitle.getLayoutParams().height;
        int headerBgHeight = toolbarHeight + StatusBarUtil.getStatusBarHeight(this);
        ViewGroup.LayoutParams params = bindingTitleView.ivBaseTitlebarBg.getLayoutParams();
        ViewGroup.MarginLayoutParams ivTitleHeadBgParams = (ViewGroup.MarginLayoutParams)
                bindingTitleView.ivBaseTitlebarBg.getLayoutParams();
        int marginTop = params.height - headerBgHeight;
        ivTitleHeadBgParams.setMargins(0, -marginTop, 0, 0);
        bindingTitleView.ivBaseTitlebarBg.setImageAlpha(0);
        StatusBarUtils.setTranslucentImageHeader(this, 0, bindingTitleView.tbBaseTitle);
        // 上移背景图片，使空白状态栏消失(这样下方就空了状态栏的高度)
        if (mHeaderBg != null) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mHeaderBg
                    .getLayoutParams();
            layoutParams.setMargins(0, -StatusBarUtil.getStatusBarHeight(this), 0, 0);
            ViewGroup.LayoutParams imgItemBgParams = mHeaderBg.getLayoutParams();
            imageBgHeight = imgItemBgParams.height;
        }
        initScrollViewListener();
        initNewSlidingParams();
    }


    private void initScrollViewListener() {
        ((MyNestedScrollView) findViewById(R.id.mns_base)).setOnScrollChangeListener(new MyNestedScrollView
                .ScrollInterface() {
            @Override
            public void onScrollChange(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                scrollChangeHeader(scrollY);
            }
        });
    }

    private void initNewSlidingParams() {
        int titleBarAndStatusHeight = (int) (CommonUtils.getDimens(R.dimen.nav_bar_height) +
                StatusBarUtil.getStatusBarHeight(this));
        slidingDistance = imageBgHeight - titleBarAndStatusHeight - (int) (CommonUtils.getDimens
                (R.dimen.base_header_activity_slide_more));

    }

    /**
     * 根据页面滑动距离改变Head方法
     *
     * @param scrolledY
     */
    private void scrollChangeHeader(int scrolledY) {
        if (scrolledY < 0) {
            scrolledY = 0;
        }
        float alpha = Math.abs(scrolledY) * 1.0f / (slidingDistance);
        Log.d(TAG, "scrollChangeHeader: "+alpha);
        Drawable drawable = bindingTitleView.ivBaseTitlebarBg.getDrawable();
        if (drawable == null) {
            return;
        }
        if (scrolledY <= slidingDistance) {
            // title部分的渐变
            drawable.mutate().setAlpha((int) (alpha * 255));
            bindingTitleView.ivBaseTitlebarBg.setImageDrawable(drawable);
        } else {
            drawable.mutate().setAlpha(255);
            bindingTitleView.ivBaseTitlebarBg.setImageDrawable(drawable);
        }
    }

    /**
     * 加载titlebar背景
     */
    private void setImgHeaderBg(String imgUrl) {
        if (!TextUtils.isEmpty(imgUrl)) {

            GlideApp.with(this).load(imgUrl).error(R.drawable.stackblur_default).transform(new
                    BlurTransformation(23, 4)).listener(new RequestListener<Drawable>() {


                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target
                        <Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                        target, DataSource dataSource, boolean isFirstResource) {
                    bindingTitleView.tbBaseTitle.setBackgroundColor(Color.TRANSPARENT);
                    bindingTitleView.ivBaseTitlebarBg.setImageAlpha(255);
                    bindingTitleView.ivBaseTitlebarBg.setVisibility(View.VISIBLE);

                    return false;
                }
            }).into(bindingTitleView.ivBaseTitlebarBg);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base_header_menu, menu);
        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible",
                            Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    public void addDisposable(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.dispose();
        }
    }

    public void removeDisposable() {
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.dispose();
        }
    }
}
