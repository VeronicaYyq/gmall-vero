package com.atguigu.gmall.gateway.config;

import org.apache.commons.io.filefilter.FalseFileFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.cors.CorsConfiguration;

import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/*
* 解决跨域问题
* 初始化corsfilter--->初始化cors配置源(允许所有请求UrlBasedCorsConfigurationSource)-->初始化配置对象（CorsConfiguration）
*
*
* */
@Configuration
public class GmallCorsConfiguration {
    @Bean
    public CorsWebFilter corsWebFilter(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:1000");//允许跨域的域名
        corsConfiguration.addAllowedMethod("*");//允许跨域的方法
        corsConfiguration.addAllowedHeader("*");//允许携带任何的头信息
        corsConfiguration.setAllowCredentials(true);//是否允许携带cookie

        UrlBasedCorsConfigurationSource urlBasedSource = new UrlBasedCorsConfigurationSource();
        urlBasedSource.registerCorsConfiguration("/**",corsConfiguration);
        return new CorsWebFilter(urlBasedSource);
    }
}
