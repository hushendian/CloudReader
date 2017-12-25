package com.xl.test.model;

import com.xl.test.bean.GankIoDataBean;
import com.xl.test.http.HttpClient;
import com.xl.test.http.RequestImpl;
import com.xl.test.utils.RxObservableUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by hushendian on 2017/11/2.
 */

public class GankOtherModel {

    private String type;
    private int pre_pager;
    private int pager;

    public void setData(String type, int pre_pager, int pager) {
        this.type = type;
        this.pre_pager = pre_pager;
        this.pager = pager;
    }

    public void getGankIoData(final RequestImpl listener) {
        Disposable disposable = HttpClient.Builder.getGankIOServer().getGankIoData(type, pager,
                pre_pager).compose(RxObservableUtils.<GankIoDataBean>applySchedulers()).subscribe
                (new Consumer<GankIoDataBean>() {
                    @Override
                    public void accept(GankIoDataBean gankIoDataBean) throws Exception {
                        listener.loadSuccess(gankIoDataBean);
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
