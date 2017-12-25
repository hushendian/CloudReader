package com.xl.test.ui.gank.child;

import android.animation.ObjectAnimator;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.xl.test.R;
import com.xl.test.adapter.EverydayAdapter;
import com.xl.test.app.Constants;
import com.xl.test.base.LazyFragment;
import com.xl.test.bean.AndroidBean;
import com.xl.test.bean.FrontpageBean;
import com.xl.test.databinding.FooterItemEverydayBinding;
import com.xl.test.databinding.FragmentEverydayBinding;
import com.xl.test.databinding.HeaderItemEverydayBinding;
import com.xl.test.http.RequestImpl;
import com.xl.test.http.cache.ACache;
import com.xl.test.http.rxBus.RxBus;
import com.xl.test.http.rxBus.RxBusBaseMessage;
import com.xl.test.http.rxBus.RxCodeConstants;
import com.xl.test.model.EverydayModel;
import com.xl.test.utils.GlideImageLoader;
import com.xl.test.utils.PerfectClickListener;
import com.xl.test.utils.SPUtils;
import com.xl.test.utils.TimeUtil;
import com.xl.test.view.webView.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by hushendian on 2017/10/17.
 */

public class EverydayFragment extends LazyFragment<FragmentEverydayBinding> {

    String year = getTodayTime().get(0);
    String month = getTodayTime().get(1);
    String day = getTodayTime().get(2);
    PerfectClickListener listener = new PerfectClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.ib_xiandu://干货闲读
                    WebViewActivity.loadUrl(v.getContext(), "https://gank.io/xiandu", "加载中...");
                    break;
                case R.id.ib_movie_hot://新电影热映
                    RxBus.getInstance().send(RxCodeConstants.JUMP_TYPE_TO_ONE, new
                            RxBusBaseMessage());
                    break;
            }

        }
    };
    private ObjectAnimator animator;
    private ACache mACache;
    private EverydayModel mEverydayModel;
    private ArrayList<String> mBannerImages;
    private HeaderItemEverydayBinding mHeaderBinding;
    private FooterItemEverydayBinding mFooterBinding;
    private View mHeaderView;
    private View mFooterView;
    private boolean mIsFirst = true;
    private boolean mIsPrepared = false;
    // 是否是上一天的请求
    private boolean isOldDayRequest;
    private ArrayList<List<AndroidBean>> mLists;
    private EverydayAdapter mEverydayAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showContentView();
        startAnimation();
        mACache = ACache.getInstance(getActivity());
        mEverydayModel = EverydayModel.getInstance();
        mBannerImages = (ArrayList<String>) mACache.getAsObject(Constants.BANNER_PIC);
        mHeaderBinding = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout
                .header_item_everyday, null, false);

        initLocalSetting();
        initRecyclerView();
        mIsPrepared = true;
        /**
         * 因为启动时先走loadData()再走onActivityCreated，
         * 所以此处要额外调用load(),不然最初不会加载内容
         */
        lazyLoad();
    }

    /**
     * 开启动画
     */
    private void startAnimation() {
        animator = ObjectAnimator.ofFloat(bindingView.ivLoading, "rotation", 0f, 360f);
        animator.setDuration(3000);//动画持续时间
        animator.setRepeatCount(10);//重复十次
        animator.setInterpolator(new LinearInterpolator());//不停顿
        animator.start();
    }

    @Override
    public int setContentView() {
        return R.layout.fragment_everyday;
    }

    /**
     * 初始化recycleview的head，并设置点击事件
     */
    private void initLocalSetting() {
        mEverydayModel.setData(getTodayTime().get(0), getTodayTime().get(1), getTodayTime().get(2));
        //区分日期出现0x的情况
        mHeaderBinding.includeEveryday.tvDailyText.setText(getTodayTime().get(2).indexOf(0) == 0
                ? getTodayTime().get(2).replace("0", "") : getTodayTime().get(2));
        mHeaderBinding.includeEveryday.ibXiandu.setOnClickListener(listener);
        mHeaderBinding.includeEveryday.ibMovieHot.setOnClickListener(listener);
    }

    /**
     * recycleview加头加尾
     */
    private void initRecyclerView() {
        bindingView.xrvEveryday.setPullRefreshEnabled(false);
        bindingView.xrvEveryday.setLoadingMoreEnabled(false);
        if (mHeaderView == null) {
            mHeaderView = mHeaderBinding.getRoot();
            bindingView.xrvEveryday.addHeaderView(mHeaderView);
        }
        if (mFooterView == null) {
            mFooterBinding = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout
                    .footer_item_everyday, null, false);
            mFooterView = mFooterBinding.getRoot();
            bindingView.xrvEveryday.addFootView(mFooterView, true);
            bindingView.xrvEveryday.noMoreLoading();
        }
        bindingView.xrvEveryday.setLayoutManager(new LinearLayoutManager(getContext()));
        //需加，不然滑动不流畅
        bindingView.xrvEveryday.setNestedScrollingEnabled(false);
        bindingView.xrvEveryday.setHasFixedSize(false);
        bindingView.xrvEveryday.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * 获取当天日期
     */
    private SparseArray<String> getTodayTime() {
        String data = TimeUtil.getData();
        String[] split = data.split("-");
        SparseArray<String> list = new SparseArray<>();
        for (int i = 0; i < split.length; i++) {
            list.put(i, split[i]);
        }
        return list;
    }

    @Override
    protected void lazyLoad() {
        // 显示时轮播图滚动
        if (mHeaderBinding != null && mHeaderBinding.banner != null) {
            mHeaderBinding.banner.startAutoPlay();
            mHeaderBinding.banner.setDelayTime(4000);
        }
        //如果Fragment没有显示，onActivityCreate没有初始化
        if (!isVisible || !mIsPrepared || !mIsFirst) {
            return;
        }
        String oneData = SPUtils.getString("everyday_data", "2017-10-20");
        if (!oneData.equals(TimeUtil.getData())) {//是第二天
            if (TimeUtil.isRightTime()) {//大于12：30,请求
                isOldDayRequest = false;
                mEverydayModel.setData(getTodayTime().get(0), getTodayTime().get(1), getTodayTime
                        ().get(2));
                showRotaLoading(true);
                loadBannerPicture();
                showContentData();
            } else {
                ArrayList<String> lastTime = TimeUtil.getLastTime(getTodayTime().get(0),
                        getTodayTime().get(1), getTodayTime().get(2));
                mEverydayModel.setData(lastTime.get(0), lastTime.get(1), lastTime.get(2));
                year = lastTime.get(0);
                month = lastTime.get(1);
                day = lastTime.get(2);

                isOldDayRequest = true;// 是昨天
                getACacheData();
            }

        } else {
            isOldDayRequest = false;
            getACacheData();
        }

    }

    /**
     * 设置加载中，加载完成布局控制
     *
     * @param isLoading
     */
    private void showRotaLoading(boolean isLoading) {
        if (isLoading) {
            bindingView.llLoading.setVisibility(View.VISIBLE);
            bindingView.xrvEveryday.setVisibility(View.GONE);
            animator.start();
        } else {
            bindingView.llLoading.setVisibility(View.GONE);
            bindingView.xrvEveryday.setVisibility(View.VISIBLE);
            animator.cancel();
        }
    }

    /**
     * 加载banner
     */
    private void loadBannerPicture() {
        mEverydayModel.showBanncerPage(new RequestImpl() {
            @Override
            public void loadSuccess(Object object) {
                if (mBannerImages == null) {
                    mBannerImages = new ArrayList<>();
                } else {
                    mBannerImages.clear();
                }
                FrontpageBean bean = (FrontpageBean) object;
                if (bean != null && bean.getResult() != null && bean.getResult().getFocus() !=
                        null && bean.getResult().getFocus().getResult() != null) {
                    final List<FrontpageBean.ResultBeanXXXXXXXXXXXXXX.FocusBean.ResultBeanX>
                            result = bean.getResult().getFocus().getResult();

                    if (result != null && result.size() != 0) {
                        for (int i = 0; i < result.size(); i++) {
                            mBannerImages.add(result.get(i).getRandpic());
                        }
                    }
                    mHeaderBinding.banner.setImages(mBannerImages).setImageLoader(new
                            GlideImageLoader()).start();
                    mACache.remove(Constants.BANNER_PIC);
                    mACache.put(Constants.BANNER_PIC, mBannerImages, 30000);
                }
            }

            @Override
            public void loadFailed(Throwable throwable) {

            }

            @Override
            public void addDisposable(Disposable disposable) {
                EverydayFragment.this.addDisposable(disposable);
            }
        });
    }

    /**
     * 加载正文内容
     */
    private void showContentData() {
        mEverydayModel.showRecyclerViewData(new RequestImpl() {

            @Override
            public void loadSuccess(Object object) {
                if (mLists != null) {
                    mLists.clear();
                }
                mLists = (ArrayList<List<AndroidBean>>) object;

                if (mLists.size() > 0 && mLists.get(0).size() > 0) {
                    setAdapter(mLists);
                }
            }

            @Override
            public void loadFailed(Throwable throwable) {
                if (mLists != null && mLists.size() > 0) {
                    return;
                }
                LoadingFailed();
            }

            @Override
            public void addDisposable(Disposable disposable) {
                EverydayFragment.this.addDisposable(disposable);
            }
        });
    }

    private void setAdapter(ArrayList<List<AndroidBean>> lists) {
        showRotaLoading(false);
        if (mEverydayAdapter == null) {
            mEverydayAdapter = new EverydayAdapter();
        } else {
            mEverydayAdapter.clear();
        }
        mEverydayAdapter.addAll(lists);
        mACache.remove(Constants.EVERYDAY_CONTENT);
        mACache.put(Constants.EVERYDAY_CONTENT, lists, 259200);
        mIsFirst = false;

        if (isOldDayRequest) {
            ArrayList<String> lastTime = TimeUtil.getLastTime(getTodayTime().get(0), getTodayTime
                    ().get(1), getTodayTime().get(2));
            SPUtils.putString("everyday_data", lastTime.get(0) + "-" + lastTime.get(1) + "-" +
                    lastTime.get(2));
        } else {
            SPUtils.putString("everyday_data", TimeUtil.getData());
        }
        mIsFirst = false;
        bindingView.xrvEveryday.setAdapter(mEverydayAdapter);
        mEverydayAdapter.notifyDataSetChanged();
    }

    /**
     * 缓存
     */
    private void getACacheData() {
        if (!mIsFirst) {
            return;
        }
        if (mBannerImages != null && mBannerImages.size() != 0) {
            mHeaderBinding.banner.setImages(mBannerImages).setImageLoader(new GlideImageLoader())
                    .start();
        } else {
            loadBannerPicture();
        }
        mLists = (ArrayList<List<AndroidBean>>) mACache.getAsObject(Constants.EVERYDAY_CONTENT);
        if (mLists != null && mLists.size() != 0) {
            setAdapter(mLists);
        } else {
            showRotaLoading(true);
            showContentData();
        }
    }

    @Override
    protected void refresh() {
        super.refresh();
        showContentData();
        showRotaLoading(true);
        lazyLoad();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}


