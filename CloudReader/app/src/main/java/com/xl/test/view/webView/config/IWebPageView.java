package com.xl.test.view.webView.config;

import android.view.View;

/**
 * Created by hushendian on 2017/11/7.
 */

public interface IWebPageView {
    void hideProgressbar();

    void showWebView();

    void hideWebView();

    //  进度条先加载到90%,然后再加载到100%
    void startProgress();

    void progressChange(int newProgress);

    void addImageClickListener();

    void fullViewAddView(View view);


}
