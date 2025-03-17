package com.github.edwgiz.tayyib.adapter.in.dgs.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.graphql.dgs.scalars.UploadScalar;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.WebGraphQlHandler;

//@Configuration
//@ComponentScan(basePackageClasses = UploadScalar.class)
public class DgsAutoConfiguration implements BeanDefinitionRegistryPostProcessor {
// MultipartGraphQlWebMvcAutoconfiguration

    /**
     * Replaces bean order for {@link name.nkonev.multipart.springboot.graphql.server.MultipartGraphQlWebMvcAutoconfiguration#graphQlMultipartRouterFunction(GraphQlProperties, WebGraphQlHandler, ObjectMapper)}
     *
     * @param registry registry
     * @throws BeansException exception
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
/*
        registry.getBeanDefinition("graphQlMultipartRouterFunction")
                .setAttribute("order", -1);
*/
    }
}
