package com.xl.test.http;

import io.reactivex.disposables.Disposable;

/**
 * Created by hushendian on 2017/10/20.
 */

public interface RequestImpl {

    void loadSuccess(Object object);

    void loadFailed(Throwable throwable);

    void addDisposable(Disposable disposable);
}
