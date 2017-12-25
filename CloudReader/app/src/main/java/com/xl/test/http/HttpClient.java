package com.xl.test.http;

import com.xl.test.bean.FrontpageBean;
import com.xl.test.bean.GankIoDataBean;
import com.xl.test.bean.GankIoDayBean;
import com.xl.test.bean.HotMovieBean;
import com.xl.test.bean.MovieDetailBean;
import com.xl.test.bean.book.BookBean;
import com.xl.test.bean.book.BookDetailBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Created by hushendian on 2017/10/20.
 */

public interface HttpClient {


    class Builder {

        public static HttpClient getDouBanServer() {
            return HttpUtils.getInstance().getDouBanServer(HttpClient.class);
        }

        public static HttpClient getTingServer() {
            return HttpUtils.getInstance().getTingServer(HttpClient.class);
        }

        public static HttpClient getGankIOServer() {
            return HttpUtils.getInstance().getGankIOServer(HttpClient.class);
        }
    }

    /**
     * 首页轮播图
     */
    @GET("ting?from=android&version=5.8.1.0&channel=ppzs&operator=3&method=baidu.ting.plaza" +
            ".index&cuid=89CF1E1A06826F9AB95A34DC0F6AAA14")
    Observable<FrontpageBean> getFrontpage();

    /**
     * 每日数据： http://gank.io/api/day/年/月/日
     * eg:http://gank.io/api/day/2015/08/06
     */
    @GET("day/{year}/{month}/{day}")
    Observable<GankIoDayBean> getGankIoDay(@Path("year") String year, @Path("month") String
            month, @Path("day") String day);

    /**
     * 分类数据: http://gank.io/api/data/数据类型/请求个数/第几页
     * 数据类型： 福利 | Android | iOS | 休息视频 | 拓展资源 | 前端 | all
     * 请求个数： 数字，大于0
     * 第几页：数字，大于0
     * eg: http://gank.io/api/data/Android/10/1
     */
    @GET("data/{type}/{page}/{pre_page}")
    Observable<GankIoDataBean> getGankIoData(@Path("type") String id, @Path("page") int page,
                                             @Path("pre_page") int pre_page);

    /***
     * movie数据
     */
    @GET("v2/movie/in_theaters")
    Observable<HotMovieBean> getHotMovieData();

    /**
     * moviedetail 数据
     */
    @GET("v2/movie/subject/{id}")
    Observable<MovieDetailBean> getMovieDetailData(@Path("id") String id);

    /**
     * 获取豆瓣电影top250
     *
     * @param start 从多少开始，如从"0"开始
     * @param count 一次请求的数目，如"10"条，最多100
     */
    @GET("v2/movie/top250")
    Observable<HotMovieBean> getMovieTop250(@Query("start") int start, @Query("count") int count);

    /**
     * 根据Tag获取图书
     */
    @GET("v2/book/search")
    Observable<BookBean> getBook(@Query("tag") String tag, @Query("start") int start, @Query
            ("count") int count);
//
    @GET("v2/book/{id}")
    Observable<BookDetailBean> getBookDetail(@Path("id") String id);
}
