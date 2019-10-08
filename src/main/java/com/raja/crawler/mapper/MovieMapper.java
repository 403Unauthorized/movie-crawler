package com.raja.crawler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.raja.crawler.domain.MovieInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author leiyongqi
 */
@Mapper
public interface MovieMapper extends BaseMapper<MovieInfo> {


}
