package com.github.edwgiz.tayyib.domain.usecase.i18n;


import org.jspecify.annotations.Nullable;

import java.util.Enumeration;
import java.util.Locale;

import static java.util.Locale.ROOT;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public final class LocaleUtils {

    private LocaleUtils() {
    }


    public static @Nullable String getCountry(Enumeration<Locale> locales) {
        while (locales.hasMoreElements()) {
            final var locale = locales.nextElement();
            final var iso3166_1_alpha2 = locale.getCountry();
            if (isNotEmpty(iso3166_1_alpha2)) {
                return iso3166_1_alpha2.toUpperCase(ROOT);
            }
        }
        return null;
    }
}
