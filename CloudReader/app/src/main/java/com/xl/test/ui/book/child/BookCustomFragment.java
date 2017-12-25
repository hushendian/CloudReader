package com.xl.test.ui.book.child;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xl.test.R;
import com.xl.test.adapter.OtherBookAdapter;
import com.xl.test.base.LazyFragment;
import com.xl.test.bean.book.BookBean;
import com.xl.test.bean.book.BooksBean;
import com.xl.test.databinding.FragmentBookCustomBinding;
import com.xl.test.http.HttpClient;
import com.xl.test.utils.CommonUtils;
import com.xl.test.utils.RxObservableUtils;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by hushendian on 2017/12/11.
 */

public class BookCustomFragment extends LazyFragment<FragmentBookCustomBinding> {
    private static final String TAG = "param1";
    private boolean isFirst = true;
    private boolean isPrepared;
    //    private BookCustomerAdapter adapter;
    private int start = 0;
    private int count = 21;
    private String tag = "综合";
    private GridLayoutManager mLayoutManager;
    private OtherBookAdapter mBookAdapter;

    public static BookCustomFragment newInstance(String tag) {
        BookCustomFragment fragment = new BookCustomFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TAG, tag);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int setContentView() {
        return R.layout.fragment_book_custom;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tag = getArguments().getString(TAG);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        adapter = new BookCustomerAdapter();

        bindingView.srlBook.setColorSchemeColors(CommonUtils.getColor(R.color.colorTheme));
        bindingView.srlBook.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bindingView.srlBook.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        start = 0;
                        loadData();
                    }
                }, 1000);
            }
        });
        isPrepared = true;

        scrollRecycleView();
        loadData();
    }

    @Override
    protected void lazyLoad() {
        if (!isFirst || !isPrepared || !isVisible) {
            return;
        }
        bindingView.srlBook.setRefreshing(true);
        bindingView.srlBook.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        }, 500);
    }

    private void loadData() {
        Disposable disposable = HttpClient.Builder.getDouBanServer().getBook(tag, start, count)
                .compose(RxObservableUtils.<BookBean>applySchedulers()).subscribe(new Consumer<BookBean>() {

                    @Override
                    public void accept(BookBean bookBean) throws Exception {
                        if (start == 0) {
                            if (bookBean != null && bookBean.getBooks() != null && bookBean.getBooks
                                    ().size() != 0) {
                                showContentView();
                                if (bindingView.srlBook.isRefreshing()) {
                                    bindingView.srlBook.setRefreshing(false);
                                }
                                setAdapter(bookBean.getBooks());
                            } else {
                                LoadingFailed();
                            }
                            isFirst = false;
                        } else {

                            if (bookBean != null && bookBean.getBooks() != null && bookBean.getBooks
                                    ().size() != 0) {
                                mBookAdapter.addAll(bookBean.getBooks());
                                mBookAdapter.notifyDataSetChanged();
                            } else {
                                LoadingFailed();
                            }
                        }
                        if (mBookAdapter != null) {
                            mBookAdapter.updateLoadStatus(OtherBookAdapter.LOAD_MORE);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (bindingView.srlBook.isRefreshing()) {
                            bindingView.srlBook.setRefreshing(false);
                        }
                        if (start == 0) {
                            LoadingFailed();
                        }
                    }
                });

    }

    private void setAdapter(List<BooksBean> list) {
//
//        bindingView.xrvBook.setLayoutManager(new StaggeredGridLayoutManager(3,
//                StaggeredGridLayoutManager.VERTICAL));
        mBookAdapter = new OtherBookAdapter(getActivity());
        mLayoutManager = new GridLayoutManager(getActivity(), 3);
        bindingView.xrvBook.setLayoutManager(mLayoutManager);
        bindingView.xrvBook.setHasFixedSize(false);
        bindingView.xrvBook.setNestedScrollingEnabled(false);
        mBookAdapter.addAll(list);
        bindingView.xrvBook.setAdapter(mBookAdapter);

    }

    public void scrollRecycleView() {
        bindingView.xrvBook.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                }
                if (mBookAdapter == null) {
                    return;
                }
                if (mLayoutManager.getItemCount() == 0) {
                    mBookAdapter.updateLoadStatus(OtherBookAdapter.LOAD_NONE);
                    return;
                }
                if (lastVisibleItem + 1 == mLayoutManager.getItemCount() && mBookAdapter
                        .getLoadStatus() != OtherBookAdapter.LOAD_MORE) {
                    mBookAdapter.updateLoadStatus(OtherBookAdapter.LOAD_MORE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            start += count;
                            loadData();
                        }
                    }, 1000);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
    }
}
