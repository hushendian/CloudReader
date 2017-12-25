package com.xl.test.ui.gank.child;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.example.xrecyclerview.XRecyclerView;
import com.xl.test.R;
import com.xl.test.adapter.AndroidAdapter;
import com.xl.test.base.LazyFragment;
import com.xl.test.bean.GankIoDataBean;
import com.xl.test.databinding.FragmentCustomBinding;
import com.xl.test.http.HttpUtils;
import com.xl.test.http.RequestImpl;
import com.xl.test.model.GankOtherModel;
import com.xl.test.utils.PerfectClickListener;
import com.xl.test.utils.SPUtils;

import io.reactivex.disposables.Disposable;

/**
 * Created by hushendian on 2017/10/17.
 */

public class CustomFragment extends LazyFragment<FragmentCustomBinding> {
    private GankOtherModel mModel;
    private AndroidAdapter mAndroidAdapter;
    private boolean isFirst = true;
    private boolean isPreapere;
    private View mHeaderView;
    private GankIoDataBean mAllBean;
    private String type = "all";
    private int mPage = 1;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mModel = new GankOtherModel();
        mAndroidAdapter = new AndroidAdapter();
//        bindingView.xrvCustom.setPullRefreshEnabled(false);
//        bindingView.xrvCustom.clearHeader();
        isPreapere = true;
        bindingView.xrvCustom.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getActivity(), "刷新", Toast.LENGTH_SHORT).show();
                bindingView.xrvCustom.refreshComplete();
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

        mModel.setData(type, mPage, HttpUtils.per_page_more);
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
        if (mHeaderView == null) {
            mHeaderView = View.inflate(getContext(), R.layout.header_item_gank_custom, null);
            bindingView.xrvCustom.addHeaderView(mHeaderView);
            initHeader(mHeaderView);
        }
//        bindingView.xrvCustom.clearHeader();

        boolean isAll = SPUtils.getString("gank_cala", "全部").equals("全部");
        mAndroidAdapter.clear();
        mAndroidAdapter.setAllType(isAll);
        mAndroidAdapter.addAll(mCustomBean.getResults());
        bindingView.xrvCustom.setLayoutManager(new LinearLayoutManager(getActivity()));
        bindingView.xrvCustom.setAdapter(mAndroidAdapter);
        mAndroidAdapter.notifyDataSetChanged();
        isFirst = false;
    }

    private void initHeader(View mHeaderView) {
        final TextView txName = (TextView) mHeaderView.findViewById(R.id.tx_name);
        final String gankCala = SPUtils.getString("gank_cala", "全部");
        txName.setText(gankCala);
        final View view = mHeaderView.findViewById(R.id.ll_choose_catalogue);
        view.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                new BottomSheet.Builder(getActivity(), R.style.myBottomSheetStyle).title("选择分类")
                        .sheet(R.menu
                                .gank_bottomsheet).listener(new DialogInterface.OnClickListener
 () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.all:
                                setTypeAndSave(txName, "all", "全部");
                                break;
                            case R.id.ios:
                                setTypeAndSave(txName, "iOS", "IOS");
                                break;
                            case R.id.app:
                                setTypeAndSave(txName, "App", "App");
                                break;
                            case R.id.qian:
                                setTypeAndSave(txName, "前端", "前端");
                                break;
                            case R.id.movie:
                                setTypeAndSave(txName, "休息视频", "休息视频");
                                break;
                            case R.id.resouce:
                                setTypeAndSave(txName, "拓展资源", "拓展资源");
                                break;
                        }
                    }
                }).show();
            }
        });
    }



    private void setTypeAndSave(TextView view, String value, String ch) {
        if (isOtherType(ch)) {
            view.setText(ch);
            mPage = 1;
            type = value;
            mAndroidAdapter.clear();
            SPUtils.putString("gank_cala", ch);
            showLoading();
            loadCustomData();
        }
    }

    private boolean isOtherType(String selectType) {
        String clickText = SPUtils.getString("gank_cala", "全部");
        if (clickText.equals(selectType)) {
            return false;
        } else {
            bindingView.xrvCustom.reset();
            return true;
        }
    }
}
