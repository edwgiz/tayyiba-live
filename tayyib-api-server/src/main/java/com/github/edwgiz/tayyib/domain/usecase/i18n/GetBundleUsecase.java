package com.github.edwgiz.tayyib.domain.usecase.i18n;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.edwgiz.tayyib.adapter.out.jdbc.I18nBundleSourcesRepository;
import com.github.edwgiz.tayyib.domain.model.I18n;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.JdbcUtils.tx;
import static org.apache.commons.lang3.StringUtils.EMPTY;


@Service
public class GetBundleUsecase {

    private static final Bindable<I18n> I18N_BINDABLE = Bindable.of(I18n.class);


    private final I18nBundleSourcesRepository i18nBundleSourcesRepository;
    private final DataSource dataSource;
    private final Cache<Locale, I18n> bundles;
    private final I18n defaultBundle;


    GetBundleUsecase(
            final I18nBundleSourcesRepository i18nBundleSourcesRepository,
            final Locale defaultLocale, DataSource dataSource) {
        this.i18nBundleSourcesRepository = i18nBundleSourcesRepository;
        this.dataSource = dataSource;
        final var defaultBundleSource = findBundleSource(defaultLocale);
        if (defaultBundleSource.isEmpty()) {
            throw new IllegalStateException("Can't initialize default i18n bundle for " + defaultLocale);
        }
        this.defaultBundle = buildBundle(defaultBundleSource);
        this.bundles = Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();
        apply(defaultLocale);
    }

    public I18n apply(final Locale locale) {
        return bundles.get(locale, this::getBundle);
    }

    private I18n getBundle(final Locale locale) {
        final var bundleSource = findBundleSource(locale);
        if (bundleSource.isEmpty()) {
            return defaultBundle;
        }
        return buildBundle(bundleSource);
    }

    private Map<String, Object> findBundleSource(final Locale defaultLocale) {
        return tx(dataSource, tx ->
                i18nBundleSourcesRepository.findBundleSource(defaultLocale.toLanguageTag(), tx));
    }

    private I18n buildBundle(final Map<String, Object> bundleSource) {
        final var standardEnvironment = new StandardEnvironment();
        final var propertySources = standardEnvironment.getPropertySources();
        propertySources.stream().map(PropertySource::getName).forEach(propertySources::remove);
        propertySources.addFirst(new MapPropertySource("jdbc", bundleSource));
        final var binder = Binder.get(standardEnvironment);
        final var result = binder.bind(EMPTY, I18N_BINDABLE);
        return result.get();
    }
}
