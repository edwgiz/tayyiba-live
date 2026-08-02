package com.github.edwgiz.tayyib.domain.usecase.calendar;

import com.github.edwgiz.tayyib.domain.model.GeoLocation;
import com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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


@TestInstance(PER_CLASS)
class GetFastingTimesUsecaseTest {

    final GetFastingTimesUsecase u = new GetFastingTimesUsecase();

    @Nested
    final class Apply {
        @Test
        public void test() {
            test("2026-01-20", "Asia/Riyadh", 20.666666, 41.333332, 0, UMM_AL_QURA,
                    "AstronomicalEventOffset{05:34:03}",
                    "AstronomicalEventOffset{06:53:31}",
                    "AstronomicalEventOffset{17:57:45}");
            test("2026-07-30", "Asia/Riyadh", 20.666666, 41.333332, 0, UMM_AL_QURA,
                    "AstronomicalEventOffset{04:25:29}",
                    "AstronomicalEventOffset{05:48:24}",
                    "AstronomicalEventOffset{18:53:52}");
            test("2026-07-30", "Europe/Moscow", 55.7522, 37.6156, 0, SAMR,
                    "AdjustedAstronomicalEventOffset{02:26:19, TWILIGHT_ANGLE}",
                    "AstronomicalEventOffset{04:31:19}",
                    "AstronomicalEventOffset{20:40:41}");
            test("2026-07-30", "Europe/Moscow", 55.7522, 37.6156, 161, SAMR,
                    "AdjustedAstronomicalEventOffset{02:21:31, TWILIGHT_ANGLE}",
                    "AstronomicalEventOffset{04:27:44}",
                    "AstronomicalEventOffset{20:44:17}");
            // test polar night conditions when no sunset/sunrise occured
            test("2026-11-29", "Europe/Oslo", 69.6489, 18.9551, 0, MWL,
                    "AstronomicalEventOffset{05:57:11}",
                    "AstronomicalEventOffset{11:22:34}",
                    "AstronomicalEventOffset{11:42:44}");
            test("2027-01-15", "Europe/Oslo", 69.6489, 18.9551, 0, MWL,
                    "AstronomicalEventOffset{06:13:51}",
                    "AstronomicalEventOffset{11:34:23}",
                    "AstronomicalEventOffset{12:12:34}");
        }

        private void test(
                final String dayStr,
                final String timezoneName,
                final double lat,
                final double lon,
                final @Nullable Integer altitude,
                final PrayerTimeMethod prayerTimeMethod,
                final String expectedFajr,
                final String expectedSunrise,
                final String expectedSunset) {
            final var day = LocalDate.parse(dayStr);
            final var timezone = ZoneId.of(timezoneName);
            final var location = new GeoLocation(lat, lon);
            final var cache = u.createCache(location, altitude, prayerTimeMethod, day.minusDays(1), timezone);
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