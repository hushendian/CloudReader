package com.xl.test.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.xl.test.R;
import com.xl.test.base.baseAdapter.BaseRecyclerViewAdapter;
import com.xl.test.base.baseAdapter.BaseRecyclerViewHolder;
import com.xl.test.bean.book.BooksBean;
import com.xl.test.databinding.ItemBookBinding;
import com.xl.test.ui.book.BookDeatailActivity;
import com.xl.test.utils.PerfectClickListener;

/**
 * Created by hushendian on 2017/12/4.
 */

public class BookCustomerAdapter extends BaseRecyclerViewAdapter<BooksBean> {
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_book);
    }

    public class ViewHolder extends BaseRecyclerViewHolder<BooksBean, ItemBookBinding> {

        public ViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        public void onBindViewHolder(final BooksBean object, int position) {
            binding.setBean(object);
            binding.llItemTop.setOnClickListener(new PerfectClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    BookDeatailActivity.StartIntent(v.getContext(), binding.ivTopPhoto, object);
                }
            });
        }
    }
}
