package com.xl.test.view.webView.config;

import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xl.test.http.utils.CheckNetwork;
import com.xl.test.view.webView.WebViewActivity;

/**
 * Created by hushendian on 2017/11/8.
 */

public class MyWebViewClient extends WebViewClient {
    private IWebPageView mIWebPageView;
    private WebViewActivity mActivity;


    public MyWebViewClient(IWebPageView mIWebPageView) {
        this.mIWebPageView = mIWebPageView;
        this.mActivity = (WebViewActivity) mIWebPageView;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request);
    }


    @Override
    public void onPageFinished(WebView view, String url) {
        mActivity.mPagerFinished = true;
        mIWebPageView.hideProgressbar();
        if (!CheckNetwork.isNetworkConnected(mActivity)) {
            mIWebPageView.hideProgressbar();
        }
        //html加载完成后，添加坚挺图片的点击js函数
        mIWebPageView.addImageClickListener();
    }
}
