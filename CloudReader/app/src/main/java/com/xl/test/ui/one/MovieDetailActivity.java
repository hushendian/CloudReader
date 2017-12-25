package com.xl.test.ui.one;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.xl.test.R;
import com.xl.test.adapter.MovieDetailAdapter;
import com.xl.test.base.BaseHeaderActivity;
import com.xl.test.bean.MovieDetailBean;
import com.xl.test.bean.moviechild.SubjectsBean;
import com.xl.test.databinding.ActivityOneMovieDetailBinding;
import com.xl.test.databinding.HeaderSlideShapeBinding;
import com.xl.test.http.RequestImpl;
import com.xl.test.model.MovieDeatailModel;
import com.xl.test.utils.CommonUtils;
import com.xl.test.utils.StringFormatUtil;
import com.xl.test.view.webView.WebViewActivity;

import io.reactivex.disposables.Disposable;

public class MovieDetailActivity extends BaseHeaderActivity<HeaderSlideShapeBinding,
        ActivityOneMovieDetailBinding> {
    private SubjectsBean subjectsBean;
    private String mMoreUrl;
    private String mMovieName;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMovieContentView(savedInstanceState);
        getIntentData();
        loadData();
    }

    @Override
    protected int setHeaderLayout() {
        return R.layout.header_slide_shape;
    }

    @Override
    protected String setHeadImgUrl() {
        if (subjectsBean == null) {
            return "";
        }
        return subjectsBean.getImages().getMedium();
    }

    @Override
    protected ImageView setHeaderImageView() {
        return bindingHeaderView.imgItemBg;
    }

    @Override
    protected void setTitleClickMore() {
        WebViewActivity.loadUrl(this, mMoreUrl, mMovieName);
    }

    @Override
    protected void onRefresh() {
        loadData();
    }


    public static void StartIntent(Context context, ImageView imageView, SubjectsBean
            subjectsBean) {
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra("bean", subjectsBean);
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation
                ((Activity) context, imageView, CommonUtils.getString(R.string
                        .transition_movie_img));
        ActivityCompat.startActivity(context, intent, compat.toBundle());
    }

    @Override
    protected void getIntentData() {
        Log.d(TAG, "getIntentData: ");
        if (getIntent() != null) {
            subjectsBean = (SubjectsBean) getIntent().getSerializableExtra("bean");
        }

    }


    private void loadData() {
        Log.d(TAG, "loadData: " + subjectsBean);
        setTitle(subjectsBean.getTitle());
        setSubTitle(String.format("主演：%s", StringFormatUtil.formatName(subjectsBean.getCasts())));
        bindingHeaderView.setSubjectsBean(subjectsBean);
        bindingHeaderView.executePendingBindings();
        Log.d(TAG, "loadSuccess: " + subjectsBean.getId());
        new MovieDeatailModel(subjectsBean.getId()).getMovieDeatail(new RequestImpl() {
            @Override
            public void loadSuccess(Object object) {
                Log.d(TAG, "loadSuccess: " + object);
                MovieDetailBean movieDetailBean = (MovieDetailBean) object;

                if (movieDetailBean != null && movieDetailBean.getCountries().size() != 0) {
                    showContentView();
                    bindingHeaderView.tvOneDay.setText(String.format("上映日期：%s", movieDetailBean
                            .getYear()));
                    bindingHeaderView.tvOneCity.setText(String.format("制片国家/地区：%s",
                            StringFormatUtil.formatGenres(movieDetailBean
                                    .getCountries())));
                    bindingHeaderView.setMovieDetailBean(movieDetailBean);
                    bindingContentView.setBean(movieDetailBean);
                    bindingContentView.executePendingBindings();
                    mMoreUrl = movieDetailBean.getAlt();
                    mMovieName = movieDetailBean.getTitle();
                    transformData(movieDetailBean);
                } else {
                    LoadingFailed();
                }
            }

            @Override
            public void loadFailed(Throwable throwable) {
                Log.d(TAG, "loadFailed: " + throwable.getMessage());
                LoadingFailed();
            }

            @Override
            public void addDisposable(Disposable disposable) {
//                this.addDisposable(disposable);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void setMovieContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_one_movie_detail);
    }

    /**
     * 异步线程转换数据
     */
    private void transformData(final MovieDetailBean movieDetailBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < movieDetailBean.getDirectors().size(); i++) {
                    movieDetailBean.getDirectors().get(i).setType("导演");
                }
                for (int i = 0; i < movieDetailBean.getCasts().size(); i++) {
                    movieDetailBean.getCasts().get(i).setType("演员");
                }
                MovieDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAdapter(movieDetailBean);
                    }
                });

            }
        }).start();
    }

    private void setAdapter(MovieDetailBean movieDetailBean) {
        bindingContentView.xrvCast.setVisibility(View.VISIBLE);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        bindingContentView.xrvCast.setLayoutManager(manager);
        bindingContentView.xrvCast.setPullRefreshEnabled(false);
        bindingContentView.xrvCast.setLoadingMoreEnabled(false);
        // 需加，不然滑动不流畅
        bindingContentView.xrvCast.setNestedScrollingEnabled(false);
        bindingContentView.xrvCast.setHasFixedSize(false);
        MovieDetailAdapter adapter = new MovieDetailAdapter();
        adapter.addAll(movieDetailBean.getDirectors());
        adapter.addAll(movieDetailBean.getCasts());
        bindingContentView.xrvCast.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


}
