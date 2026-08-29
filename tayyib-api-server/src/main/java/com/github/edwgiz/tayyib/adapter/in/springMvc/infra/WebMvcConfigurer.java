package com.github.edwgiz.tayyib.adapter.in.springMvc.infra;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.resource.EncodedResourceResolver;


public class WebMvcConfigurer implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:webapp/static")
                .setCachePeriod(0)
                .resourceChain(true)
                .addResolver(new EncodedResourceResolver());
    }
}
