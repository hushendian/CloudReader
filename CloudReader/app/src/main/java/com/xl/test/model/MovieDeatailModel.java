package com.xl.test.model;

import com.xl.test.bean.MovieDetailBean;
import com.xl.test.http.HttpClient;
import com.xl.test.http.RequestImpl;
import com.xl.test.utils.RxObservableUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by hushendian on 2017/11/24.
 */

public class MovieDeatailModel {
    private String id;

    public MovieDeatailModel(String id) {
        this.id = id;
    }

    public void getMovieDeatail(final RequestImpl listener) {
        Disposable disposable = HttpClient.Builder.getDouBanServer().getMovieDetailData(id)
                .compose(RxObservableUtils.<MovieDetailBean>applySchedulers()).subscribe(new Consumer<MovieDetailBean>() {


                    @Override
                    public void accept(MovieDetailBean movieDetailBean) throws Exception {
                        listener.loadSuccess(movieDetailBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        listener.loadFailed(throwable);
                    }
                });
        listener.addDisposable(disposable);
    }

}
