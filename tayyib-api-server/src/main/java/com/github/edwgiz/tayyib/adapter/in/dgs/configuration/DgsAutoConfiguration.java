package com.github.edwgiz.tayyib.adapter.in.dgs.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.graphql.server.WebGraphQlHandler;
import tools.jackson.databind.json.JsonMapper;


@AutoConfiguration
public class DgsAutoConfiguration implements BeanDefinitionRegistryPostProcessor {

    /**
     * Replaces bean order for {@link name.nkonev.multipart.springboot.graphql.server.MultipartGraphQlWebMvcAutoconfiguration#graphQlMultipartRouterFunction(org.springframework.boot.graphql.autoconfigure.GraphQlProperties, WebGraphQlHandler, JsonMapper)}
     *
     * @param registry registry
     * @throws BeansException exception
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        registry.getBeanDefinition("graphQlMultipartRouterFunction")
                .setAttribute("order", -1);
    }
}
