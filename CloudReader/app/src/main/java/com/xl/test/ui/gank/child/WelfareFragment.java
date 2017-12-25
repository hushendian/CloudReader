package com.xl.test.ui.gank.child;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Toast;

import com.example.xrecyclerview.XRecyclerView;
import com.xl.test.R;
import com.xl.test.adapter.WelfareAdapter;
import com.xl.test.app.Constants;
import com.xl.test.base.LazyFragment;
import com.xl.test.base.baseAdapter.OnItemClickListener;
import com.xl.test.bean.GankIoDataBean;
import com.xl.test.databinding.FragmentWelfareBinding;
import com.xl.test.http.HttpUtils;
import com.xl.test.http.RequestImpl;
import com.xl.test.http.cache.ACache;
import com.xl.test.model.GankOtherModel;
import com.xl.test.view.viewBindingImage.viewBindingImageActivity;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;

//import com.xl.test.adapter.WelfareAdapter;

/**
 * Created by hushendian on 2017/10/17.
 */

public class WelfareFragment extends LazyFragment<FragmentWelfareBinding> {
    private WelfareAdapter mWelfareAdapter;
    private boolean isPrepared = false;
    private boolean isFirst = true;
    private int mPage = 1;
    private GankIoDataBean meiziBean;
    private GankOtherModel mModel;
    private ACache aCache;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mModel = new GankOtherModel();
        mWelfareAdapter = new WelfareAdapter();
        aCache = ACache.get(getContext());
        meiziBean = (GankIoDataBean) aCache.getAsObject(Constants.GANK_MEIZI);
        bindingView.xrvWelfare.setPullRefreshEnabled(false);
        bindingView.xrvWelfare.clearHeader();
        bindingView.xrvWelfare.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getActivity(), "刷新", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadMore() {
                mPage++;
                loadWelfareData();
            }
        });
        isPrepared = true;
    }

    @Override
    protected void lazyLoad() {

        if (!isPrepared || !isVisible || !isFirst) {
            return;
        }
        if (meiziBean != null && meiziBean.getResults() != null && meiziBean.getResults().size()
                != 0) {
            showContentView();
            imgList.clear();
            ;
            for (int i = 0; i < meiziBean.getResults().size(); i++) {
                imgList.add(meiziBean.getResults().get(i).getUrl());
            }
            meiziBean = (GankIoDataBean) aCache.getAsObject(Constants.GANK_MEIZI);
            setAdapter(meiziBean);
        } else {
            loadWelfareData();
        }

    }

    @Override
    public int setContentView() {
        return R.layout.fragment_welfare;
    }

    ArrayList<String> imgList = new ArrayList<>();

    public void loadWelfareData() {
        mModel.setData("福利", mPage, HttpUtils.per_page_more);
        mModel.getGankIoData(new RequestImpl() {
            @Override
            public void loadSuccess(Object object) {
                showContentView();
                GankIoDataBean gankIoDataBean = (GankIoDataBean) object;
                if (mPage == 1) {
                    if (gankIoDataBean != null && gankIoDataBean.getResults() != null &&
                            gankIoDataBean.getResults().size() != 0) {
                        imgList.clear();
                        for (GankIoDataBean.ResultBean resultBean : gankIoDataBean.getResults()) {
                            imgList.add(resultBean.getUrl());
                        }
                        setAdapter(gankIoDataBean);
                        aCache.remove(Constants.GANK_MEIZI);
                        aCache.put(Constants.GANK_MEIZI, gankIoDataBean, 30000);
                    }
                } else {
                    // TODO: 2017/11/6  上拉加载处理
                    if (gankIoDataBean != null && gankIoDataBean.getResults() != null &&
                            gankIoDataBean.getResults().size() != 0) {
                        bindingView.xrvWelfare.refreshComplete();
                        mWelfareAdapter.addAll(gankIoDataBean.getResults());
                        mWelfareAdapter.notifyDataSetChanged();

                        for (GankIoDataBean.ResultBean resultBean : gankIoDataBean.getResults()) {
                            imgList.add(resultBean.getUrl());
                        }
                    } else {
                        bindingView.xrvWelfare.noMoreLoading();
                    }
                }
            }

            @Override
            public void loadFailed(Throwable throwable) {
                bindingView.xrvWelfare.refreshComplete();
                if (mWelfareAdapter.getItemCount() == 0) {
                    LoadingFailed();
                }
                if (mPage > 1) {
                    mPage--;
                }
            }

            @Override
            public void addDisposable(Disposable disposable) {
                WelfareFragment.this.addDisposable(disposable);
            }
        });

    }

    private void setAdapter(GankIoDataBean gankIoDataBean) {
        mWelfareAdapter.addAll(gankIoDataBean.getResults());
        bindingView.xrvWelfare.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        bindingView.xrvWelfare.setAdapter(mWelfareAdapter);
        mWelfareAdapter.notifyDataSetChanged();
        mWelfareAdapter.setOnItemClickListener(new OnItemClickListener<GankIoDataBean.ResultBean>
                () {
            @Override
            public void onClick(GankIoDataBean.ResultBean resultBean, int position) {

                Intent intent = new Intent(getContext(), viewBindingImageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("select", 2);//2,大图显示当前页面；1，头像。不显示页数
                bundle.putInt("position", position);
                bundle.putStringArrayList("imageurl", imgList);
                intent.putExtras(bundle);
                getContext().startActivity(intent);
            }
        });
        isFirst = false;
    }

    @Override
    protected void refresh() {
        super.refresh();
        setContentView();
        loadWelfareData();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
