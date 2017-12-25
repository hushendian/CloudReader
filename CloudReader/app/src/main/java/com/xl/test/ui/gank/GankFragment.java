package com.xl.test.ui.gank;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import com.xl.test.R;
import com.xl.test.base.LazyFragment;
import com.xl.test.databinding.FragmentGankBinding;
import com.xl.test.http.rxBus.RxBus;
import com.xl.test.http.rxBus.RxCodeConstants;
import com.xl.test.ui.gank.child.AndroidFragment;
import com.xl.test.ui.gank.child.CustomFragment;
import com.xl.test.ui.gank.child.EverydayFragment;
import com.xl.test.ui.gank.child.WelfareFragment;
import com.xl.test.view.MyFragmentPagerAdapter;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

/**
 * Created by hushendian on 2017/10/17.
 */

public class GankFragment extends LazyFragment<FragmentGankBinding> {

    private ArrayList<String> mTitleList = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showLoading();
        initFragment();
        initRxBus();
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getChildFragmentManager(),
                mFragments, mTitleList);
        bindingView.vpGank.setAdapter(adapter);
        bindingView.vpGank.setOffscreenPageLimit(3);
        adapter.notifyDataSetChanged();
        bindingView.tabGank.setTabMode(TabLayout.MODE_FIXED);
        bindingView.tabGank.setupWithViewPager(bindingView.vpGank);
        showContentView();
    }

    private void initFragment() {
        mTitleList.add("每日推荐");
        mTitleList.add("福利");
        mTitleList.add("干货订制");
        mTitleList.add("大安卓");
        mFragments.add(new EverydayFragment());
        mFragments.add(new WelfareFragment());
        mFragments.add(new CustomFragment());
        mFragments.add(new AndroidFragment());
    }

    @Override
    public int setContentView() {
        return R.layout.fragment_gank;
    }


    public void initRxBus() {
        RxBus.getInstance().tObservable(RxCodeConstants.JUMP_TYPE, Integer.class).subscribe(new Consumer<Integer>() {

            @Override
            public void accept(Integer integer) throws Exception {

                if (integer == 0) {
                    bindingView.vpGank.setCurrentItem(3);
                } else if (integer == 1) {
                    bindingView.vpGank.setCurrentItem(1);
                } else {
                    bindingView.vpGank.setCurrentItem(2);
                }
            }
        });
    }
}
