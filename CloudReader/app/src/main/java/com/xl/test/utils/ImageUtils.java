package com.xl.test.utils;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.xl.test.R;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by hushendian on 2017/10/13.
 */

public class ImageUtils {

    private static ImageUtils imageUtils;

    public static ImageUtils getInstance() {
        if (imageUtils == null) {
            synchronized (ImageUtils.class) {
                if (imageUtils == null) {
                    imageUtils = new ImageUtils();
                }

            }
        }
        return imageUtils;
    }

    /**
     * 加载普通图片
     *
     * @param path
     * @param placeholderId
     * @param errorId
     * @param imageView
     */
    public static void loadImageview(String path, int placeholderId, int errorId,
                                     ImageView
                                             imageView) {
        GlideApp.with(imageView.getContext()).load(path).placeholder(placeholderId).error
                (errorId).into
                (imageView);
    }

    /**
     * 加载圆角图,暂时用到显示头像
     */
    public void displayCircle(ImageView imageView, String imageUrl, int error) {
        GlideApp.with(imageView.getContext())
                .load(imageUrl)
                .error(error)
                .transform(new GlideCircleTransform(imageView.getContext()))
                .into(imageView);

    }

    /**
     * 妹子，电影列表图
     *
     * @param defaultPicType 电影：0；妹子：1； 书籍：2
     */
    @BindingAdapter({"android:displayFadeImage", "android:defaultPicType"})
    public static void displayFadeImage(ImageView imageView, String url, int defaultPicType) {
//        displayEspImage(url, imageView, defaultPicType);
        loadImageview(url, getDefaultPic(defaultPicType), getDefaultPic(defaultPicType), imageView);
    }

    /**
     * 电影详情页显示电影图片(等待被替换)（测试的还在，已可以弃用）
     * 没有加载中的图
     */
    @BindingAdapter("android:showImg")
    public static void showImg(ImageView imageView, String url) {
        GlideApp.with(imageView.getContext())
                .load(url)
                .error(getDefaultPic(0))
                .into(imageView);
    }

    /**
     * 电影列表图片
     * override:重新设置tag的宽高值 px
     *
     * @param
     * @return
     */
    @BindingAdapter("android:showMovieImg")
    public static void showMovieImg(ImageView imageView, String url) {
        GlideApp.with(imageView.getContext()).load(url).override((int) CommonUtils.getDimens(R
                .dimen.movie_detail_width), (int) CommonUtils.getDimens(R.dimen
                .movie_detail_height)).placeholder(getDefaultPic(0)).error(getDefaultPic(0)).into
                (imageView);
    }

    /**
     * 电影详情页显示高斯背景图
     *
     * @param
     * @return
     */
    @BindingAdapter("android:showImgBg")
    public static void showImgBg(ImageView imageView, String url) {
        displayGaussian(imageView.getContext(), url, imageView);
    }

    /**
     * 显示高斯模糊效果（电影详情页）
     */
    private static void displayGaussian(Context context, String url, ImageView imageView) {
        // "23":模糊度；"4":图片缩放4倍后再进行模糊
        GlideApp.with(context).load(url).error(R.drawable.stackblur_default)
                .placeholder(R.drawable.stackblur_default).transform(new BlurTransformation
                (23, 4)).into(imageView);

    }

    /**
     * 书籍列表图片
     * override:重新设置tag的宽高值 px
     *
     * @param
     * @return
     */
    @BindingAdapter("android:showBookImg")
    public static void showBookImg(ImageView imageView, String url) {
        GlideApp.with(imageView.getContext()).load(url).override((int) CommonUtils.getDimens(R
                .dimen.movie_detail_width), (int) CommonUtils.getDimens(R.dimen
                .movie_detail_height)).placeholder(getDefaultPic(2)).error(getDefaultPic(2)).into
                (imageView);
    }

    private static int getDefaultPic(int type) {
        switch (type) {
            case 0:// 电影
                return R.drawable.img_default_movie;
            case 1:// 妹子
                return R.drawable.img_default_meizi;
            case 2:// 书籍
                return R.drawable.img_default_book;
        }
        return R.drawable.img_default_meizi;
    }

    /**
     * 显示随机的图片(每日推荐)
     *
     * @param imgNumber 有几张图片要显示,对应默认图
     * @param imageUrl  显示图片的url
     * @param imageView 对应图片控件
     */
    public static void displayRandom(int imgNumber, String imageUrl, ImageView imageView) {
        GlideApp.with(imageView.getContext())
                .load(imageUrl)
                .placeholder(getMusicDefaultPic(imgNumber))
                .error(getMusicDefaultPic(imgNumber))
                .into(imageView);
    }

    private static int getMusicDefaultPic(int imgNumber) {
        switch (imgNumber) {
            case 1:
                return R.drawable.img_two_bi_one;
            case 2:
                return R.drawable.img_four_bi_three;
            case 3:
                return R.drawable.img_one_bi_one;
        }
        return R.drawable.img_four_bi_three;
    }

    /**
     * 用于干货item，将gif图转换为静态图
     */
    public static void displayGif(String url, ImageView imageView) {
        GlideApp.with(imageView.getContext()).asBitmap().load(url).placeholder(R.drawable
                .img_one_bi_one).error(R.drawable.img_one_bi_one).into(imageView);
    }


}
