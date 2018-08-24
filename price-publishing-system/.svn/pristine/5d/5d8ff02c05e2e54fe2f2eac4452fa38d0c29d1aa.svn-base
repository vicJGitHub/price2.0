package com.hywa.pricepublish;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@EnableAutoConfiguration
//配置druid必须加的注解，如果不加，访问页面打不开，filter和servlet、listener之类的需要单独进行注册才能使用，
@ServletComponentScan
@MapperScan(basePackages = "com.hywa.pricepublish.dao.mapper")
//扫描额外的配置文件
@PropertySource("classpath:properties/info.properties")
public class PricePublishingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PricePublishingSystemApplication.class, args);
    }

}