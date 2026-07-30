package com.github.edwgiz.tayyib.adapter.in.dgs.fetcher;

import com.github.edwgiz.tayyib.adapter.commons.dgs.dto.*;
import com.github.edwgiz.tayyib.adapter.commons.dgs.dto.CalendarDay;
import com.github.edwgiz.tayyib.domain.model.*;
import com.github.edwgiz.tayyib.domain.model.HijriMethod;
import com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod;
import com.github.edwgiz.tayyib.domain.usecase.calendar.GetCalendarDaysUsecase;
import com.github.edwgiz.tayyib.domain.usecase.i18n.GetBundleUsecase;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.netflix.graphql.dgs.internal.DgsWebMvcRequestData;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.LocaleResolver;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Locale.ROOT;
import static java.util.Map.entry;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;


@DgsComponent
public class CalendarFetcher {

    private static final Logger log = LoggerFactory.getLogger(CalendarFetcher.class);

    private final static Set<String> SUNDAY_FIRST_COUNTRIES = Set.of(
            "US", "CA", "AU", "NZ", "PH", "JP", "KR", "TW", "HK", "SA", "EG", "IL");

    private final LocaleResolver localeResolver;
    private final GetCalendarDaysUsecase getCalendarDaysUsecase;
    private final GetBundleUsecase getBundleUsecase;


    CalendarFetcher(
            final LocaleResolver localeResolver,
            final GetCalendarDaysUsecase getCalendarDaysUsecase,
            final GetBundleUsecase getBundleUsecase) {
        this.localeResolver = localeResolver;
        this.getCalendarDaysUsecase = getCalendarDaysUsecase;
        this.getBundleUsecase = getBundleUsecase;
    }


    @DgsQuery
    GetCalendarCellsResult getCalendarCells(
            final @InputArgument GetCalendarCellsInput input,
            final DgsDataFetchingEnvironment dfe) {

        final var requestData = getRequestData(dfe);
        final var hijriMethod = HijriMethod.valueOf(input.getHijriMethod().name());
        final var prayerTimeMethod = PrayerTimeMethod.valueOf(input.getPrayerTimeMethod().name());
        final var geoLocation = toModel(input.getLocation());

        final var dayOfWeeks = DayOfWeek.values();
        final var dayOfWeekNames = new String[7];
        final var isSundayFirst = SUNDAY_FIRST_COUNTRIES.contains(requestData.iso3166_2);
        int dayOfWeekOffset = isSundayFirst ? 6 : 0;
        for (int i = 0; i < dayOfWeekNames.length; i++) {
            dayOfWeekNames[i] = dayOfWeeks[(i + dayOfWeekOffset) % 7].getDisplayName(TextStyle.SHORT, requestData.locale);
        }
        final var i18n = getBundleUsecase.apply(requestData.locale());
        final var timezone = ZoneId.of(input.getTimezone());
        final var today = Instant.ofEpochMilli(input.getMillis()).atZone(timezone).toLocalDate();
        final var calendarResult = getCalendarDaysUsecase.apply(today, isSundayFirst, hijriMethod, prayerTimeMethod, timezone, geoLocation);
        final var hijriMonthNames = new HashMap<Integer, String>();
        final var calendarEventReasons = new HashMap<String, CalendarEventReason>();
        var todayIndex = (Integer) null;

        final var calendarDays = switch (calendarResult) {
            case GetCalendarDaysUsecase.Result.Ok ok -> {
                final var result = new ArrayList<CalendarDay>();
                final var firstDayOfMonth = ok.firstDayOfMonth();
                final var days = ok.calendarDayPairs();
                for (int j = 0; j < days.size(); j++) {
                    var calendarDay = days.get(j);
                    final var day = calendarDay.gregorian();
                    if (day.equals(today)) {
                        todayIndex = j;
                    }
                    final var hijriDay = calendarDay.hijri();
                    final var lastDayOfMonth = ok.lastDayOfMonth();
                    hijriMonthNames.computeIfAbsent(hijriDay.month(), i -> i18n.calendar().monthNames()[i]);
                    final var events = new ArrayList<CalendarEvent>();
                    for (final var event : calendarDay.events()) {
                        events.add(toDto(event, i18n, calendarEventReasons));
                    }
                    result.add(new CalendarDay(
                            new GregorianCalendarDay(
                                    day.isBefore(firstDayOfMonth) || day.isAfter(lastDayOfMonth),
                                    day.getDayOfMonth()),
                            new HijriCalendarDay(hijriDay.day(), hijriDay.month()),
                            events));
                }

                yield result;
            }
            case GetCalendarDaysUsecase.Result.ServiceUnavailable _ -> List.<CalendarDay>of();
        };

        final var monthLabel = ofPattern("yyyy MMM", requestData.locale()).format(today);

        return new GetCalendarCellsResult(
                hijriMonthNames.entrySet().stream().map(e -> new StringToStringEntry(Integer.toString(e.getKey()), e.getValue())).toList(),
                new ArrayList<>(calendarEventReasons.values()),
                monthLabel,
                Arrays.asList(dayOfWeekNames),
                calendarDays,
                todayIndex
        );
    }


    private static @Nullable GeoLocation toModel(final @Nullable GeoPointInput location) {
        return location == null ? null : new GeoLocation(location.getLatitude(), location.getLongitude());
    }


    private static CalendarEvent toDto(
            final CalendarDayPair.CalendarEvent event,
            final I18n i18n,
            final HashMap<String, CalendarEventReason> calendarEventReasons
    ) {
        final var fastingReasonGroups = i18n.fasting().reasonGroups();
        final var reasonViews = new ArrayList<CalendarEventReasonView>();
        for (final var reason : event.reasons()) {
            final var reasonEntry = switch (reason.group()) {
                case OBLIGATORY_FASTING ->
                        entry(CalendarEventReasonGroup.OBLIGATORY_FASTING, fastingReasonGroups.obligatory());
                case VOLUNTARY_FASTING ->
                        entry(CalendarEventReasonGroup.VOLUNTARY_FASTING, fastingReasonGroups.voluntary());
                case PROHIBITING_FASTING ->
                        entry(CalendarEventReasonGroup.PROHIBITING_FASTING, fastingReasonGroups.prohibiting());
            };
            reasonViews.add(new CalendarEventReasonView(reason.id(), getHeadline(reason, reasonEntry.getValue())));
            if (!calendarEventReasons.containsKey(reason.id())) {
                calendarEventReasons.put(reason.id(), new CalendarEventReason(
                        reason.id(),
                        reasonEntry.getKey()));
            }
        }

        return new CalendarEvent(
                reasonViews,
                event.startMinute(),
                event.endMinute());
    }


    private static @Nullable String getHeadline(
            final CalendarDayPair.CalendarEventReason reason,
            final I18n.Fasting.FastingReasonGroup reasonGroup) {
        if (isNotEmpty(reason.headlineId())) {
            for (final var reasonI18n : reasonGroup.reasons()) {
                if (reason.id().equals(reasonI18n.id())) {
                    final String headline = isNotEmpty(reasonI18n.headlines())
                            ? reasonI18n.headlines().get(reason.headlineId())
                            : null;
                    if (headline == null) {
                        log.error("Can't find headline by reasonId: {}, headlineId: {}", reason.id(), reason.headlineId());
                    }
                    return headline;
                }
            }
        }
        return null;
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
