package com.xl.test.app;

import android.app.Application;

import com.xl.test.http.HttpUtils;
import com.xl.test.utils.DebugUtil;

/**
 * Created by hushendian on 2017/10/13.
 */

public class CloudReaderApplication extends Application {

    private static CloudReaderApplication mApplication;

    public static CloudReaderApplication getInstance() {
        return mApplication;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        //初始化context，否则会导致HTTP 504 Unsatisfiable Request (only-if-cached)出现
        HttpUtils.getInstance().init(this, DebugUtil.DEBUG);

    }
}
