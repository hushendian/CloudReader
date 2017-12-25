package com.xl.test.utils;

import android.content.Context;
import android.widget.ImageView;

import com.xl.test.R;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by hushendian on 2017/10/23.
 */

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        GlideApp.with(context).load(path)
                .placeholder(R.drawable.img_two_bi_one)
                .error(R.drawable.img_two_bi_one)
                .into(imageView);
    }
}
