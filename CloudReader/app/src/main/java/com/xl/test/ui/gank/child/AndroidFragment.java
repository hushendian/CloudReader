package com.xl.test.ui.gank.child;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import com.example.xrecyclerview.XRecyclerView;
import com.xl.test.R;
import com.xl.test.adapter.AndroidAdapter;
import com.xl.test.base.LazyFragment;
import com.xl.test.bean.GankIoDataBean;
import com.xl.test.databinding.FragmentCustomBinding;
import com.xl.test.http.HttpUtils;
import com.xl.test.http.RequestImpl;
import com.xl.test.model.GankOtherModel;

import io.reactivex.disposables.Disposable;

/**
 * Created by hushendian on 2017/10/17.
 */

public class AndroidFragment extends LazyFragment<FragmentCustomBinding> {
    private GankOtherModel mModel;
    private AndroidAdapter mAndroidAdapter;
    private boolean isFirst = true;
    private boolean isPreapere;
    private GankIoDataBean mAllBean;

    private int mPage = 1;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mModel = new GankOtherModel();
        mAndroidAdapter = new AndroidAdapter();
        bindingView.xrvCustom.setPullRefreshEnabled(false);
        bindingView.xrvCustom.clearHeader();
        isPreapere = true;
        bindingView.xrvCustom.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                mPage++;
                loadCustomData();
            }
        });
    }

    @Override
    protected void lazyLoad() {

        if (!isVisible || !isFirst || !isPreapere) {
            return;
        }
        // TODO: 2017/11/6  先查看缓存
        if (mAllBean != null
                && mAllBean.getResults() != null
                && mAllBean.getResults().size() > 0) {

        } else {
            loadCustomData();
        }
    }

    @Override
    public int setContentView() {
        return R.layout.fragment_custom;
    }

    private void loadCustomData() {

        mModel.setData("Android", mPage, HttpUtils.per_page_more);
        mModel.getGankIoData(new RequestImpl() {
            @Override
            public void loadSuccess(Object object) {
                showContentView();
                GankIoDataBean bean = (GankIoDataBean) object;
                if (mPage == 1) {
                    if (bean != null && bean.getResults() != null && bean.getResults().size() !=
                            0) {
                        setAdapter(bean);
                    }
                } else {
                    if (bean != null && bean.getResults() != null && bean.getResults().size() !=
                            0) {
                        bindingView.xrvCustom.refreshComplete();
                        mAndroidAdapter.addAll(bean.getResults());
                        mAndroidAdapter.notifyDataSetChanged();

                    } else {
                        bindingView.xrvCustom.noMoreLoading();
                    }
                }


            }

            @Override
            public void loadFailed(Throwable throwable) {

            }

            @Override
            public void addDisposable(Disposable disposable) {

            }
        });

    }

    /**
     * 设置adapter
     */
    private void setAdapter(GankIoDataBean mCustomBean) {
        mAndroidAdapter.clear();
        mAndroidAdapter.setAllType(false);
        mAndroidAdapter.addAll(mCustomBean.getResults());
        bindingView.xrvCustom.setLayoutManager(new LinearLayoutManager(getActivity()));
        bindingView.xrvCustom.setAdapter(mAndroidAdapter);
        mAndroidAdapter.notifyDataSetChanged();
        isFirst = false;
    }
}
