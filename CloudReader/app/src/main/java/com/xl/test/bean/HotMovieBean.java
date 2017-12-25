package com.xl.test.bean;

import com.xl.test.bean.moviechild.SubjectsBean;

import java.util.List;

/**
 * Created by hushendian on 2017/11/21.
 */

public class HotMovieBean {
    private int count;
    private int start;
    private int total;
    private List<SubjectsBean> subjects;
    private String title;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<SubjectsBean> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectsBean> subjects) {
        this.subjects = subjects;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "HotMovieBean{" +
                "count=" + count +
                ", start=" + start +
                ", total=" + total +
                ", subjects=" + subjects +
                ", title='" + title + '\'' +
                '}';
    }
}
