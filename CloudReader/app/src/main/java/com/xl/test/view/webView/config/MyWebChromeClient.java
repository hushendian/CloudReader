package com.xl.test.view.webView.config;

import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.xl.test.view.webView.WebViewActivity;

/**
 * Created by hushendian on 2017/11/8.
 */

public class MyWebChromeClient extends WebChromeClient {


    private IWebPageView webPageView;
    private WebViewActivity webViewActivity;
    private String title;

    public MyWebChromeClient(IWebPageView webPageView) {
        this.webPageView = webPageView;
        this.webViewActivity = (WebViewActivity) webPageView;
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        webViewActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        webPageView.hideWebView();
    }

    @Override
    public void onHideCustomView() {
        webPageView.showWebView();
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        Log.d("MyWebChromeClient", "onProgressChanged: ");
        webPageView.progressChange(newProgress);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        webViewActivity.setTitle(title);
        this.title = title;
    }


    public String getTitle() {
        return title;
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        return super.onJsAlert(view, url, message, result);
    }

}
