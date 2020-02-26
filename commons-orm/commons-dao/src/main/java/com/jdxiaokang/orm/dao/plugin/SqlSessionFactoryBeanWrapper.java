package com.jdxiaokang.orm.dao.plugin;

import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;

public class SqlSessionFactoryBeanWrapper implements FactoryBean<SqlSessionFactory> {

    private DataSource dataSource;
    private Interceptor[] interceptors;
    private String config;
    private String mapperLocations;
    private SqlSessionFactoryBuilder mySqlSessionFactoryBuilder;
    @Autowired
    private ResourcePatternResolver resourceLoader;

    public SqlSessionFactoryBeanWrapper() {
    }

    public String getConfig() {
        return this.config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getMapperLocations() {
        return this.mapperLocations;
    }

    public void setMapperLocations(String mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public SqlSessionFactoryBeanWrapper dataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public SqlSessionFactoryBeanWrapper interceptors(Interceptor[] interceptors) {
        this.interceptors = interceptors;
        return this;
    }

    public SqlSessionFactoryBeanWrapper config(String config) {
        this.config = config;
        return this;
    }

    public SqlSessionFactoryBeanWrapper mapperLocations(String mapperLocations) {
        this.mapperLocations = mapperLocations;
        return this;
    }

    public SqlSessionFactoryBeanWrapper mySqlSessionFactoryBuilder(SqlSessionFactoryBuilder mySqlSessionFactoryBuilder) {
        this.mySqlSessionFactoryBuilder = mySqlSessionFactoryBuilder;
        return this;
    }

    public SqlSessionFactoryBeanWrapper build() {
        return this;
    }

    public SqlSessionFactory getObject() throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(this.dataSource);
        factory.setVfs(SpringBootVFS.class);
        factory.setConfigLocation(this.resourceLoader.getResource(this.config));
        factory.setSqlSessionFactoryBuilder(this.mySqlSessionFactoryBuilder);
        if (this.mapperLocations != null && !this.mapperLocations.trim().equals("")) {
            factory.setMapperLocations(this.resourceLoader.getResources(this.mapperLocations));
        }

        if (this.interceptors != null) {
            factory.setPlugins(this.interceptors);
        }

        return factory.getObject();
    }

    public Class<?> getObjectType() {
        return SqlSessionFactory.class;
    }

    public boolean isSingleton() {
        return true;
    }

}