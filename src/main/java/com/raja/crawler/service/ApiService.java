package com.raja.crawler.service;

import com.raja.crawler.utils.JsoupUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据爬取业务层
 * @author leiyongqi
 */
@Service
public class ApiService {

    public boolean movieInfo(String url) {
        Document doc = JsoupUtils.getDocument(url);

        return true;
    }

    private Elements getMovieList(Document doc) {
        Element container = doc.getElementById("app");
        Element moviesChannel = container.selectFirst("div.movies-channel");
        Element moviesList = moviesChannel.selectFirst("div.movies-list");
        Element movieList= moviesList.selectFirst("dl.movie-list");
        return movieList.getElementsByTag("dd");
    }

}
