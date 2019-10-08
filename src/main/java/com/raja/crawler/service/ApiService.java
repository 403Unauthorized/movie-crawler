package com.raja.crawler.service;

import com.raja.crawler.domain.MovieInfo;
import com.raja.crawler.mapper.MovieMapper;
import com.raja.crawler.sqlsession.SqlSessionInstance;
import com.raja.crawler.utils.DateUtil;
import com.raja.crawler.utils.JsoupUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeFilter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 数据爬取业务层
 * @author leiyongqi
 */
@Slf4j
@Service
public class ApiService {

    private static final String MAOYAN_URL = "https://maoyan.com";

    private static final int LEADING_ACTORS_NUM = 5;

    private Integer defaultOffset = 0;

    private final MovieMapper movieMapper;

    public ApiService(MovieMapper movieMapper) {
        this.movieMapper = movieMapper;
    }

    public List<MovieInfo> crawlMovies(Document doc) {
        Elements elements = getMovieList(doc);

        List<MovieInfo> movies = new ArrayList<>();

        for (Element e : elements) {
            String movieUrl = e.getElementsByClass("movie-item")
                    .first()
                    .getElementsByTag("a")
                    .first()
                    .attr("href");

            String score = e.getElementsByClass("channel-detail channel-detail-orange")
                    .first()
                    .getElementsByClass("integer").text()
                    + e.getElementsByClass("channel-detail channel-detail-orange")
                    .first().getElementsByClass("fraction").text();
            MovieInfo movie = movieInfo(MAOYAN_URL + movieUrl, score);

        }

        return movies;
    }

    private MovieInfo movieInfo(String url, String score) {
        Document detailsDoc = JsoupUtils.getDocument(url);
        MovieInfo movie = new MovieInfo();

        movie.setScore(score);

        // 获取影片主要信息
        Element banner = detailsDoc.selectFirst("div.banner");
        Element wrapper = banner.selectFirst("div.wrapper");
        // 封面图片div
        Element left = wrapper.selectFirst("div.celeInfo-left");
        // 封面图片 img 标签
        Element img = left.selectFirst("div.avatar-shadow").selectFirst("img.avatar");
        movie.setCoverImageUrl(img.attr("src"));
        // 影片名称等信息
        Element right = wrapper.selectFirst("div.celeInfo-right");
        // 影片名称
        Element movieBrief = right.selectFirst("div.movie-brief-container");
        movie.setTitle(movieBrief.selectFirst("h3.name").text());
        // 英文名称
        movie.setEtitle(movieBrief.selectFirst("div.ename").text());

        Element ul = movieBrief.getElementsByTag("ul").first();
        Elements lis = ul.getElementsByTag("li");

        if (lis.size() > 0) {
            movie.setTags(lis.get(0).text());
            String[] str = lis.get(1).text().split("/");

            movie.setProductionCountry(str[0]);
            if (str.length > 1) {
                movie.setDuration(Integer.valueOf(StringUtils.trimWhitespace(str[1].substring(0, str[1].indexOf("分钟")))));
            }


            String releaseDateText = lis.get(2).text();
            if (!StringUtils.isEmpty(releaseDateText)) {
                boolean release = releaseDateText.contains("大陆");

                if (release) {
                    movie.setReleaseRegion("中国大陆");
                }
                String releaseDate = "";
                try {
                    releaseDate = releaseDateText.substring(0, !release ? 10 : releaseDateText.indexOf("大陆"));
                } catch (StringIndexOutOfBoundsException e) {
                    releaseDate = releaseDateText.substring(0, 4);
                }
                if (!StringUtils.isEmpty(releaseDate)) {
                    movie.setReleaseDate(DateUtil.strToDate(releaseDate, DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI));
                }
            }
        }


        Element appContainer = detailsDoc.getElementById("app");
        Element mainContent = appContainer.selectFirst("div.main-content-container");
        Element tabContainer = mainContent.selectFirst("div.main-content").selectFirst("div.tab-container");
        //
        Element elem = tabContainer.selectFirst("div.tab-content-container");
        // 介绍
        Element desc = elem.selectFirst("div.tab-desc");

        Element descriptionModule = desc.select("div.module").first();

        // 剧情简介
        String summary = new String(descriptionModule.selectFirst("div.mod-content").selectFirst("span.dra").text().getBytes(), StandardCharsets.UTF_8);
        movie.setDescription(summary);

        // 演职人员
        Element castMember = elem.selectFirst("div.tab-celebrity");
        Element celebrityContainer = castMember.selectFirst("div.celebrity-container");
        Elements celebrityGroups = celebrityContainer.select("div.celebrity-group");
        // 导演
        Element director = celebrityGroups.get(0);
        Element directorListUl = director.selectFirst("ul.celebrity-list");
        Elements directorList = directorListUl.getElementsByTag("li");

        movie.setDirectors(setMovieCelebrities(directorList));

        if (celebrityGroups.size() > 1) {
            // 演员
            Element actor = celebrityGroups.get(1);
            Element actorListUl = actor.selectFirst("ul.celebrity-list");
            Elements actorList = actorListUl.getElementsByTag("li");

            movie.setLeadingActors(setMovieCelebrities(actorList));
        }

        log.info(movie.toString());
        int count = movieMapper.insert(movie);

        log.info("数据添加：{} 条。", count);

        return movie;
    }

    private String setMovieCelebrities(Elements list) {
        StringBuilder names = new StringBuilder();
        int leading = 0;
        for (Element e : list) {
            if (leading >= LEADING_ACTORS_NUM) {
                break;
            }
            Element info = e.selectFirst("div.info").getElementsByTag("a").first();
            if (!StringUtils.isEmpty(names.toString())) {
                names.append(",");
            }
            names.append(info.text());

            // TODO: 添加演职人员信息
            String url = info.attr("href");
            leading++;
        }
        return names.toString();
    }

    private Elements getMovieList(Document doc) {
        Element container = doc.getElementById("app");
        Element moviesChannel = container.selectFirst("div.movies-channel");
        Element moviesList = moviesChannel.selectFirst("div.movies-list");
        Element movieList= moviesList.selectFirst("dl.movie-list");
        return movieList.getElementsByTag("dd");
    }

}
