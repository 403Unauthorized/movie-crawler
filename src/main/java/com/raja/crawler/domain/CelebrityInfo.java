package com.raja.crawler.domain;

import lombok.Data;

import java.util.Date;

@Data
public class CelebrityInfo {
    private Long id;
    private String cname;
    private String alias;
    private String ename;
    private String tags;
    private Date birthday;
    private String birthPlace;
    private Integer height;
    private String nationality;
    private String minzu;
    private String gender;
    private String graduatedSchool;
    private String description;
}