package com.xl.test.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.xl.test.R;
import com.xl.test.base.baseAdapter.BaseRecyclerViewAdapter;
import com.xl.test.base.baseAdapter.BaseRecyclerViewHolder;
import com.xl.test.bean.moviechild.PersonBean;
import com.xl.test.databinding.ItemMovieDetailPersonBinding;
import com.xl.test.utils.PerfectClickListener;
import com.xl.test.view.webView.WebViewActivity;


/**
 * Created by hushendian on 2017/11/28.
 */

public class MovieDetailAdapter extends BaseRecyclerViewAdapter<PersonBean> {
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_movie_detail_person);
    }

    public class ViewHolder extends BaseRecyclerViewHolder<PersonBean,
            ItemMovieDetailPersonBinding> {


        public ViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        public void onBindViewHolder(final PersonBean object, int position) {
            binding.setPersonBean(object);
            binding.llItem.setOnClickListener(new PerfectClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    Log.d("MovieDetailAdapter", "onNoDoubleClick: " + object.getAlt() + "------"
                            + object.getName());
                    WebViewActivity.loadUrl(v.getContext(), object.getAlt(), object.getName());
                }
            });
        }
    }
}
