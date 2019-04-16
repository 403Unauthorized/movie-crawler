package com.raja.crawler.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.raja.crawler.common.JsonResult;

@Controller
public class IndexController {

	private final Logger logger = LoggerFactory.getLogger(IndexController.class);
	
	private static final String MAOYAN_URL = "https://maoyan.com";
	
	private static final String MAOYAN_FILMS_URL = "https://maoyan.com/films";
	
	@RequestMapping("/view/index")
	public ModelAndView index() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("index");
		return mav;
	}
	
	@RequestMapping(value = "/api/execute", method = RequestMethod.GET)
	@ResponseBody
	public JsonResult execute() {
		JsonResult result = new JsonResult();
		Connection connect = null;
		Document doc;
		
		
		try {
			connect = Jsoup.connect(MAOYAN_FILMS_URL);
			Map<String, String> header = new HashMap<String, String>();
			header.put("Host", "http://info.bet007.com");
			header.put("User-Agent", "  Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0");
			header.put("Accept", "  text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			header.put("Accept-Language", "zh-cn,zh;q=0.5");
			header.put("Accept-Charset", "  GB2312,utf-8;q=0.7,*;q=0.7");
			header.put("Connection", "keep-alive");
			Connection data = connect.headers(header);
			doc = data.get();
			logger.info(doc.title());
			
			// 获取正在热映列表
//			Element container = doc.getElementsByClass("tab-movie-list").first();
			Element container = doc.getElementById("app");
			
			Element moviesChannel = container.select("div.movies-channel").first();
			
			Element moviesList = moviesChannel.select("div.movies-list").first();
			
			Element movieList = moviesList.select("dl.movie-list").first();
			
			Elements ddList = movieList.getElementsByTag("dd");
			
//			Elements movieCards = container.getElementsByClass("movie-card-wrap");
			
			for(Element e : ddList) {
				
				String url = e.getElementsByClass("movie-item")
						.first()
						.getElementsByTag("a")
						.first()
						.attr("href");
				String movieName = e.getElementsByClass("movie-item-title")
						.first()
						.attr("title");
				String score = e.getElementsByClass("channel-detail channel-detail-orange")
						.first()
						.getElementsByClass("integer").text()
						+ e.getElementsByClass("channel-detail channel-detail-orange")
						.first().getElementsByClass("fraction").text();
				
				if (StringUtils.isEmpty(score)) {
					score = "暂无评分";
				}
				
				logger.info(url + " - " + movieName + " - " + score);
				
				Connection details = null;
				
				try {
					details = Jsoup.connect(MAOYAN_URL + url);
					details.headers(header);
					Document detailsDoc = details.get();
					
					logger.info(detailsDoc.title());
				} catch (Exception e2) {
					// TODO: handle exception
				}
				
			}
			
			
			result.setCode(200);
			result.setFlag(true);
			
		} catch (IOException e) {
			result.setCode(500);
			result.setFlag(false);
			result.setMessage(e.getMessage());
		}
		
		return result;
	}
	
}
