package com.github.edwgiz.tayyib.domain.usecase.calendar;

import com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.shadow.de.siegmar.fastcsv.util.Nullable;

import java.time.LocalDate;
import java.time.ZoneId;

import static com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.MWL;
import static com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.SAMR;
import static com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.UMM_AL_QURA;
import static java.lang.Math.toRadians;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.junit.jupiter.params.provider.Arguments.arguments;


@TestInstance(PER_CLASS)
class GetFastingTimesUsecaseTest {

    final GetFastingTimesUsecase u = new GetFastingTimesUsecase();

    @Nested
    final class Apply {

        static Arguments[] test() {
            return new Arguments[]{
                    arguments(
                            "Riyadh winter day",
                            "2026-01-20", "Asia/Riyadh", 20.666666, 41.333332, 0, UMM_AL_QURA,
                            "AstronomicalEventOffset{05:34:03}",
                            "AstronomicalEventOffset{06:53:31}",
                            "AstronomicalEventOffset{17:57:45}"
                    ),
                    arguments(
                            "Riyadh summer day",
                            "2026-07-30", "Asia/Riyadh", 20.666666, 41.333332, 0, UMM_AL_QURA,
                            "AstronomicalEventOffset{04:25:29}",
                            "AstronomicalEventOffset{05:48:24}",
                            "AstronomicalEventOffset{18:53:52}"
                    ),
                    arguments(
                            "Moscow summer day",
                            "2026-07-30", "Europe/Moscow", 55.7522, 37.6156, 0, SAMR,
                            "AdjustedAstronomicalEventOffset{02:26:19, TWILIGHT_ANGLE}",
                            "AstronomicalEventOffset{04:31:19}",
                            "AstronomicalEventOffset{20:40:41}"
                    ),
                    arguments(
                            "Moscow summer day 2",
                            "2026-08-03", "Europe/Moscow", 55.7522, 37.6156, 0, SAMR,
                            "AdjustedAstronomicalEventOffset{02:29:40, TWILIGHT_ANGLE}",
                            "AstronomicalEventOffset{04:38:42}",
                            "AstronomicalEventOffset{20:32:49}"
                    ),
                    arguments(
                            "Moscow summer day with 161 m altitude",
                            "2026-07-30", "Europe/Moscow", 55.7522, 37.6156, 161, SAMR,
                            "AdjustedAstronomicalEventOffset{02:21:31, TWILIGHT_ANGLE}",
                            "AstronomicalEventOffset{04:27:44}",
                            "AstronomicalEventOffset{20:44:17}"
                    ),

                    arguments(
                            "Tromsø polar night transition: no actual sunrise/sunset, interpolation calculation",
                            "2026-11-29", "Europe/Oslo", 69.6489, 18.9551, 0, MWL,
                            "AstronomicalEventOffset{05:57:11}",
                            "AstronomicalEventOffset{11:22:34}",
                            "AstronomicalEventOffset{11:42:44}"
                    ),
                    arguments(
                            "Tromsø polar night: deep winter interpolation calculation",
                            "2027-01-15", "Europe/Oslo", 69.6489, 18.9551, 0, MWL,
                            "AstronomicalEventOffset{06:13:51}",
                            "AstronomicalEventOffset{11:34:23}",
                            "AstronomicalEventOffset{12:12:34}"
                    ),

                    arguments(
                            "Maximum daylight period near Arctic Circle during summer solstice",
                            "2026-06-20", "Europe/Helsinki", 65.8, 25.7, 0, MWL,
                            "AdjustedAstronomicalEventOffset{01:22:07, TWILIGHT_ANGLE}",
                            "AstronomicalEventOffset{01:27:15}",
                            "AstronomicalEventOffset{+1 01:10:17}"
                    ),
                    arguments(
                            "Maximum daylight period with max western timezone offset (UTC-12)",
                            "2026-06-20", "Etc/GMT+12", 65.8, 25.7, 0, MWL,
                            "AdjustedAstronomicalEventOffset{-1 10:21:45, TWILIGHT_ANGLE}",
                            "AstronomicalEventOffset{-1 10:26:43}",
                            "AstronomicalEventOffset{10:11:07}"
                    ),
                    arguments(
                            "Maximum daylight period with max eastern timezone offset (UTC+14)",
                            "2026-06-20", "Etc/GMT-14", 65.8, 25.7, 0, MWL,
                            "AdjustedAstronomicalEventOffset{12:21:31, TWILIGHT_ANGLE}",
                            "AstronomicalEventOffset{12:26:44}",
                            "AstronomicalEventOffset{+1 12:10:37}"
                    )};
        }


        @ParameterizedTest(name = "{0}")
        @MethodSource()
        void test(
                final String description,
                final String dayStr,
                final String timezoneName,
                final double WGS84_lat,
                final double WGS84_lon,
                final @Nullable Integer altitude,
                final PrayerTimeMethod prayerTimeMethod,
                final String expectedFajr,
                final String expectedSunrise,
                final String expectedSunset) {
            final var day = LocalDate.parse(dayStr);
            final var timezone = ZoneId.of(timezoneName);
            final var cache = u.createCache(WGS84_lat, WGS84_lon, altitude, prayerTimeMethod, day.minusDays(1), timezone);
            final var result = u.apply(day, cache);

            assertEquals(expectedFajr, result.fajr().toString());
            assertEquals(expectedSunrise, result.sunrise().toString());
            assertEquals(expectedSunset, result.sunset().toString());
        }
    }


    @Test
    void eventMinutes() {
        final var solarNoonAngle = GetFastingTimesUsecase.solarNoonAngle(0.0, 0.0);
        // 06:00 UTC which is exactly sunrise at Greenwich on an equinox approximation
        assertEquals(21600, GetFastingTimesUsecase.eventUtcOffset(solarNoonAngle, toRadians(90.0), true));
    }


    @Test
    void prayerTimeCalculationMethodNameConsistency() {
        for (final var prayerTimeCalculationMethod : PrayerTimeMethod.values()) {
            assertNotNull(GetFastingTimesUsecase.Method.valueOf(prayerTimeCalculationMethod.name()));
        }
    }
}