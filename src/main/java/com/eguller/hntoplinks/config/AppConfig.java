package com.eguller.hntoplinks.config;

import com.eguller.hntoplinks.entities.HnEntity;
import com.eguller.hntoplinks.repository.Sequence;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.relational.core.mapping.event.BeforeSaveEvent;
import org.springframework.mobile.device.DeviceHandlerMethodArgumentResolver;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Configuration
@EnableScheduling
public class AppConfig implements WebMvcConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private Sequence sequence;
    @Bean
    public DeviceResolverHandlerInterceptor deviceResolverHandlerInterceptor() {
        return new DeviceResolverHandlerInterceptor(); 
    }

    @Bean
    public DeviceHandlerMethodArgumentResolver deviceHandlerMethodArgumentResolver() {
        return new DeviceHandlerMethodArgumentResolver(); 
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(deviceResolverHandlerInterceptor()); 
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(deviceHandlerMethodArgumentResolver()); 
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder){
        return restTemplateBuilder
                .setConnectTimeout(Duration.of(5, ChronoUnit.SECONDS))
           .setReadTimeout(Duration.of(30, ChronoUnit.SECONDS))
           .build();
    }

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }

    @Bean
    public ApplicationListener<BeforeSaveEvent<Object>> assignId() {
        return event -> {
            Object entity = event.getEntity();
            if(entity instanceof HnEntity){
                ((HnEntity) entity).setId(sequence.getNextId());
            }
        };
    }
}