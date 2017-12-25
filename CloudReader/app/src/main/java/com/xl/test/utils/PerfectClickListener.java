package com.xl.test.utils;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * Created by hushendian on 2017/10/17.
 */

public abstract class PerfectClickListener implements OnClickListener {
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;
    private int id = -1;

    //分两种情况，第一种：一个控件点击事件；第二种：两个控件点击事件
    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        int mId = v.getId();
        if (id != mId) {
            id = mId;
            lastClickTime = currentTime;
            onNoDoubleClick(v);
            return;
        }
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

    protected abstract void onNoDoubleClick(View v);
}
