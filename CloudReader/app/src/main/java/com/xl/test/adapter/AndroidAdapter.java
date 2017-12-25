package com.xl.test.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.xl.test.R;
import com.xl.test.base.baseAdapter.BaseRecyclerViewAdapter;
import com.xl.test.base.baseAdapter.BaseRecyclerViewHolder;
import com.xl.test.bean.GankIoDataBean;
import com.xl.test.databinding.ItemAndroidBinding;
import com.xl.test.utils.ImageUtils;
import com.xl.test.view.webView.WebViewActivity;

/**
 * Created by hushendian on 2017/11/6.
 */

public class AndroidAdapter extends BaseRecyclerViewAdapter<GankIoDataBean.ResultBean> {
    private boolean isAll = false;

    public void setAllType(boolean isAll) {
        this.isAll = isAll;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_android);
    }

    private class ViewHolder extends BaseRecyclerViewHolder<GankIoDataBean.ResultBean,
            ItemAndroidBinding> {

        public ViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        public void onBindViewHolder(final GankIoDataBean.ResultBean object, int position) {
            if (isAll && "福利".equals(object.getType())) {
                binding.ivAllWelfare.setVisibility(View.VISIBLE);
                binding.llWelfareOther.setVisibility(View.GONE);
                ImageUtils.displayFadeImage(binding.ivAllWelfare, object.getUrl(), 1);
            } else {
                binding.ivAllWelfare.setVisibility(View.GONE);
                binding.llWelfareOther.setVisibility(View.VISIBLE);
            }
            if (isAll) {
                binding.tvContentType.setVisibility(View.VISIBLE);
                binding.tvContentType.setText(" · " + object.getType());
            } else {
                binding.tvContentType.setVisibility(View.GONE);
            }
            binding.setResultsBean(object);
            binding.executePendingBindings();

            //显示gif图片会很耗内存
            if (object.getImages() != null && object.getImages().size() != 0 && !TextUtils
                    .isEmpty(object.getImages().get(0))) {
                binding.ivAndroidPic.setVisibility(View.VISIBLE);
                Log.d("AndroidAdapter", "onBindViewHolder: " + object.getImages().get(0));
                ImageUtils.displayGif(object.getImages().get(0), binding.ivAndroidPic);
            } else {
                binding.ivAndroidPic.setVisibility(View.GONE);
            }
            binding.llAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebViewActivity.loadUrl(v.getContext(),object.getUrl(),"加载中...");
                }
            });
        }

    }

}
