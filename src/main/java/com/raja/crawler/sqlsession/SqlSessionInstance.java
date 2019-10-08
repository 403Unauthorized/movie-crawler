package com.raja.crawler.sqlsession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SqlSessionInstance {

    private final SqlSessionFactory sqlSessionFactory;

    private volatile SqlSession sqlSession;

    public SqlSessionInstance(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public SqlSession getInstance() {
        if (sqlSession == null) {
            synchronized (SqlSessionInstance.class) {
                if (sqlSession == null) {
                    sqlSession = sqlSessionFactory.openSession(false);
                }
            }
        }
        return sqlSession;
    }
}
