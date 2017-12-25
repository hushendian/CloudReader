package com.xl.test.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.xl.test.R;
import com.xl.test.base.baseAdapter.BaseRecyclerViewAdapter;
import com.xl.test.base.baseAdapter.BaseRecyclerViewHolder;
import com.xl.test.bean.moviechild.SubjectsBean;
import com.xl.test.databinding.ItemOneBinding;
import com.xl.test.ui.one.MovieDetailActivity;
import com.xl.test.utils.CommonUtils;
import com.xl.test.utils.PerfectClickListener;

/**
 * Created by hushendian on 2017/11/21.
 */

public class OneAdapter extends BaseRecyclerViewAdapter<SubjectsBean> {
    private Activity activity;
    public OneAdapter(Activity activity) {
        this.activity = activity;
    }
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new viewHolder(parent, R.layout.item_one);
    }

    class viewHolder extends BaseRecyclerViewHolder<SubjectsBean, ItemOneBinding> {

        public viewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        public void onBindViewHolder(final SubjectsBean object, int position) {
            if (object != null) {
                binding.setSubjectsBean(object);
            }


            ViewHelper.setScaleX(itemView, 0.8f);
            ViewHelper.setScaleY(itemView, 0.8f);
            ViewPropertyAnimator.animate(itemView).scaleX(1.0f).setDuration(350).setInterpolator
                    (new OvershootInterpolator()).start();
            ViewPropertyAnimator.animate(itemView).scaleY(1.0f).setDuration(350).setInterpolator
                    (new OvershootInterpolator()).start();
            // 分割线颜色
            binding.viewColor.setBackgroundColor(CommonUtils.randomColor());
            binding.llOneItem.setOnClickListener(new PerfectClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    MovieDetailActivity.StartIntent(activity,binding.ivOnePhoto,object);
                }
            });
        }
    }
}
