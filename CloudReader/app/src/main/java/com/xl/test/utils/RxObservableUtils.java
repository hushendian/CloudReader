package com.xl.test.utils;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hushendian on 2017/10/24.
 */

public class RxObservableUtils {
    public static <T> ObservableTransformer<T, T> applySchedulers() {


        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                        .mainThread());


            }
        };
    }

}
