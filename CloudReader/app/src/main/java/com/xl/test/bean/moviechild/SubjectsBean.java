package com.xl.test.bean.moviechild;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hushendian on 2017/11/20.
 */

public class SubjectsBean implements Serializable {

    private RatingBean rating;
    private List<String> genres;
    private String title;
    private List<PersonBean> casts;
    private int collect_count;
    private String original_title;
    private String subtype;
    private String year;
    private String alt;
    private String id;
    private List<PersonBean> directors;
    private ImagesBean images;

    public RatingBean getRating() {
        return rating;
    }

    public void setRating(RatingBean rating) {
        this.rating = rating;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<PersonBean> getCasts() {
        return casts;
    }

    public void setCasts(List<PersonBean> casts) {
        this.casts = casts;
    }

    public int getCollect_count() {
        return collect_count;
    }

    public void setCollect_count(int collect_count) {
        this.collect_count = collect_count;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PersonBean> getDirectors() {
        return directors;
    }

    public void setDirectors(List<PersonBean> directors) {
        this.directors = directors;
    }

    public ImagesBean getImages() {
        return images;
    }

    public void setImages(ImagesBean images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "SubjectsBean{" +
                "rating=" + rating +
                ", genres=" + genres +
                ", title='" + title + '\'' +
                ", casts=" + casts +
                ", collect_count=" + collect_count +
                ", original_title='" + original_title + '\'' +
                ", subtype='" + subtype + '\'' +
                ", year='" + year + '\'' +
                ", alt='" + alt + '\'' +
                ", id='" + id + '\'' +
                ", directors=" + directors +
                ", images=" + images +
                '}';
    }
}
