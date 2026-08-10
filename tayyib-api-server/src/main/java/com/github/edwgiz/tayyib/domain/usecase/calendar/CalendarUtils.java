package com.github.edwgiz.tayyib.domain.usecase.calendar;


import org.jspecify.annotations.Nullable;

import java.util.Set;

public final class CalendarUtils {

    private static final Set<String> SUNDAY_FIRST_COUNTRIES = Set.of(
            "US", "CA", "AU", "NZ", "PH", "JP", "KR", "TW", "HK", "SA", "EG", "IL");


    private CalendarUtils() {
    }


    public static boolean isSundayFirst(@Nullable String iso31661Alpha2) {
        return SUNDAY_FIRST_COUNTRIES.contains(iso31661Alpha2);
    }
}
