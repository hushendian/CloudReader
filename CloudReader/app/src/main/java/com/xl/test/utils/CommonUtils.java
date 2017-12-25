package com.xl.test.utils;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.xl.test.app.CloudReaderApplication;

import java.util.Random;

/**
 * Created by hushendian on 2017/10/13.
 */

public class CommonUtils {


    /**
     * 随机颜色
     */
    public static int randomColor() {
        Random random = new Random();
        int red = random.nextInt(150) + 50;//50-199
        int green = random.nextInt(150) + 50;//50-199
        int blue = random.nextInt(150) + 50;//50-199
        return Color.rgb(red, green, blue);
    }

    public static Drawable getDrawable(int resID) {

        return getResource().getDrawable(resID);
    }

    public static Resources getResource() {
        return CloudReaderApplication.getInstance().getResources();
    }

    public static int getColor(int color) {
        return getResource().getColor(color);
    }

    /**
     * @param resId dimes.xml文件中的dp或者sp数值乘以屏幕scale来换算成px单位
     * @return
     */
    public static float getDimens(int resId) {
        return getResource().getDimension(resId);
    }

    public static String getString(int resId) {
        return getResource().getString(resId);
    }

}
