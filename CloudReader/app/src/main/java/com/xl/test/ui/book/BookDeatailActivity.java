package com.xl.test.ui.book;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.xl.test.R;
import com.xl.test.base.BaseHeaderActivity;
import com.xl.test.bean.book.BookDetailBean;
import com.xl.test.bean.book.BooksBean;
import com.xl.test.databinding.ActivityBookDetailBinding;
import com.xl.test.databinding.HeaderBookDetailBinding;
import com.xl.test.http.HttpClient;
import com.xl.test.utils.CommonUtils;
import com.xl.test.utils.RxObservableUtils;
import com.xl.test.view.webView.WebViewActivity;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by hushendian on 2017/12/12.
 */

public class BookDeatailActivity extends BaseHeaderActivity<HeaderBookDetailBinding,
        ActivityBookDetailBinding> {
    private static String BEAN = "bean";
    private BooksBean bean;
    private String mMoreUrl;
    private String mMovieName;

    @Override
    protected int setHeaderLayout() {
        return R.layout.header_book_detail;
    }

    @Override
    protected String setHeadImgUrl() {

        if (bean == null || bean.getImages() == null) {
            return "";
        }

        return bean.getImages().getLarge();
    }

    @Override
    protected ImageView setHeaderImageView() {
        return bindingHeaderView.imgItemBg;
    }

    @Override
    protected void setTitleClickMore() {
        WebViewActivity.loadUrl(this, mMoreUrl, mMovieName);
    }

    @Override
    protected void onRefresh() {
        loadDetailActivity();
    }

    @Override
    protected void getIntentData() {
        if (getIntent() != null) {
            bean = (BooksBean) getIntent().getExtras().get(BEAN);
        }
    }

    @Override
    protected void setMovieContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_book_detail);
    }

    public static void StartIntent(Context context, ImageView imageView, BooksBean bean) {
        Intent intent = new Intent(context, BookDeatailActivity.class);
        intent.putExtra(BEAN, bean);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation
                ((Activity) context, imageView, CommonUtils.getString(R.string.transition_book_img));
        ActivityCompat.startActivity(context,intent,optionsCompat.toBundle());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMovieContentView(savedInstanceState);

        getIntentData();
        // TODO: 2017/12/13 此处有疑问
        initSlideShapeTheme(setHeadImgUrl(), setHeaderImageView());
        setTitle(bean.getTitle());
        setSubTitle("作者：" + bean.getAuthor());
        bindingHeaderView.setBooksBean(bean);
        bindingHeaderView.executePendingBindings();
        loadDetailActivity();
    }

    private void loadDetailActivity() {
        if (bean != null) {
            Disposable disposable = HttpClient.Builder.getDouBanServer().getBookDetail(bean.getId
                    ()).compose(RxObservableUtils.<BookDetailBean>applySchedulers()).subscribe
                    (new Consumer<BookDetailBean>() {

                        @Override
                        public void accept(BookDetailBean bookDetailBean) throws Exception {
                            if (bookDetailBean != null) {
                                mMoreUrl = bookDetailBean.getAlt();
                                mMovieName = bookDetailBean.getTitle();
                                showContentView();
                                bindingContentView.setBookDetailBean(bookDetailBean);
                                bindingContentView.executePendingBindings();
                            } else {
                                LoadingFailed();
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            LoadingFailed();
                        }
                    });
        }

    }
}
