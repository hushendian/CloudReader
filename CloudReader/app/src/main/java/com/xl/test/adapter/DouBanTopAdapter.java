package com.xl.test.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.xl.test.R;
import com.xl.test.base.baseAdapter.BaseRecyclerViewAdapter;
import com.xl.test.base.baseAdapter.BaseRecyclerViewHolder;
import com.xl.test.bean.moviechild.SubjectsBean;
import com.xl.test.databinding.ItemDoubanTopBinding;
import com.xl.test.ui.one.MovieDetailActivity;
import com.xl.test.utils.PerfectClickListener;

/**
 * Created by hushendian on 2017/12/4.
 */

public class DouBanTopAdapter extends BaseRecyclerViewAdapter<SubjectsBean> {
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_douban_top);
    }

    public class ViewHolder extends BaseRecyclerViewHolder<SubjectsBean, ItemDoubanTopBinding> {

        public ViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        public void onBindViewHolder(final SubjectsBean object, int position) {
            binding.setBean(object);
            binding.llItemTop.setOnClickListener(new PerfectClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    MovieDetailActivity.StartIntent(v.getContext(), binding.ivTopPhoto, object);
                }
            });
        }
    }
}
