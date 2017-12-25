package com.xl.test;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.xl.test.app.ConstantsImageUrl;
import com.xl.test.databinding.ActivityMainBinding;
import com.xl.test.databinding.NavHeaderMainBinding;
import com.xl.test.http.rxBus.RxBus;
import com.xl.test.http.rxBus.RxBusBaseMessage;
import com.xl.test.http.rxBus.RxCodeConstants;
import com.xl.test.ui.book.BookFragment;
import com.xl.test.ui.gank.GankFragment;
import com.xl.test.ui.one.OneFragment;
import com.xl.test.utils.ImageUtils;
import com.xl.test.utils.PerfectClickListener;
import com.xl.test.view.MyFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements ViewPager
        .OnPageChangeListener, View.OnClickListener {
    private FrameLayout llTitleMenu;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private NavigationView navView;
    private DrawerLayout drawerLayout;
    private ViewPager vpContent;
    private ActivityMainBinding mBinding;
    private ImageView llTitleGank;
    private ImageView llTitleOne;
    private ImageView llTitleDou;
    private NavHeaderMainBinding bind;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initView();
        loadData();
    }


    private void initView() {
        drawerLayout = mBinding.drawerLayout;
        navView = mBinding.navView;
        fab = mBinding.include.fab;
        toolbar = mBinding.include.toolbar;
        llTitleMenu = mBinding.include.llTitleMenu;
        vpContent = mBinding.include.vpContent;
        llTitleGank = mBinding.include.ivTitleGank;
        llTitleOne = mBinding.include.ivTitleOne;
        llTitleDou = mBinding.include.ivTitleDou;
        //初始化NavigationView的HeadView布局代码
        navView.inflateHeaderView(R.layout.nav_header_main);
        View headView = navView.getHeaderView(0);
        bind = DataBindingUtil.bind(headView);
        bind.setListener(this);
        setOnclickListener();
    }


    private void loadData() {

        fab.setVisibility(View.GONE);
        //保证drawerLayout打开后，状态栏与head保持一致

        ImageUtils.getInstance().displayCircle(bind.ivAvatar, ConstantsImageUrl.IC_AVATAR, R
                .drawable.ic_avatar_default);
        //添加actionbar，否则menu不显示
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowTitleEnabled(false);
        }
        mBinding.include.ivTitleGank.setSelected(true);
        initContentFragment();
        initRxBus();
    }


    private void setOnclickListener() {
        llTitleMenu.setOnClickListener(this);
        mBinding.include.ivTitleGank.setOnClickListener(this);
        mBinding.include.ivTitleDou.setOnClickListener(this);
        mBinding.include.ivTitleOne.setOnClickListener(this);
        fab.setOnClickListener(this);
        //NavigationView的HeadView布局代码点击
        bind.llNavExit.setOnClickListener(this);
        bind.ivAvatar.setOnClickListener(this);
        bind.llNavHomepage.setOnClickListener(listener);
        bind.llNavScanDownload.setOnClickListener(listener);
        bind.llNavDeedback.setOnClickListener(listener);
        bind.llNavAbout.setOnClickListener(listener);
        bind.llNavLogin.setOnClickListener(listener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_title_menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.iv_title_gank://干货
                //添加判断，减少cpu损耗
                if (vpContent.getCurrentItem() != 0) {
                    llTitleGank.setSelected(true);
                    llTitleOne.setSelected(false);
                    llTitleDou.setSelected(false);
                    vpContent.setCurrentItem(0);
                }
                break;
            case R.id.iv_title_one://电影
                if (vpContent.getCurrentItem() != 1) {
                    llTitleOne.setSelected(true);
                    llTitleGank.setSelected(false);
                    llTitleDou.setSelected(false);
                    vpContent.setCurrentItem(1);
                }
                break;
            case R.id.iv_title_dou://书籍
                if (vpContent.getCurrentItem() != 2) {
                    llTitleDou.setSelected(true);
                    llTitleOne.setSelected(false);
                    llTitleGank.setSelected(false);
                    vpContent.setCurrentItem(2);
                }
                break;
            case R.id.iv_avatar: // 头像进入GitHub
                break;
            case R.id.ll_nav_exit:// 退出应用
                finish();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Toast.makeText(this, "搜索", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                //进入后台程序，热启动加快启动速度
                moveTaskToBack(true);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void initContentFragment() {
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new GankFragment());
        fragmentList.add(new OneFragment());
        fragmentList.add(new BookFragment());
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),
                fragmentList);
        vpContent.setAdapter(adapter);
        vpContent.setOffscreenPageLimit(2);
        vpContent.addOnPageChangeListener(this);
        vpContent.setCurrentItem(0);
    }


    private PerfectClickListener listener = new PerfectClickListener() {
        @Override
        protected void onNoDoubleClick(final View v) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
            mBinding.drawerLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (v.getId()) {
                        case R.id.ll_nav_homepage:// 主页
                            Toast.makeText(MainActivity.this, "主页", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.ll_nav_scan_download://扫码下载
                            Toast.makeText(MainActivity.this, "扫码下载", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.ll_nav_deedback:// 问题反馈
                            Toast.makeText(MainActivity.this, "问题反馈", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.ll_nav_about:// 关于云阅
                            Toast.makeText(MainActivity.this, "关于云阅", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.ll_nav_login:// 登录GitHub账号
                            Toast.makeText(MainActivity.this, "登录GitHub账号", Toast.LENGTH_SHORT)
                                    .show();
                            break;

                    }
                }
            }, 260);
        }
    };

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                llTitleGank.setSelected(true);
                llTitleOne.setSelected(false);
                llTitleDou.setSelected(false);
                break;
            case 1:
                llTitleOne.setSelected(true);
                llTitleGank.setSelected(false);
                llTitleDou.setSelected(false);
                break;
            case 2:
                llTitleDou.setSelected(true);
                llTitleOne.setSelected(false);
                llTitleGank.setSelected(false);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void initRxBus() {

        RxBus.getInstance().tObservable(RxCodeConstants.JUMP_TYPE_TO_ONE, RxBusBaseMessage.class)
                .subscribe(new Consumer<RxBusBaseMessage>() {
                    @Override
                    public void accept(RxBusBaseMessage rxBusBaseMessage) throws Exception {
                        mBinding.include.vpContent.setCurrentItem(1);
                    }
                });

    }
}
