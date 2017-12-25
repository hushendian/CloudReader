package com.xl.test.ui.one;

import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.example.xrecyclerview.XRecyclerView;
import com.xl.test.R;
import com.xl.test.adapter.DouBanTopAdapter;
import com.xl.test.base.BaseActivity;
import com.xl.test.bean.HotMovieBean;
import com.xl.test.bean.moviechild.SubjectsBean;
import com.xl.test.databinding.ActivityDoubanTopBinding;
import com.xl.test.http.HttpClient;
import com.xl.test.utils.RxObservableUtils;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by hushendian on 2017/11/30.
 */

public class DoubanTopActivity extends BaseActivity<ActivityDoubanTopBinding> {
    DouBanTopAdapter adapter;
    private int start = 0;
    private int mCount = 21;

    @Override
    protected void setContentView1(Bundle savedInstanceState) {
        setContentView(R.layout.activity_douban_top);
    }

    @Override
    protected void initView() {
        adapter = new DouBanTopAdapter();
    }

    @Override
    protected void loadData() {
        setTitle("豆瓣电影Top250");
        bindingView.xrvTop.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                start += mCount;
                loadDouBanTop250();
            }
        });
        loadDouBanTop250();

    }


    private void loadDouBanTop250() {
        Disposable disposable = HttpClient.Builder.getDouBanServer().getMovieTop250(start,
                mCount).compose(RxObservableUtils.<HotMovieBean>applySchedulers()).subscribe(new Consumer<HotMovieBean>() {

            @Override
            public void accept(HotMovieBean hotMovieBean) throws Exception {
                showContentView();
                if (start == 0) {
                    if (hotMovieBean != null && hotMovieBean.getSubjects() != null &&
                            hotMovieBean.getSubjects().size() != 0) {
                        setAdapter(hotMovieBean.getSubjects());
                    } else {
                        bindingView.xrvTop.setVisibility(View.GONE);
                    }
                } else {
                    if (hotMovieBean != null && hotMovieBean.getSubjects() != null &&
                            hotMovieBean.getSubjects().size() > 0) {
                        bindingView.xrvTop.refreshComplete();
                        adapter.addAll(hotMovieBean.getSubjects());
                        adapter.notifyDataSetChanged();
                    } else {
                        bindingView.xrvTop.setLoadingMoreEnabled(false);
                    }
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }


    private void setAdapter(List<SubjectsBean> list) {
        adapter.clear();
        adapter.addAll(list);
        bindingView.xrvTop.setLayoutManager(new StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL));
        bindingView.xrvTop.setAdapter(adapter);
        bindingView.xrvTop.setPullRefreshEnabled(false);
        bindingView.xrvTop.clearHeader();
        bindingView.xrvTop.setNestedScrollingEnabled(false);
        bindingView.xrvTop.setHasFixedSize(false);
        bindingView.xrvTop.setLoadingMoreEnabled(true);
        adapter.notifyDataSetChanged();
    }
}
