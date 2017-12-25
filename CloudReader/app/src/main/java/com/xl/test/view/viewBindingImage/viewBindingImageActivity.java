package com.xl.test.view.viewBindingImage;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.xl.test.R;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by hushendian on 2017/11/14.
 */

public class viewBindingImageActivity extends AppCompatActivity implements ViewPager
        .OnPageChangeListener, PhotoViewAttacher.OnPhotoTapListener {
    private int position;
    private int select;

    // 保存图片
    private TextView tv_save_big_image;
    // 接收传过来的uri地址
    public List<String> imageuri;
    // 接收穿过来当前选择的图片的数量
    int code;
    // 用于判断是头像还是文章图片 1:头像 2：文章大图
    int selet;

    // 用于管理图片的滑动
    ViewPager very_image_viewpager;
    // 当前页数
    private int page;
    /**
     * 显示当前图片的页数
     */
    TextView very_image_viewpager_text;
    /**
     * 本应用图片的id
     */
    private int imageId;
    /**
     * 是否是本应用中的图片
     */
    private boolean isApp;
    /**
     * 用于判断是否是加载本地图片
     */
    private boolean isLocal;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_big_image);
        getIntentData();
        initView();
        loadData();
    }


    protected void initView() {
        very_image_viewpager_text = (TextView) findViewById(R.id.very_image_viewpager_text);
        tv_save_big_image = (TextView) findViewById(R.id.tv_save_big_image);
        very_image_viewpager = (ViewPager) findViewById(R.id.very_image_viewpager);
    }


    protected void loadData() {
        tv_save_big_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/11/15 保存图片
            }
        });
        if (isApp) {
            myPaperAdapter myPageAdapter = new myPaperAdapter();
            very_image_viewpager.setAdapter(myPageAdapter);
            very_image_viewpager.setEnabled(false);
        } else {
            ViewPagerAdapter adapter = new ViewPagerAdapter();
            very_image_viewpager.setAdapter(adapter);
            very_image_viewpager.setOnPageChangeListener(this);
            very_image_viewpager.setEnabled(false);
            very_image_viewpager.setCurrentItem(position);
            if (select == 2) {
                very_image_viewpager_text.setText((position + 1) + " / " + imageuri.size());
            }
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (select == 2) {
            very_image_viewpager_text.setText((position + 1) + " / " + imageuri.size());
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (select == 2) {
            very_image_viewpager_text.setText((position + 1) + " / " + imageuri.size());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    protected void getIntentData() {
        if(getIntent()!=null){
            Bundle bundle = getIntent().getExtras();
            position = bundle.getInt("position");
            select = bundle.getInt("select");
            imageuri = bundle.getStringArrayList("imageurl");
            /**是否是本应用中的图片*/
            isApp = bundle.getBoolean("isApp", false);
            /**本应用图片的id*/
            imageId = bundle.getInt("id", 0);
        }



    }

    @Override
    public void onPhotoTap(View view, float x, float y) {
        finish();
    }

    @Override
    public void onOutsidePhotoTap() {
        finish();
    }

    class myPaperAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getLayoutInflater().inflate(R.layout.viewpager_very_image, container,
                    false);
            PhotoView zoom_image_view = (PhotoView) view.findViewById(R.id.zoom_image_view);
            ProgressBar spinner = (ProgressBar) view.findViewById(R.id.loading);
            zoom_image_view.setOnPhotoTapListener(viewBindingImageActivity.this);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {

            if (imageuri.size() == 0 || imageuri == null) {
                return 0;
            }
            return imageuri.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getLayoutInflater().inflate(R.layout.viewpager_very_image, container,
                    false);
            final PhotoView zoom_image_view = (PhotoView) view.findViewById(R.id.zoom_image_view);
            final ProgressBar spinner = (ProgressBar) view.findViewById(R.id.loading);
            spinner.setVisibility(View.VISIBLE);
            String url = null;
            // TODO: 2017/11/15 区分本地网络地址
            String path = imageuri.get(position);
            if (isLocal) {

            } else {
                url = path;
            }

            Glide.with(viewBindingImageActivity.this).load(url).listener(new RequestListener<Drawable>() {


                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                            Target<Drawable> target, boolean isFirstResource) {
                    Toast.makeText(getApplicationContext(), "资源加载异常", Toast.LENGTH_SHORT).show();
                    spinner.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                        target, DataSource dataSource, boolean isFirstResource) {
                    spinner.setVisibility(View.GONE);
                    int height = zoom_image_view.getHeight();

                    int wHeight = getWindowManager().getDefaultDisplay().getHeight();
                    //CENTER_CROP:将图片等比例缩放，让图片的短边与imageview的边长度相同
                    //FIT_CENTER：ImageView的默认状态，大图等比例缩小，使整幅图能够居中显示在ImageView
                    // 中，小图等比例放大，同样要整体居中显示在ImageView中。
                    if (height > wHeight) {
                        zoom_image_view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    } else {
                        zoom_image_view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    }
                    return false;
                }
            }).into(zoom_image_view);
            zoom_image_view.setOnPhotoTapListener(viewBindingImageActivity.this);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
