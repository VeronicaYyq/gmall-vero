package com.atguigu.gmall.sms.config;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 数据源配置
 *
 * @author HelloWoodes
 */
@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public HikariDataSource dataSource(@Value("${spring.datasource.url}")String url) {

        HikariDataSource dataSource=new HikariDataSource();
        dataSource.setJdbcUrl(url);
        return dataSource;
    }

    /**
     * 需要将 DataSourceProxy 设置为主数据源，否则事务无法回滚
     *
     *
     * @return The default datasource
     */
    @Primary
    @Bean("dataSource")

    public DataSource dataSource(DataSource dataSource) {
        return new DataSourceProxy(dataSource);
    }
}
