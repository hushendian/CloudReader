package com.xl.test.view.webView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.xl.test.R;
import com.xl.test.view.webView.config.IWebPageView;
import com.xl.test.view.webView.config.MyWebChromeClient;
import com.xl.test.view.webView.config.MyWebViewClient;

/**
 * Created by hushendian on 2017/11/7.
 */

public class WebViewActivity extends AppCompatActivity implements IWebPageView {

    private ProgressBar mpProgressBar;
    private Toolbar mToolbar;
    private WebView mWebView;
    private String mUrl;
    private String mTitle;
    private FrameLayout videoFullView;
    private MyWebChromeClient chromeClient;
    public boolean mPagerFinished;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        getIntentData();
        initView();
        loadData();
    }



    protected void initView() {
//        StatusBarUtil.setColor(this, CommonUtils.getColor(R.color.colorTheme), 0);
        mpProgressBar = findViewById(R.id.pb_progress);
        mWebView = findViewById(R.id.webview_detail);
        videoFullView = findViewById(R.id.video_fullView);
        mToolbar = findViewById(R.id.title_tool_bar);
        initWebSetting();
    }

    protected void loadData() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back);
        }
        setTitle(mTitle);
        mToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.actionbar_more));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.actionbar_share:
                        break;
                    case R.id.actionbar_open:
                        break;
                    case R.id.actionbar_cope:
                        break;

                }
                return false;
            }
        });
        mWebView.loadUrl(mUrl);
    }


    @Override
    public void hideProgressbar() {
        mpProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showWebView() {
        mWebView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideWebView() {
        mWebView.setVisibility(View.GONE);
    }

    @Override
    public void startProgress() {

    }

    @Override
    public void progressChange(int newProgress) {
        mpProgressBar.setVisibility(View.VISIBLE);
        int progress = newProgress;
        mpProgressBar.setProgress(progress);
        if (progress == 100) {
            mpProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void addImageClickListener() {

    }

    @Override
    public void fullViewAddView(View view) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.webview_menu, menu);
        return true;
    }

    public static void loadUrl(Context context, String url, String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("mTitle", title);
        intent.putExtra("mUrl", url);
        context.startActivity(intent);
    }


    protected void getIntentData() {
        if (getIntent() != null) {
            mTitle = getIntent().getStringExtra("mTitle");
            mUrl = getIntent().getStringExtra("mUrl");
        }
    }


    public void setTitle(String title) {
        mToolbar.setTitle(title);
    }

    private void initWebSetting() {
        mpProgressBar.setVisibility(View.VISIBLE);
        WebSettings wbs = mWebView.getSettings();
        wbs.setUseWideViewPort(true);//将图片调整到适合webview的大小,设置此属性，可任意比例缩放
        wbs.setLoadWithOverviewMode(false);//网页内容的宽度是否大于webview控件的大小
        wbs.setSaveFormData(true);//保存表单
        //是否支持屏幕缩放控件与手势缩放
        wbs.setSupportZoom(true);
        wbs.setBuiltInZoomControls(true);
        wbs.setDisplayZoomControls(true);
        //启动应用缓存
        wbs.setAppCacheEnabled(true);
        //设置缓存模式
        wbs.setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.setInitialScale(1);//缩放比例
        wbs.setJavaScriptEnabled(true);//告诉webview启用javaScript执行，默认是false
        wbs.setBlockNetworkImage(false);//页面加载完成之后，再放开图片
        wbs.setDomStorageEnabled(true);//使用localStorage必须打开
        wbs.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//排版适应屏幕
        wbs.setSupportMultipleWindows(true);//webview是否支持多个窗口
        //webview从5.0开始默认不允许混合模式，https中不能加载http资源，需要设置开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wbs.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        /** 设置字体默认缩放大小(改变网页字体大小,setTextSize  api14被弃用)*/
        wbs.setTextZoom(100);
        chromeClient = new MyWebChromeClient(this);
        mWebView.setWebChromeClient(chromeClient);
        mWebView.setWebViewClient(new MyWebViewClient(this));
    }
}
