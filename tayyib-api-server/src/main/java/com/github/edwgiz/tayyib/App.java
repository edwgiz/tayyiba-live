package com.github.edwgiz.tayyib;

import com.github.edwgiz.tayyib.domain.usecase.i18n.ListSupportedLocalesUsecase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;


@SpringBootApplication
class App {

    public static void main(final String[] args) {
        SpringApplication.run(App.class, args);
    }


    @Bean
    @DependsOn("liquibase")
    LocaleResolver localeResolver(
            final ListSupportedLocalesUsecase listSupportedLocalesUsecase,
            final Locale defaultLocale) {
        final var localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setSupportedLocales(listSupportedLocalesUsecase.apply());
        localeResolver.setDefaultLocale(defaultLocale);
        return localeResolver;
    }


    @Bean
    Locale defaultLocale() {
        return Locale.ENGLISH;
    }
}
