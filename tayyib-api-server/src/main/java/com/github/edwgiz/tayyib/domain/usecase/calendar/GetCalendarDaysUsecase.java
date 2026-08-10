package com.github.edwgiz.tayyib.domain.usecase.calendar;

import com.github.edwgiz.tayyib.adapter.out.httpClient.AladhanIslamicCalendarClient;
import com.github.edwgiz.tayyib.adapter.out.jdbc.GregorianHijriMappingRepository;
import com.github.edwgiz.tayyib.adapter.out.jdbc.PrayerTimeMethodByCountryRepository;
import com.github.edwgiz.tayyib.domain.model.CalendarDay;
import com.github.edwgiz.tayyib.domain.model.CalendarDayPair;
import com.github.edwgiz.tayyib.domain.model.CalendarDayPair.CalendarEvent;
import com.github.edwgiz.tayyib.domain.model.CalendarDayPair.CalendarEventReason;
import com.github.edwgiz.tayyib.domain.model.HijriMethod;
import com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod;
import com.github.edwgiz.tayyib.domain.usecase.calendar.GetFastingTimesUsecase.Cache.ProlongedCalendarEvent;
import com.github.edwgiz.tayyib.domain.usecase.geo.GetReverseGeocodingUsecase;
import com.github.edwgiz.tayyib.domain.usecase.geo.GetReverseGeocodingUsecase.GetReverseGeocodingArgs;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.JdbcUtils.tx;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.JdbcUtils.txWithoutResult;
import static com.github.edwgiz.tayyib.domain.model.CalendarDayPair.CalendarEventReasonGroup.OBLIGATORY_FASTING;
import static com.github.edwgiz.tayyib.domain.model.CalendarDayPair.CalendarEventReasonGroup.PROHIBITING_FASTING;
import static com.github.edwgiz.tayyib.domain.model.CalendarDayPair.CalendarEventReasonGroup.VOLUNTARY_FASTING;
import static java.lang.Math.min;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.apache.commons.lang3.ArrayUtils.contains;


@Service
public class GetCalendarDaysUsecase {

    private final GetFastingTimesUsecase getFastingTimesUsecase;
    private final GetReverseGeocodingUsecase getReverseGeocodingUsecase;
    private final AladhanIslamicCalendarClient aladhanIslamicCalendarClient;
    private final DataSource dataSource;
    private final GregorianHijriMappingRepository gregorianHijriMappingRepository;
    private final PrayerTimeMethodByCountryRepository prayerTimeMethodByCountryRepository;


    public GetCalendarDaysUsecase(
            final GetFastingTimesUsecase getFastingTimesUsecase,
            final GetReverseGeocodingUsecase getReverseGeocodingUsecase,
            final AladhanIslamicCalendarClient aladhanIslamicCalendarClient,
            final DataSource dataSource,
            final GregorianHijriMappingRepository gregorianHijriMappingRepository,
            final PrayerTimeMethodByCountryRepository prayerTimeMethodByCountryRepository) {
        this.getFastingTimesUsecase = getFastingTimesUsecase;
        this.getReverseGeocodingUsecase = getReverseGeocodingUsecase;
        this.aladhanIslamicCalendarClient = aladhanIslamicCalendarClient;
        this.dataSource = dataSource;
        this.gregorianHijriMappingRepository = gregorianHijriMappingRepository;
        this.prayerTimeMethodByCountryRepository = prayerTimeMethodByCountryRepository;
    }


    public Result apply(
            final LocalDate firstDay,
            final LocalDate lastDay,
            final HijriMethod hijriMethod,
            final ZoneId timezone,
            final @Nullable FastingTimeCalculationArgs fastingTimeCalculationArgs
    ) {
        var locationIso3166_1_alpha2 = (String) null;
        var locationDisplayName = (String) null;
        if (fastingTimeCalculationArgs != null) {
            final var reverseGeoCoding = getReverseGeocodingUsecase.apply(new GetReverseGeocodingArgs(
                    fastingTimeCalculationArgs.lat(),
                    fastingTimeCalculationArgs.lon(),
                    fastingTimeCalculationArgs.acceptLanguage()));
            if (reverseGeoCoding != null) {
                locationIso3166_1_alpha2 = reverseGeoCoding.iso3166_1_alpha2();
                locationDisplayName = reverseGeoCoding.displayName();
            }
        }

        final var gregorianHijriMapping = tx(dataSource, tx -> gregorianHijriMappingRepository.findBetween(
                hijriMethod,
                firstDay,
                lastDay,
                tx));

        final var length = (int) DAYS.between(firstDay, lastDay);

        final var calendarDays = new ArrayList<CalendarDayPair>(length);
        final var fastingTimesEntry = createFastingTimesCache(firstDay.minusDays(1), timezone, fastingTimeCalculationArgs, locationIso3166_1_alpha2);
        for (var day = firstDay; !day.isAfter(lastDay); day = day.plusDays(1)) {
            if (!gregorianHijriMapping.containsKey(day)) {
                final var gregorianToHijriCalendar = aladhanIslamicCalendarClient.gregorianToHijriCalendar(day.getMonthValue(), day.getYear(), hijriMethod);
                if (gregorianToHijriCalendar == null || !contains(gregorianToHijriCalendar.gregorian(), day)) {
                    return new Result.ServiceUnavailable();
                }
                final var newGregorian = gregorianToHijriCalendar.gregorian();
                final var newHijri = gregorianToHijriCalendar.hijri();
                txWithoutResult(dataSource, tx -> gregorianHijriMappingRepository.save(
                        hijriMethod,
                        newGregorian,
                        newHijri,
                        tx));
                for (int i = 0; i < newGregorian.length; i++) {
                    gregorianHijriMapping.put(newGregorian[i], newHijri[i]);
                }
            }
            final var hijriDay = gregorianHijriMapping.get(day);
            final var fastingReasons = createFastingReasons(hijriDay, day.getDayOfWeek());


            final var calendarEvents = fastingTimesEntry == null
                    ? List.of(new CalendarEvent(fastingReasons, null, null))
                    : createCalendarEvents(fastingTimesEntry.getKey(), day, calendarDays, fastingReasons);
            calendarDays.add(new CalendarDayPair(day, hijriDay, calendarEvents));
        }

        return new Result.Ok(
                calendarDays,
                locationDisplayName,
                fastingTimesEntry == null ? null : fastingTimesEntry.getValue());
    }


    private @Nullable Entry<GetFastingTimesUsecase.Cache, PrayerTimeMethod> createFastingTimesCache(
            final LocalDate firstDay,
            final ZoneId timezone,
            final @Nullable FastingTimeCalculationArgs fastingTimeCalculationArgs,
            final @Nullable String locationIso3166_1_alpha2) {
        if (fastingTimeCalculationArgs == null) {
            return null;
        }
        final var prayerTimeMethod = getPrayerTimeMethod(fastingTimeCalculationArgs.prayerTimeMethod(), locationIso3166_1_alpha2);
        final var cache = getFastingTimesUsecase.createCache(
                fastingTimeCalculationArgs.lat(),
                fastingTimeCalculationArgs.lon(),
                0,
                prayerTimeMethod,
                firstDay.minusDays(1),
                timezone);
        return Map.entry(cache, prayerTimeMethod);
    }


    private PrayerTimeMethod getPrayerTimeMethod(
            @Nullable PrayerTimeMethod prayerTimeMethod,
            @Nullable String locationIso3166_1_alpha2) {
        if (prayerTimeMethod != null) {
            return prayerTimeMethod;
        }
        if (locationIso3166_1_alpha2 != null) {
            final var prayerTimeMethodByCountry = tx(dataSource, tx ->
                    prayerTimeMethodByCountryRepository.find(tx, locationIso3166_1_alpha2));
            if (prayerTimeMethodByCountry.isPresent()) {
                return prayerTimeMethodByCountry.get();
            }
        }
        return PrayerTimeMethod.MWL;
    }


    private List<CalendarEvent> createCalendarEvents(
            final GetFastingTimesUsecase.Cache fastingTimesCache,
            final LocalDate day,
            final ArrayList<CalendarDayPair> calendarDays, List<CalendarEventReason> fastingReasons) {

        final var prolongedCalendarEvents = fastingTimesCache.prolongedCalendarEvents;
        var fastingReasonEvent = (CalendarEvent) null;
        if (!fastingReasons.isEmpty()) {
            var startMinute = (Integer) null;
            var endMinute = (Integer) null;
            final var result = getFastingTimesUsecase.apply(day, fastingTimesCache);
            startMinute = getMinute(result.fajr());
            endMinute = getMinute(result.sunset());
            if (endMinute != null && endMinute > 1440) {
                prolongedCalendarEvents.put(day.plusDays(1), new ProlongedCalendarEvent(fastingReasons, endMinute - 1440));
                endMinute = 1441;
            }
            if (startMinute == null && endMinute == null) {
                fastingReasonEvent = new CalendarEvent(fastingReasons, null, null);
            } else if (startMinute != null && startMinute < 0) {
                if (!calendarDays.isEmpty()) {
                    final var previousDay = calendarDays.getLast();
                    previousDay.events().add(new CalendarEvent(
                            fastingReasons,
                            startMinute + 1440,
                            endMinute == null ? null : min(endMinute + 1440, 1441)));
                }
                fastingReasonEvent = new CalendarEvent(fastingReasons, -1, endMinute);
            } else {
                fastingReasonEvent = new CalendarEvent(fastingReasons, startMinute, endMinute);
            }
        }

        final var calendarEvents = new ArrayList<CalendarEvent>();
        final var prolongedCalendarEvent = prolongedCalendarEvents.remove(day);
        if (prolongedCalendarEvent != null) {
            final var prolongedEndMinute = prolongedCalendarEvent.endMinute();
            calendarEvents.add(new CalendarEvent(
                    prolongedCalendarEvent.reasons(),
                    -1,
                    min(prolongedEndMinute, 1441)
            ));
            if (prolongedEndMinute > 1440) {
                prolongedCalendarEvents.put(day.plusDays(1), new ProlongedCalendarEvent(
                        prolongedCalendarEvent.reasons(),
                        prolongedEndMinute - 1440));
            }
        }
        if (fastingReasonEvent != null) {
            calendarEvents.add(fastingReasonEvent);
        }
        return calendarEvents;
    }


    private static @Nullable Integer getMinute(
            final GetFastingTimesUsecase.Result.EventOffset eventOffset
    ) {
        return switch (eventOffset) {
            case GetFastingTimesUsecase.Result.EventOffset.AstronomicalEventOffset astronomical ->
                    astronomical.seconds() / 60;
            case GetFastingTimesUsecase.Result.EventOffset.AdjustedAstronomicalEventOffset adjustedAstronomical ->
                    adjustedAstronomical.seconds() / 60;
            case GetFastingTimesUsecase.Result.EventOffset.Undefined _ -> null;
        };
    }


    private List<CalendarEventReason> createFastingReasons(final CalendarDay hijri, final DayOfWeek dayOfWeek) {
        final var result = new ArrayList<CalendarEventReason>();
        final int month = hijri.month();
        final int day = hijri.day();
        if (month == 10 && day == 1) {
            result.add(new CalendarEventReason("fasting-reason-eid-days", "eid-al-fitr", PROHIBITING_FASTING));
        }
        if (month == 12) {
            switch (day) {
                case 10 ->
                        result.add(new CalendarEventReason("fasting-reason-eid-days", "eid-al-adha", PROHIBITING_FASTING));
                case 11, 12, 13 ->
                        result.add(new CalendarEventReason("fasting-reason-tashriq", null, PROHIBITING_FASTING));
            }
        }
        if (!result.isEmpty()) {
            return result;
        }

        if (month == 9) {
            result.add(new CalendarEventReason("fasting-reason-ramadan", null, OBLIGATORY_FASTING));
        }
        // Day of Ashura
        if (month == 12 && day == 9) {
            result.add(new CalendarEventReason("fasting-reason-arafah-or-ashura", "arafah", VOLUNTARY_FASTING));
        } else if (month == 1 && day == 10) {
            result.add(new CalendarEventReason("fasting-reason-arafah-or-ashura", "ashura", VOLUNTARY_FASTING));
        }
        if (month == 12 && day < 10) {// First nine days of Dhu al-Hijjah
            result.add(new CalendarEventReason("fasting-reason-first-9-dhu-al-hijjah", null, VOLUNTARY_FASTING));
        }
        switch (day) {
            case 13, 14, 15 ->
                    result.add(new CalendarEventReason("fasting-reason-white-days", null, VOLUNTARY_FASTING));
        }
        switch (dayOfWeek) {
            case MONDAY ->
                    result.add(new CalendarEventReason("fasting-reason-monday-thursday", "monday", VOLUNTARY_FASTING));
            case THURSDAY ->
                    result.add(new CalendarEventReason("fasting-reason-monday-thursday", "thursday", VOLUNTARY_FASTING));
        }
        if (!result.isEmpty()) {
            if (month == 10) { // let's shawwal fasting to be covered by other reasons
                result.add(new CalendarEventReason("fasting-reason-shawwal", null, VOLUNTARY_FASTING));
            }
        }
        return result;
    }


    public record FastingTimeCalculationArgs(
            double lat,
            double lon,
            @Nullable String acceptLanguage,
            @Nullable PrayerTimeMethod prayerTimeMethod
    ) {
    }


    public sealed interface Result {
        record Ok(
                ArrayList<CalendarDayPair> calendarDayPairs,
                @Nullable String locationDisplayName,
                @Nullable PrayerTimeMethod prayerTimeMethod
        ) implements Result {
        }

        record ServiceUnavailable() implements Result {
        }
    }
}
