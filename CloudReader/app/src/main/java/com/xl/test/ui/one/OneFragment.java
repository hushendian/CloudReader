package com.xl.test.ui.one;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.xl.test.R;
import com.xl.test.adapter.OneAdapter;
import com.xl.test.app.ConstantsImageUrl;
import com.xl.test.base.LazyFragment;
import com.xl.test.bean.HotMovieBean;
import com.xl.test.bean.moviechild.SubjectsBean;
import com.xl.test.databinding.FragmentOneBinding;
import com.xl.test.http.RequestImpl;
import com.xl.test.model.OneModel;
import com.xl.test.utils.ImageUtils;
import com.xl.test.utils.PerfectClickListener;

import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by hushendian on 2017/10/17.
 */

public class OneFragment extends LazyFragment<FragmentOneBinding> {
    private boolean isFirst = true;
    private boolean isPrepared = false;
    private OneAdapter adapter;
    private OneModel oneModel;
    private List<SubjectsBean> beans;
    private View mHeaderView = null;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindingView.listOne.clearHeader();
        bindingView.listOne.setPullRefreshEnabled(false);
        bindingView.listOne.setLoadingMoreEnabled(false);
        bindingView.listOne.setNestedScrollingEnabled(false);
        bindingView.listOne.setHasFixedSize(false);
        bindingView.listOne.setItemAnimator(new DefaultItemAnimator());
        adapter = new OneAdapter(getActivity());
        oneModel = new OneModel();
        isPrepared = true;
    }

    @Override
    public int setContentView() {
        return R.layout.fragment_one;
    }

    @Override
    protected void lazyLoad() {
        if (!isFirst || !isPrepared || !isVisible) {
            return;
        }
        getMovieData();
    }


    private void getMovieData() {
        Log.d(TAG, "loadSuccess.getHotMovieData: ");
        oneModel.getHotMovieData(new RequestImpl() {
            @Override
            public void loadSuccess(Object object) {
                Log.d(TAG, "loadSuccess.getHotMovieData: " + object);
                showContentView();
                HotMovieBean hotMovieBean = (HotMovieBean) object;
                if (hotMovieBean != null) {
                    beans = hotMovieBean.getSubjects();
                    if (beans != null && beans.size() != 0) {
                        setAdapter(beans);
                    } else {
                        LoadingFailed();
                    }
                }
            }

            @Override
            public void loadFailed(Throwable throwable) {
                showContentView();
                if (adapter != null && adapter.getItemCount() == 0) {
                    LoadingFailed();
                }
            }

            @Override
            public void addDisposable(Disposable disposable) {

            }
        });
    }

    private void setAdapter(List<SubjectsBean> beans) {
        Log.d(TAG, "setAdapter: ");
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        bindingView.listOne.setLayoutManager(manager);
        if (mHeaderView == null) {
            mHeaderView = getLayoutInflater().inflate(R.layout.header_item_one, null, false);
            View llMovieTop = mHeaderView.findViewById(R.id.ll_movie_top);
            ImageView ivImg = (ImageView) mHeaderView.findViewById(R.id.iv_img);
            ImageUtils.displayRandom(3, ConstantsImageUrl.ONE_URL_01, ivImg);
            llMovieTop.setOnClickListener(new PerfectClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    Intent intent = new Intent(getActivity(), DoubanTopActivity.class);
                    startActivity(intent);
                }
            });
        }
        bindingView.listOne.addHeaderView(mHeaderView);
        adapter.clear();
        adapter.addAll(beans);
        bindingView.listOne.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        isFirst = false;
    }
}
