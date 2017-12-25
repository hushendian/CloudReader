package com.xl.test.model;

import android.util.Log;

import com.xl.test.app.ConstantsImageUrl;
import com.xl.test.bean.AndroidBean;
import com.xl.test.bean.FrontpageBean;
import com.xl.test.bean.GankIoDayBean;
import com.xl.test.http.HttpClient;
import com.xl.test.http.RequestImpl;
import com.xl.test.http.cache.ACache;
import com.xl.test.utils.RxObservableUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by hushendian on 2017/10/19.
 */

public class EverydayModel {
    private static String TAG = "EverydayModel";
    private String year = "2017";
    private String month = "10";
    private String day = "20";
    private static EverydayModel instance;
    private static final String HOME_ONE = "home_one";
    private static final String HOME_TWO = "home_two";
    private static final String HOME_SIX = "home_six";

    public static EverydayModel getInstance() {
        if (instance == null) {
            synchronized (ACache.class) {
                if (instance == null) {
                    instance = new EverydayModel();
                }
            }
        }
        return instance;
    }

    public void setData(String year, String month, String day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    /**
     * 请求banner数据
     *
     * @param listener
     */
    public void showBanncerPage(final RequestImpl listener) {
        Disposable disposable = HttpClient.Builder.getTingServer()
                .getFrontpage()
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Consumer<FrontpageBean>() {
                    @Override
                    public void accept(FrontpageBean frontpageBean) throws Exception {
                        listener.loadSuccess(frontpageBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        listener.loadFailed(throwable);
                    }
                });
        listener.addDisposable(disposable);
    }

    /**
     * 显示RecyclerView数据
     */
    public void showRecyclerViewData(final RequestImpl listener) {

        Function<GankIoDayBean, Observable<List<List<AndroidBean>>>> function = new
                Function<GankIoDayBean, Observable<List<List<AndroidBean>>>>() {


                    @Override
                    public Observable<List<List<AndroidBean>>> apply(GankIoDayBean gankIoDayBean)
                            throws
                            Exception {
                        List<List<AndroidBean>> lists = new ArrayList<>();
                        GankIoDayBean.ResultsBean results = gankIoDayBean.getResults();


                        if (results.getAndroid() != null && results.getAndroid().size() > 0) {
                            addUrlList(lists, results.getAndroid(), "Android");
                        }
                        if (results.getWelfare() != null && results.getWelfare().size() > 0) {
                            addUrlList(lists, results.getWelfare(), "福利");
                        }
                        if (results.getiOS() != null && results.getiOS().size() > 0) {
                            addUrlList(lists, results.getiOS(), "IOS");
                        }
                        if (results.getRestMovie() != null && results.getRestMovie().size() > 0) {
                            addUrlList(lists, results.getRestMovie(), "休息视频");
                        }
                        if (results.getResource() != null && results.getResource().size() > 0) {
                            addUrlList(lists, results.getResource(), "拓展资源");
                        }
                        if (results.getRecommend() != null && results.getRecommend().size() > 0) {

                            addUrlList(lists, results.getRecommend(), "瞎推荐");
                        }
                        if (results.getFront() != null && results.getFront().size() > 0) {
                            addUrlList(lists, results.getFront(), "前端");
                        }
                        if (results.getApp() != null && results.getApp().size() > 0) {
                            addUrlList(lists, results.getApp(), "App");
                        }
                        for(List<AndroidBean> list:lists){
                            Log.d(TAG, "apply: "+list.size());
                        }
                        return Observable.just(lists);
                    }
                };


        Consumer<List<List<AndroidBean>>> consumer = new Consumer<List<List<AndroidBean>>>() {
            @Override
            public void accept(List<List<AndroidBean>> lists) throws Exception {
                listener.loadSuccess(lists);
            }
        };
        Consumer<Throwable> throwable = new Consumer<Throwable>() {

            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d(TAG, "accept: " + throwable.getMessage());
                listener.loadFailed(throwable);
            }
        };
        Log.d(TAG, "showRecyclerViewData: " + year + "-" + month + "-" + day);
        // TODO: 2017/11/2 此处粗暴赋值，是为了解决请求不到数据时的情况，可以走缓存
        year = "2017";
        month = "11";
        day = "1";

        Disposable disposable = HttpClient.Builder.getGankIOServer()
                .getGankIoDay(year, month, day)
                .compose(RxObservableUtils
                        .<GankIoDayBean>applySchedulers())
                .flatMap(function).subscribe(consumer,
                        throwable);
        listener.addDisposable(disposable);

    }

    // subList没有实现序列化！缓存时会出错！

    /**
     * @param lists     初始值为null
     * @param arrayList Android中包含5项
     * @param typeTitle 类型起始值为Android
     */
    private void addUrlList(List<List<AndroidBean>> lists, List<AndroidBean> arrayList, String
            typeTitle) {
        // 先把title放到首位
        AndroidBean bean = new AndroidBean();
        bean.setType_title(typeTitle);
        ArrayList<AndroidBean> androidBeen = new ArrayList<>();
        androidBeen.add(bean);
        lists.add(androidBeen);

        int androidSize = arrayList.size();
        if (androidSize > 0 && androidSize < 4) {

            lists.add(addUrlList(arrayList, androidSize));
        } else if (androidSize >= 4) {

            ArrayList<AndroidBean> list1 = new ArrayList<>();
            ArrayList<AndroidBean> list2 = new ArrayList<>();

            for (int i = 0; i < androidSize; i++) {
                //0~2
                if (i < 3) {
                    list1.add(getAndroidBean(arrayList, i, androidSize));
                } else if (i < 6) {
                    //3~4
                    list2.add(getAndroidBean(arrayList, i, androidSize));
                }
            }
            lists.add(list1);
            Log.d(TAG, "addUrlList: "+lists.size());
            lists.add(list2);
            Log.d(TAG, "addUrlList: "+lists.size());
        }
    }

    /**
     * android 为例
     * @param arrayList
     * @param i
     * @param androidSize 5
     * @return
     */
    private AndroidBean getAndroidBean(List<AndroidBean> arrayList, int i, int androidSize) {

        AndroidBean androidBean = new AndroidBean();
        // 标题
        androidBean.setDesc(arrayList.get(i).getDesc());
        // 类型
        androidBean.setType(arrayList.get(i).getType());
        // 跳转链接
        androidBean.setUrl(arrayList.get(i).getUrl());
        // 随机图的url
        if (i < 3) {
            androidBean.setImage_url(ConstantsImageUrl.HOME_SIX_URLS[getRandomData
                    (ConstantsImageUrl.HOME_ONE_URLS.length)]);//三小图
        } else if (androidSize == 4) {
            androidBean.setImage_url(ConstantsImageUrl.HOME_ONE_URLS[getRandomData
                    (ConstantsImageUrl.HOME_ONE_URLS.length)]);//一图
        } else if (androidSize == 5) {
            androidBean.setImage_url(ConstantsImageUrl.HOME_TWO_URLS[getRandomData
                    (ConstantsImageUrl.HOME_TWO_URLS.length)]);//两图
        } else if (androidSize >= 6) {
            androidBean.setImage_url(ConstantsImageUrl.HOME_SIX_URLS[getRandomData
                    (ConstantsImageUrl.HOME_SIX_URLS.length)]);//三小图
        }
        return androidBean;
    }


    private List<AndroidBean> addUrlList(List<AndroidBean> arrayList, int androidSize) {
        List<AndroidBean> tempList = new ArrayList<>();

        for (int i = 0; i < androidSize; i++) {
            AndroidBean androidBean = new AndroidBean();
            // 标题
            androidBean.setDesc(arrayList.get(i).getDesc());
            // 类型
            androidBean.setType(arrayList.get(i).getType());
            // 跳转链接
            androidBean.setUrl(arrayList.get(i).getUrl());
//            DebugUtil.error("---androidSize:  " + androidSize);
            // 随机图的url
//            if (androidSize == 1) {
//                androidBean.setImage_url(ConstantsImageUrl.HOME_ONE_URLS[getRandom(1)]);//一图
//            } else if (androidSize == 2) {
//                androidBean.setImage_url(ConstantsImageUrl.HOME_TWO_URLS[getRandom(2)]);//两图
//            } else if (androidSize == 3) {
//                androidBean.setImage_url(ConstantsImageUrl.HOME_SIX_URLS[getRandom(3)]);//三图
//            }
            if (androidSize == 1) {
                androidBean.setImage_url(ConstantsImageUrl.HOME_ONE_URLS[getRandomData
                        (ConstantsImageUrl.HOME_ONE_URLS.length)]);//一图
            } else if (androidSize == 2) {
                androidBean.setImage_url(ConstantsImageUrl.HOME_TWO_URLS[getRandomData
                        (ConstantsImageUrl.HOME_TWO_URLS.length)]);//两图
            } else if (androidSize == 3) {
                androidBean.setImage_url(ConstantsImageUrl.HOME_SIX_URLS[getRandomData
                        (ConstantsImageUrl.HOME_SIX_URLS.length)]);//三图
            }
            tempList.add(androidBean);
        }
        return tempList;
    }


    private int getRandomData(int length) {
        Random random = new Random();
        return random.nextInt(length);
    }

//    /**
//     * 取不同的随机图，在每次网络请求时重置
//     */
//    private int getRandom(int type) {
//        String saveWhere = null;
//        int urlLength = 0;
//        if (type == 1) {
//            saveWhere = HOME_ONE;
//            urlLength = ConstantsImageUrl.HOME_ONE_URLS.length;
//        } else if (type == 2) {
//            saveWhere = HOME_TWO;
//            urlLength = ConstantsImageUrl.HOME_TWO_URLS.length;
//        } else if (type == 3) {
//            saveWhere = HOME_SIX;
//            urlLength = ConstantsImageUrl.HOME_SIX_URLS.length;
//        }
//
//        String home_six = SPUtils.getString(saveWhere, "");
//        Log.d(TAG, "getRandom: " + "----------------" + urlLength + "============" + home_six);
//        if (!TextUtils.isEmpty(home_six)) {
//            // 已取到的值
//            String[] split = home_six.split(",");
//
//            Random random = new Random();
//            for (int j = 0; j < urlLength; j++) {
//                int randomInt = random.nextInt(urlLength);
//
//                boolean isUse = false;
//                for (String aSplit : split) {
//                    if (!TextUtils.isEmpty(aSplit) && String.valueOf(randomInt).equals(aSplit)) {
//                        isUse = true;
//                        break;
//                    }
//                }
//                if (!isUse) {
//                    StringBuilder sb = new StringBuilder(home_six);
//                    sb.insert(0, randomInt + ",");
//                    SPUtils.putString(saveWhere, sb.toString());
//                    return randomInt;
//                }
//            }
//
//        } else {
//            Random random = new Random();
//            int randomInt = random.nextInt(urlLength);
//            SPUtils.putString(saveWhere, randomInt + ",");
//            return randomInt;
//        }
//        return 0;
//    }


}


