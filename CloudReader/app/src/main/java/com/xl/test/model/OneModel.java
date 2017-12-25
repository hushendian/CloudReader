package com.xl.test.model;

import android.util.Log;

import com.xl.test.bean.HotMovieBean;
import com.xl.test.http.HttpClient;
import com.xl.test.http.RequestImpl;
import com.xl.test.utils.RxObservableUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by hushendian on 2017/11/21.
 */

public class OneModel {


    public void getHotMovieData(final RequestImpl listener) {
        Disposable disposable = HttpClient.Builder.getDouBanServer().getHotMovieData().compose
                (RxObservableUtils.<HotMovieBean>applySchedulers()
                ).subscribe(new Consumer<HotMovieBean>() {
            @Override
            public void accept(HotMovieBean hotMovieBean) throws Exception {
                Log.d("OneModel", "accept: " + hotMovieBean);
                listener.loadSuccess(hotMovieBean);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d("OneModel", "accept: "+throwable.getMessage());
                listener.loadFailed(throwable);
            }
        });
        listener.addDisposable(disposable);


    }
}
