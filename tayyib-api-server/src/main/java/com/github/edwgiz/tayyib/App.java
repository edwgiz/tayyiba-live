package com.github.edwgiz.tayyib;

import com.github.edwgiz.tayyib.domain.usecase.i18n.ListSupportedLocalesUsecase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;


@SpringBootApplication
class App {

    public static void main(final String[] args) {
        SpringApplication.run(App.class, args);
    }


    @Bean
    LocaleResolver localeResolver(
            final List<Locale> supportedLocales,
            final Locale defaultLocale
            ) {
        final var localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setSupportedLocales(supportedLocales);
        localeResolver.setDefaultLocale(defaultLocale);
        return localeResolver;
    }


    @Bean
    @DependsOn("liquibase")
    List<Locale> supportedLocales(final ListSupportedLocalesUsecase listSupportedLocalesUsecase) {
        return listSupportedLocalesUsecase.apply();
    }


    @Bean
    Locale defaultLocale() {
        return Locale.ENGLISH;
    }
}
