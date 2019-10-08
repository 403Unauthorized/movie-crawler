package com.raja.crawler.domain;

import lombok.Data;

import java.util.Date;

@Data
public class MovieInfo {
    private Long id;
    private String title;
    private String subTitle;
    private String etitle;
    private String types;
    private String tags;
    private String directors;
    private String leadingActors;
    private Integer duration;
    private String productionCountry;
    private String releaseRegion;
    private Date releaseDate;
    private String coverImageUrl;
    private String description;
    private String score;
    private Long scoreNum;
}
