package com.xl.test.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xl.test.R;
import com.xl.test.bean.book.BooksBean;
import com.xl.test.databinding.FooterItemBookBinding;
import com.xl.test.databinding.HeaderItemBookBinding;
import com.xl.test.databinding.ItemBookBinding;
import com.xl.test.ui.book.BookDeatailActivity;
import com.xl.test.utils.PerfectClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hushendian on 2017/12/21.
 */

public class OtherBookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int status = 1;
    private List<BooksBean> list;
    private Context context;
    public static final int LOAD_MORE = 0;
    public static final int LOAD_PULL_TO = 1;
    public static final int LOAD_NONE = 2;
    private static final int LOAD_END = 3;
    private static final int TYPE_FOOTER_BOOK = -2;
    private static final int TYPE_HEADER_BOOK = -3;
    private static final int TYPE_CONTENT_BOOK = -4;

   public  OtherBookAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    /**
     * 处理 GridLayoutManager 添加头尾布局占满屏幕宽的情况
     */

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gaLayoutManager = (GridLayoutManager) manager;
            gaLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isHeader(position) || isFooter(position) ? gaLayoutManager
                            .getSpanCount() : 1);
                }
            });
        }
    }

    /**
     * 这里规定 position = 0 时
     * 就为头布局，设置为占满整屏幕宽
     */
    private boolean isHeader(int position) {
        return position >= 0 && position < 1;
    }

    /**
     * 这里规定 position =  getItemCount() - 1时
     * 就为尾布局，设置为占满整屏幕宽
     * getItemCount() 改了 ，这里就不用改
     */
    private boolean isFooter(int position) {
        return position < getItemCount() && position >= getItemCount() - 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {

            case TYPE_HEADER_BOOK:
                HeaderItemBookBinding bindHeader = DataBindingUtil.inflate(LayoutInflater.from
                        (context), R.layout.header_item_book, parent, false);
                return new HeaderViewHolder(bindHeader.getRoot());
            case TYPE_FOOTER_BOOK:
                FooterItemBookBinding bindFooter = DataBindingUtil.inflate(LayoutInflater.from
                        (context), R.layout.footer_item_book, parent, false);
                return new FooterViewHolder(bindFooter.getRoot());
            default:
                ItemBookBinding bindContent = DataBindingUtil.inflate(LayoutInflater.from
                        (context), R.layout.item_book, parent, false);
                return new BookViewHolder(bindContent.getRoot());
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.bindItem();
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.bindItem();
        } else if (holder instanceof BookViewHolder) {
            BookViewHolder bookViewHolder = (BookViewHolder) holder;
            if (list != null && list.size() > 0) {
                // 内容从"1"开始
//                DebugUtil.error("------position: "+position);
                bookViewHolder.bindItem(list.get(position - 1), position - 1);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER_BOOK;
        } else if (position + 1 == getItemCount()) {
            return TYPE_FOOTER_BOOK;
        } else {
            return TYPE_CONTENT_BOOK;
        }
    }


    public void updateLoadStatus(int status) {
        this.status = status;
        notifyDataSetChanged();
    }

    public int getLoadStatus() {
        return this.status;
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        FooterItemBookBinding mBindFooter;

        public FooterViewHolder(View itemView) {
            super(itemView);
            mBindFooter = DataBindingUtil.getBinding(itemView);
            mBindFooter.rlMore.setGravity(Gravity.CENTER);
        }

        private void bindItem() {
            switch (status) {
                case LOAD_MORE:
                    mBindFooter.progress.setVisibility(View.VISIBLE);
                    mBindFooter.tvLoadPrompt.setText("正在加载...");
                    itemView.setVisibility(View.VISIBLE);
                    break;
                case LOAD_PULL_TO:
                    mBindFooter.progress.setVisibility(View.GONE);
                    mBindFooter.tvLoadPrompt.setText("上拉加载更多");
                    itemView.setVisibility(View.VISIBLE);
                    break;
                case LOAD_NONE:
                    System.out.println("LOAD_NONE----");
                    mBindFooter.progress.setVisibility(View.GONE);
                    mBindFooter.tvLoadPrompt.setText("没有更多内容了");
                    break;
                case LOAD_END:
                    itemView.setVisibility(View.GONE);
            }
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderItemBookBinding mBindBook;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mBindBook = DataBindingUtil.getBinding(itemView);
        }

        private void bindItem() {

        }
    }

    private class BookViewHolder extends RecyclerView.ViewHolder {
        ItemBookBinding mBindBook;

        public BookViewHolder(View itemView) {
            super(itemView);
            mBindBook = DataBindingUtil.getBinding(itemView);
        }

        private void bindItem(final BooksBean book, int position) {
            mBindBook.setBean(book);
            mBindBook.executePendingBindings();

            mBindBook.llItemTop.setOnClickListener(new PerfectClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    BookDeatailActivity.StartIntent(context, mBindBook.ivTopPhoto, book);
                }
            });
        }
    }


    public void addAll(List<BooksBean> list) {
        this.list.addAll(list);
    }

    public void add(BooksBean bean) {
        this.list.add(bean);
    }

    public List<BooksBean> getList() {
        return list;
    }

    public void setList(List<BooksBean> list) {
        this.list = list;
    }
}
