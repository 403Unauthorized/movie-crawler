package com.raja.crawler.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * 猫眼电影页面专用JsoupUtil
 * @author leiyongqi
 */
public class JsoupUtils {

    private static final Map<String, String> header;

    static {
        header = new HashMap<>();
        header.put("Host", "http://info.bet007.com");
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0");
        header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        header.put("Accept-Language", "zh-cn,zh;q=0.5");
        header.put("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
        header.put("Connection", "keep-alive");
    }

    public static Document getDocument(String url) {
        Connection connection = Jsoup.connect(url);
        try {
            connection.headers(header);
            return connection.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
