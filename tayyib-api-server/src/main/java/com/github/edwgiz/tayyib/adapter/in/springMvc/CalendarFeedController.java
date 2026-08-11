package com.github.edwgiz.tayyib.adapter.in.springMvc;

import com.github.edwgiz.tayyib.adapter.commons.dgs.dto.HijriMethod;
import com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod;
import com.github.edwgiz.tayyib.domain.usecase.calendar.GetCalendarDaysUsecase;
import com.github.edwgiz.tayyib.domain.usecase.calendar.GetCalendarDaysUsecase.FastingTimeCalculationArgs;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static java.lang.Double.parseDouble;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNullElse;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.http.ResponseEntity.status;


@RestController
public class CalendarFeedController {


    private final GetCalendarDaysUsecase getCalendarDaysUsecase;
    private final List<Locale> supportedLocales;
    private final CalendarFeedView calendarFeedView;

    CalendarFeedController(
            final GetCalendarDaysUsecase getCalendarDaysUsecase,
            final List<Locale> supportedLocales,
            final CalendarFeedView calendarFeedView) {
        this.getCalendarDaysUsecase = getCalendarDaysUsecase;
        this.supportedLocales = supportedLocales;
        this.calendarFeedView = calendarFeedView;
    }


    @GetMapping(value = "/calendar/fasting.ics",
            produces = {TEXT_PLAIN_VALUE, "text/calendar"})
    public ResponseEntity<StreamingResponseBody> get(
            final @RequestParam LocalDate start,
            final @RequestParam String timezone,
            final @RequestParam String locale,
            final @RequestParam("hijri-method") HijriMethod hijriMethod,
            final @RequestParam @Nullable String latitude,
            final @RequestParam @Nullable String longitude,
            final @RequestParam("prayer-time-method") @Nullable String prayerTimeMethod,
            final Locale fallbackLocale) {

        final ZoneId zoneId;
        try {
            zoneId = ZoneId.of(timezone);
        } catch (IllegalArgumentException _) {
            return create400Response("Invalid query parameter 'timezone'");
        }

        final com.github.edwgiz.tayyib.domain.model.HijriMethod hijriMethodModel;
        try {
            hijriMethodModel = com.github.edwgiz.tayyib.domain.model.HijriMethod.valueOf(hijriMethod.name());
        } catch (IllegalArgumentException _) {
            return create400Response("Invalid query parameter 'hijri-method'");
        }

        final Locale prefferedLocale;
        try {
            final var locales = Locale.LanguageRange.parse(locale, new HashMap<>());
            final var matchingLocale = Locale.lookup(locales, supportedLocales);
            prefferedLocale = requireNonNullElse(matchingLocale, fallbackLocale);
        } catch (IllegalArgumentException _) {
            return create400Response("Invalid query parameter 'locale'");
        }

        final FastingTimeCalculationArgs fastingTimeCalculationArgs;
        if (latitude != null || longitude != null) {
            if (latitude == null) {
                return create400Response("Missed query parameter 'latitude' while 'longitude' is passed");
            }
            final double lat;
            try {
                lat = parseDouble(latitude);
            } catch (NumberFormatException _) {
                return create400Response("Invalid query parameter 'latitude'");
            }
            if (longitude == null) {
                return create400Response("Missed query parameter 'longitude' while 'latitude' is passed");
            }
            final double lon;
            try {
                lon = parseDouble(longitude);
            } catch (NumberFormatException _) {
                return create400Response("Invalid query parameter 'longitude'");
            }

            final PrayerTimeMethod preferredPrayerTimeMethod;
            if (prayerTimeMethod == null) {
                preferredPrayerTimeMethod = null;
            } else {
                try {
                    preferredPrayerTimeMethod = PrayerTimeMethod.valueOf(prayerTimeMethod);
                } catch (IllegalArgumentException _) {
                    return create400Response("Invalid query parameter 'prayer-time-method'");
                }
            }

            fastingTimeCalculationArgs = new FastingTimeCalculationArgs(lat, lon, prefferedLocale.toLanguageTag(), preferredPrayerTimeMethod);
        } else {
            fastingTimeCalculationArgs = null;
        }

        return switch (getCalendarDaysUsecase.apply(start, start.plusDays(30), hijriMethodModel, zoneId, fastingTimeCalculationArgs)) {
            case GetCalendarDaysUsecase.Result.Ok ok -> calendarFeedView.render(ok, prefferedLocale, zoneId);
            case GetCalendarDaysUsecase.Result.ServiceUnavailable _ -> status(SERVICE_UNAVAILABLE).build();
        };
    }


    private static ResponseEntity<StreamingResponseBody> create400Response(final String msg) {
        return status(BAD_REQUEST).contentType(TEXT_PLAIN).body(out -> out.write(msg.getBytes(UTF_8)));
    }
}
