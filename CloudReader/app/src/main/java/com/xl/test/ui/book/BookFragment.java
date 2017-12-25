package com.xl.test.ui.book;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import com.xl.test.R;
import com.xl.test.base.LazyFragment;
import com.xl.test.databinding.FragmentBookBinding;
import com.xl.test.ui.book.child.BookCustomFragment;
import com.xl.test.view.MyFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hushendian on 2017/10/17.
 */

public class BookFragment extends LazyFragment<FragmentBookBinding> {
    private List<String> mTitleList = new ArrayList<>();
    private List<Fragment> mFragments = new ArrayList<>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showLoading();
        initFragment();
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getChildFragmentManager(),
                mFragments, mTitleList);
        bindingView.vpBook.setAdapter(adapter);
        bindingView.vpBook.setOffscreenPageLimit(2);
        adapter.notifyDataSetChanged();
        bindingView.tabBook.setTabMode(TabLayout.MODE_FIXED);
        bindingView.tabBook.setupWithViewPager(bindingView.vpBook);
        showContentView();
    }

    @Override
    protected void onInvisible() {

    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public int setContentView() {
        return R.layout.fragment_book;
    }

    private void initFragment() {
        mTitleList.add("文学");
        mTitleList.add("文化");
        mTitleList.add("生活");
        mFragments.add(BookCustomFragment.newInstance("文学"));
        mFragments.add( BookCustomFragment.newInstance("文化"));
        mFragments.add( BookCustomFragment.newInstance("生活"));
    }
}
