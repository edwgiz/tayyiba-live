package com.github.edwgiz.tayyib.adapter.in.springMvc.infra;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.resource.EncodedResourceResolver;

import java.util.List;


public class WebMvcConfigurer implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        final var encodedResourceResolver = new EncodedResourceResolver();
        encodedResourceResolver.setContentCodings(List.of("br")); // leave Brotli only
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:webapp/static")
                .setCachePeriod(0)
                .resourceChain(true)
                .addResolver(encodedResourceResolver);
    }
}
