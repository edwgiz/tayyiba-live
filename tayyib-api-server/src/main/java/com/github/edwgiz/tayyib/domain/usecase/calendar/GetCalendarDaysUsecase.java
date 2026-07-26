package com.github.edwgiz.tayyib.domain.usecase.calendar;

import com.github.edwgiz.tayyib.adapter.out.httpClient.AladhanIslamicCalendarClient;
import com.github.edwgiz.tayyib.adapter.out.jdbc.GregorianHijriMappingRepository;
import com.github.edwgiz.tayyib.domain.model.CalendarDay;
import com.github.edwgiz.tayyib.domain.model.CalendarDayPair;
import com.github.edwgiz.tayyib.domain.model.CalendarDayPair.CalendarEvent;
import com.github.edwgiz.tayyib.domain.model.CalendarDayPair.CalendarEventReason;
import com.github.edwgiz.tayyib.domain.model.GeoLocation;
import com.github.edwgiz.tayyib.domain.model.HijriMethod;
import net.time4j.Moment;
import net.time4j.calendar.HijriCalendar;
import net.time4j.engine.StartOfDay;
import net.time4j.tz.ZonalOffset;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.JdbcUtils.tx;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.JdbcUtils.txWithoutResult;
import static com.github.edwgiz.tayyib.domain.model.CalendarDayPair.CalendarEventReasonGroup.OBLIGATORY_FASTING;
import static com.github.edwgiz.tayyib.domain.model.CalendarDayPair.CalendarEventReasonGroup.PROHIBITING_FASTING;
import static com.github.edwgiz.tayyib.domain.model.CalendarDayPair.CalendarEventReasonGroup.VOLUNTARY_FASTING;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.nextOrSame;
import static java.time.temporal.TemporalAdjusters.previousOrSame;
import static net.time4j.scale.TimeScale.POSIX;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.ArrayUtils.contains;


@Service
public class GetCalendarDaysUsecase {

    private final AladhanIslamicCalendarClient aladhanIslamicCalendarClient;
    private final DataSource dataSource;
    private final GregorianHijriMappingRepository gregorianHijriMappingRepository;


    public GetCalendarDaysUsecase(
            final AladhanIslamicCalendarClient aladhanIslamicCalendarClient,
            final DataSource dataSource,
            final GregorianHijriMappingRepository gregorianHijriMappingRepository) {
        this.aladhanIslamicCalendarClient = aladhanIslamicCalendarClient;
        this.dataSource = dataSource;
        this.gregorianHijriMappingRepository = gregorianHijriMappingRepository;
    }


    public Result apply(
            final long millis,
            final LocalDate today,
            final boolean isSundayFirst,
            final HijriMethod hijriMethod,
            final @Nullable GeoLocation geoLocation
    ) {
        final var firstDayOfMonth = today.with(firstDayOfMonth());
        final var firstDay = firstDayOfMonth.with(previousOrSame(isSundayFirst ? SUNDAY : MONDAY));
        final var lastDayOfMonth = firstDayOfMonth.with(lastDayOfMonth());
        final var lastDay = lastDayOfMonth.with(nextOrSame(isSundayFirst ? SATURDAY : SUNDAY));

        final var gregorianHijriMapping = tx(dataSource, tx -> gregorianHijriMappingRepository.findBetween(
                hijriMethod,
                firstDay,
                lastDay,
                tx));

        final var length = (int) DAYS.between(firstDay, lastDay);

        final var calendarDays = new ArrayList<CalendarDayPair>(length);
        for (var day = firstDay; !day.isAfter(lastDay); day = day.plusDays(1)) {
            if (!gregorianHijriMapping.containsKey(day)) {
                // download mapping by aladhan api
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
            calendarDays.add(new CalendarDayPair(
                    day,
                    hijriDay,
                    isEmpty(fastingReasons)
                            ? List.of()
                            : List.of(new CalendarEvent(
                            fastingReasons,
                            null,
                            null))));
        }


//        HijrahDate today = HijrahChronology.INSTANCE.dateNow();
        //      ChronoLocalDateTime<HijrahDate> hijriTimestamp = today.atTime(LocalTime.MIDNIGHT);

        final var moment = Moment.of(millis / 1000, (int) (millis % 1000) * 1_000_000, POSIX);
        final var gregorianDate = moment.toZonalTimestamp(ZonalOffset.UTC).toDate();


        HijriCalendar diyanet = gregorianDate.transform(HijriCalendar.class, HijriCalendar.VARIANT_DIYANET);
        HijriCalendar ummalqura = gregorianDate.transform(HijriCalendar.class, HijriCalendar.VARIANT_UMALQURA);
        Moment at = diyanet.atTime(0, 0).at(ZonalOffset.UTC, StartOfDay.MIDNIGHT);
        return new Result.Ok(firstDayOfMonth, lastDayOfMonth, calendarDays);
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


    public sealed interface Result {
        record Ok(
                LocalDate firstDayOfMonth,
                LocalDate lastDayOfMonth,
                ArrayList<CalendarDayPair> calendarDayPairs
        ) implements Result {

        }

        record ServiceUnavailable() implements Result {
        }
    }
}
