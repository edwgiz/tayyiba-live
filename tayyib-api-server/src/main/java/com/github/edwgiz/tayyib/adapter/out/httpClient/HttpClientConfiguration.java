package com.github.edwgiz.tayyib.adapter.out.httpClient;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import static org.springframework.http.HttpHeaders.USER_AGENT;


public class HttpClientConfiguration {

    @Bean
    RestClient.Builder restClientBuilder(@Value("${spring.application.name}") String applicationName) {
        final var result = RestClient.builder();
        result.defaultHeader(USER_AGENT, applicationName);
        return result;
    }
}
