package com.github.edwgiz.tayyib.adapter.in.dgs.fetcher;

import com.github.edwgiz.tayyib.adapter.commons.dgs.dto.GetCalendarCellsResult;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.netflix.graphql.dgs.internal.DgsWebMvcRequestData;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.LocaleResolver;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import static java.util.Locale.ROOT;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@DgsComponent
public class CalendarFetcher {

    private final static Set<String> SUNDAY_FIRST_COUNTRIES = Set.of(
            "US", "CA", "AU", "NZ", "PH", "JP", "KR", "TW", "HK", "SA", "EG", "IL");

    private final LocaleResolver localeResolver;


    CalendarFetcher(final LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }


    @DgsQuery
    GetCalendarCellsResult getCalendarCells(
            final @InputArgument Long millis,
            final @InputArgument String timezone,
            final DgsDataFetchingEnvironment dfe) {

        final var requestData = getRequestData(dfe);


        final var dayOfWeeks = DayOfWeek.values();
        final var dayOfWeekNames = new String[7];
        int dayOfWeekOffset = SUNDAY_FIRST_COUNTRIES.contains(requestData.iso3166_2) ? 6 : 0;
        for (int i = 0; i < dayOfWeekNames.length; i++) {
            dayOfWeekNames[i] = dayOfWeeks[(i + dayOfWeekOffset) % 7].getDisplayName(TextStyle.SHORT, requestData.locale);
        }

        return new GetCalendarCellsResult(
                Arrays.asList(dayOfWeekNames)
        );
    }


    private RequestData getRequestData(final DgsDataFetchingEnvironment dfe) {
        final var dgsRequestData = (DgsWebMvcRequestData) dfe.getDgsContext().getRequestData();
        if (dgsRequestData == null) {
            throw new IllegalStateException("Can't get dgs request data");
        }
        final var dgsWebRequest = (ServletWebRequest) dgsRequestData.getWebRequest();
        if (dgsWebRequest == null) {
            throw new IllegalStateException("Can't get dgs web request");
        }
        final var servletRequest = (HttpServletRequest) dgsWebRequest.getNativeRequest();

        return new RequestData(
                localeResolver.resolveLocale(servletRequest),
                getCountry(servletRequest));
    }


    private record RequestData(
            Locale locale,
            @Nullable String iso3166_2
    ) {
    }


    private static @Nullable String getCountry(final HttpServletRequest servletRequest) {
        final var acceptedLocales = servletRequest.getLocales();
        while (acceptedLocales.hasMoreElements()) {
            final var acceptedLocale = acceptedLocales.nextElement();
            final var iso3166_2 = acceptedLocale.getCountry();
            if (isNotEmpty(iso3166_2)) {
                return iso3166_2.toUpperCase(ROOT);
            }
        }
        return null;
    }
}
