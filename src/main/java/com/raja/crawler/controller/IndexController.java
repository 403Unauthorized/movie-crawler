package com.raja.crawler.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.raja.crawler.domain.MovieInfo;
import com.raja.crawler.service.ApiService;
import com.raja.crawler.utils.JsoupUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.raja.crawler.common.ResponseEntity;

@Controller
public class IndexController {

	private final Logger logger = LoggerFactory.getLogger(IndexController.class);
	
	private static final String MAOYAN_FILMS_URL = "https://maoyan.com/films?showType=3";

	private Integer defaultOffset = 0;

	private final ApiService service;

	public IndexController(ApiService apiService) {
		this.service = apiService;
	}
	
	@RequestMapping("/view/index")
	public ModelAndView index() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("index");
		return mav;
	}
	
	@RequestMapping(value = "/api/execute", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity execute() {
		ResponseEntity result = new ResponseEntity();

		int offset = defaultOffset;

		String movieUrl = MAOYAN_FILMS_URL;

		if (offset > 0) {
			movieUrl = movieUrl + "&offset=" + offset;
		}

		Document doc = JsoupUtils.getDocument(movieUrl);

		List<MovieInfo> movies = new ArrayList<>();

		Map<Integer, Object> map = new HashMap<>();

		while (doc != null) {
			logger.info(doc.title());

			movies = service.crawlMovies(doc);

			map.put(offset, movies);

			offset += 30;

			if (offset > 0) {
				movieUrl = MAOYAN_FILMS_URL + "&offset=" + offset;
			}

			doc = JsoupUtils.getDocument(movieUrl);
		}


		result.setCode(200);
		result.setData(movies);
		result.setFlag(true);
		
		return result;
	}
	
}
