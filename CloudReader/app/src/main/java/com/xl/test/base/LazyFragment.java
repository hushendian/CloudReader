package com.xl.test.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xl.test.R;
import com.xl.test.utils.PerfectClickListener;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by hushendian on 2017/10/17.
 */

/**
 * viewStub
 *
 * @param <SV>
 */
public abstract class LazyFragment<SV extends ViewDataBinding> extends Fragment {
    public static String TAG = "";
    //布局view
    protected SV bindingView;


    protected boolean isVisible = false;
    //加载中
    private LinearLayout loading;
    //加载失败
    private LinearLayout loadingFailed;
    // 内容布局
    private RelativeLayout mContainer;

    // 动画
    private AnimationDrawable mAnimationDrawable;
    private CompositeDisposable mCompositeDisposable;


    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();

        } else {
            isVisible = false;
            onInvisible();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        TAG = getActivity().getClass().getSimpleName();
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        bindingView = DataBindingUtil.inflate(getActivity().getLayoutInflater(),
                setContentView(), null, false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bindingView.getRoot().setLayoutParams(params);
        mContainer = view.findViewById(R.id.container);
        mContainer.addView(bindingView.getRoot());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loading = getView(R.id.ll_progress_bar);
        ImageView img = getView(R.id.img_progress);

        mAnimationDrawable = (AnimationDrawable) img.getDrawable();
        if (!mAnimationDrawable.isRunning()) {
            mAnimationDrawable.start();
        }
        loadingFailed = getView(R.id.ll_error_refresh);
        loadingFailed.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                showLoading();
                refresh();
            }
        });
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
        if (bindingView.getRoot().getVisibility() != View.GONE) {
            bindingView.getRoot().setVisibility(View.GONE);
        }
        if (loadingFailed.getVisibility() != View.GONE) {
            loadingFailed.setVisibility(View.GONE);
        }

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
        if (bindingView.getRoot().getVisibility() == View.GONE) {
            bindingView.getRoot().setVisibility(View.VISIBLE);
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
        if (bindingView.getRoot().getVisibility() != View.GONE) {
            bindingView.getRoot().setVisibility(View.GONE);
        }
    }

    protected void refresh() {

    }


    protected <T extends View> T getView(int id) {
        return (T) getView().findViewById(id);
    }

    protected void onInvisible() {

    }

    protected void onVisible() {
        lazyLoad();
    }

    protected void lazyLoad() {

    }

    public abstract int setContentView();


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
