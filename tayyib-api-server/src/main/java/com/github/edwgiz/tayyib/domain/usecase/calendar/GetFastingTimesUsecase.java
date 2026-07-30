package com.github.edwgiz.tayyib.domain.usecase.calendar;


import com.github.edwgiz.tayyib.domain.model.GeoLocation;
import com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.github.edwgiz.tayyib.domain.usecase.calendar.GetFastingTimesUsecase.HighLatitudeRule.NONE;
import static com.github.edwgiz.tayyib.domain.usecase.calendar.GetFastingTimesUsecase.HighLatitudeRule.TWILIGHT_ANGLE;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;


@Service
public class GetFastingTimesUsecase {

    /*
     * Sunrise definition:
     *
     * -0.833 degrees includes:
     * - solar upper limb radius (~0.2667°)
     * - atmospheric refraction (~0.5667°)
     */
    public static final double SUNRISE_ANGLE = toRadians(-0.833);
    public static final double SUNSET_ANGLE = SUNRISE_ANGLE;


    enum HighLatitudeRule {
        NONE,
        TWILIGHT_ANGLE
    }


    public enum Method {
        MUSLIM_WORLD_LEAGUE(-18.0, TWILIGHT_ANGLE),
        ISLAMIC_SOCIETY_OF_NORTH_AMERICA(-15.0, TWILIGHT_ANGLE),
        EGYPTIAN_GENERAL_AUTHORITY_OF_SURVEY(-19.5, TWILIGHT_ANGLE),
        UMM_AL_QURA_UNIVERSITY_MAKKAH(-18.5, NONE),
        UNIVERSITY_OF_ISLAMIC_SCIENCES_KARACHI(-18.0, TWILIGHT_ANGLE),
        INSTITUTE_OF_GEOPHYSICS_TEHRAN(-17.7, TWILIGHT_ANGLE),
        SHIA_ITHNA_ASHARI_QUM(-16.0, TWILIGHT_ANGLE),
        GULF_REGION(-18.0, TWILIGHT_ANGLE),
        KUWAIT(-18.0, TWILIGHT_ANGLE),
        QATAR(-18.0, TWILIGHT_ANGLE),
        MAJLIS_UGAMA_ISLAM_SINGAPORE(-20.0, TWILIGHT_ANGLE),
        UNION_ORGANIZATION_ISLAMIC_FRANCE(-12.0, TWILIGHT_ANGLE),
        DIYANET_TURKEY(-18.0, TWILIGHT_ANGLE),
        SPIRITUAL_ADMINISTRATION_OF_MUSLIMS_OF_RUSSIA(-16.0, TWILIGHT_ANGLE),
        MOON_SIGHTING_COMMITTEE(-18.0, TWILIGHT_ANGLE),
        DUBAI_UAE(-18.2, TWILIGHT_ANGLE),
        JAKIM_MALAYSIA(-20.0, TWILIGHT_ANGLE),
        TUNISIA(-18.0, TWILIGHT_ANGLE),
        ALGERIA(-18.0, TWILIGHT_ANGLE),
        KEMENTERIAN_AGAMA_INDONESIA(-20.0, TWILIGHT_ANGLE),
        MOROCCO(-19.0, TWILIGHT_ANGLE),
        COMUNIDADE_ISLAMICA_DE_LISBOA(-18.0, TWILIGHT_ANGLE);


        private final double fajrAngle;
        private final HighLatitudeRule highLatitudeRule;

        Method(final double fajrAngle, HighLatitudeRule highLatitudeRule) {
            this.fajrAngle = toRadians(fajrAngle);
            this.highLatitudeRule = highLatitudeRule;
        }
    }


    public Cache createCache(
            final @Nullable GeoLocation location,
            final @Nullable Integer altitude,
            final PrayerTimeMethod prayerTimeMethod,
            final LocalDate previousDay,
            final ZoneId timezone) {
        if (location == null) {
            return null;
        }
        final var latitude = toRadians(location.lat());
        final var latitudeSin = sin(latitude);
        final var latitudeCos = cos(latitude);
        final var horizonDip = altitude != null && altitude > 0
                ? sqrt(2.0 * altitude / MeeusUtils.earthRadius(latitudeSin, latitudeCos))
                : 0.0;
        final var method = Method.valueOf(prayerTimeMethod.name());
        final var cache = new Cache(
                latitudeSin,
                latitudeCos,
                toRadians(location.lon()),
                timezone,
                method.fajrAngle - horizonDip,
                SUNRISE_ANGLE - horizonDip,
                SUNSET_ANGLE - horizonDip
                , method);
        cache.previousSunset = apply(previousDay, cache).sunset;
        return cache;
    }

    /**
     * <p>The calculation is based on Jean Meeus' "Astronomical Algorithms".
     * It uses the apparent solar longitude, corrected obliquity, solar
     * solarDeclination, equation of time, and hour angle.</p>
     *
     * @param day   local day
     * @param cache cache
     * @return times as local seconds after midnight
     */
    public Result apply(
            final LocalDate day,
            final Cache cache) {

        final var startOfDay = day.atStartOfDay(cache.timezone);
        final var jd = MeeusUtils.julianDay(startOfDay.plusHours(12).toEpochSecond() * 1000L);
        final var T = MeeusUtils.julianCentury(jd);
        final var dailyAngles = MeeusUtils.dailyAngles(T);
        final var solarNoonAngle = solarNoonAngle(cache.longitude, dailyAngles.equationOfTime());

        final var timezoneOffset = startOfDay.getOffset().getTotalSeconds();

        var sunrise = createAstronomicalEventOffset(cache, solarNoonAngle, dailyAngles.solarDeclination(), cache.sunriseAngle, true, timezoneOffset);
        if (sunrise == null) {

        }
        var sunset = createAstronomicalEventOffset(cache, solarNoonAngle, dailyAngles.solarDeclination(), cache.sunsetAngle, false, timezoneOffset);

        Result.EventOffset fajr = createAstronomicalEventOffset(cache, solarNoonAngle, dailyAngles.solarDeclination(), cache.fajrAngle, true, timezoneOffset);
        if (fajr == null) {
            if (cache.previousSunset != null) {
                fajr = switch (cache.prayerTimeMethod.highLatitudeRule) {
                    case TWILIGHT_ANGLE -> {
                        if (cache.previousSunset instanceof Result.EventOffset.AstronomicalEventOffset astronomicalSunset
                                && sunrise instanceof Result.EventOffset.AstronomicalEventOffset astronomicalSunrise) {
                            yield createTwilightAngleAstronomicalEventOffset(astronomicalSunset, astronomicalSunrise, cache.fajrAngle, true);
                        } else {
                            yield new Result.EventOffset.Undefined();
                        }
                    }
                    case NONE -> new Result.EventOffset.Undefined();
                };
            }
        }


        final var result = new Result(fajr, sunrise, sunset);
        cache.previousSunset = result.sunset;
        return result;
    }


    private Result.EventOffset.AdjustedAstronomicalEventOffset createTwilightAngleAstronomicalEventOffset(
            final Result.EventOffset.AstronomicalEventOffset sunset,
            final Result.EventOffset.AstronomicalEventOffset sunrise,
            final double angle,
            final boolean morning) {
        int nightSeconds = sunrise.seconds() - sunset.seconds();
        while (nightSeconds < 0) {
            nightSeconds += 86400;
        }

        final int offset = (int) Math.round(nightSeconds * Math.abs(toDegrees(angle)) / 60);

        final int minutesFromStartOfDay = morning
                ? sunrise.seconds() - offset
                : sunset.seconds() + offset;

        return new Result.EventOffset.AdjustedAstronomicalEventOffset(minutesFromStartOfDay, TWILIGHT_ANGLE);
    }


    public static class Cache {

        private final double latitudeSin;
        private final double latitudeCos;
        private final double longitude;
        private final ZoneId timezone;
        private final double fajrAngle;
        private final double sunriseAngle;
        private final double sunsetAngle;
        private final Method prayerTimeMethod;
        private GetFastingTimesUsecase.Result.@Nullable EventOffset previousSunset;
        private @Nullable NearestValidDays nearestValidDays;


        private Cache(
                final double latitudeSin,
                final double latitudeCos,
                final double longitude,
                final ZoneId timezone,
                final double fajrAngle,
                final double sunriseAngle,
                final double sunsetAngle,
                final Method prayerTimeMethod) {
            this.latitudeSin = latitudeSin;
            this.latitudeCos = latitudeCos;
            this.longitude = longitude;
            this.timezone = timezone;
            this.fajrAngle = fajrAngle;
            this.sunriseAngle = sunriseAngle;
            this.sunsetAngle = sunsetAngle;
            this.prayerTimeMethod = prayerTimeMethod;
        }


        private record NearestValidDays(
                NearestValidDay previous,
                NearestValidDay next
        ) {
            private record NearestValidDay(
                    ZonedDateTime date,
                    int sunrise,
                    int sunset
            ) {
            }
        }
    }


    public record Result(
            EventOffset fajr,
            EventOffset sunrise,
            EventOffset sunset
    ) {
        public sealed interface EventOffset {
            record AstronomicalEventOffset(
                    int seconds
            ) implements EventOffset {
                @Override
                public @NonNull String toString() {
                    return "AstronomicalEventOffset{" +
                            LocalTime.ofSecondOfDay(seconds) +
                            '}';
                }
            }

            record AdjustedAstronomicalEventOffset(
                    int seconds,
                    HighLatitudeRule highLatitudeRule
            ) implements EventOffset {
                @Override
                public @NonNull String toString() {
                    return "AdjustedAstronomicalEventOffset{" +
                            LocalTime.ofSecondOfDay(seconds) +
                            ", " + highLatitudeRule +
                            '}';
                }
            }

            record Undefined(
            ) implements EventOffset {
            }
        }
    }


    static Result.EventOffset.AstronomicalEventOffset createAstronomicalEventOffset(
            Cache cache,
            final double solarNoonAngle,
            final double declination,
            final double elevation,
            final boolean morning,
            final int timezoneOffset) {

        final var hourAngle = MeeusUtils.hourAngle(cache.latitudeSin, cache.latitudeCos, declination, elevation);
        if (hourAngle == null) {
            return null;
        }

        final var utcOffset = eventUtcOffset(solarNoonAngle, hourAngle, morning);

        return new Result.EventOffset.AstronomicalEventOffset(utcOffset + timezoneOffset);
    }


    /**
     * <p>The hour angle is converted into seconds from midnight UTC using
     * the solar noon equation:</p>
     *
     * @param solarNoonAngle solar noon angle in radians
     * @param hourAngle      hour angle in radians (positive magnitude)
     * @param morning        {@code true} for sunrise/Fajr-like events,
     *                       {@code false} for sunset/Isha-like events
     * @return the UTC clock time when the Sun reaches a given hour angle.
     */
    static int eventUtcOffset(
            final double solarNoonAngle,
            final double hourAngle,
            final boolean morning) {

        final var eventAngle = morning
                ? solarNoonAngle - hourAngle
                : solarNoonAngle + hourAngle;

        final var eventUtcOffset = MeeusUtils.SECONDS_PER_RADIAN * eventAngle;

        // normalize to [0, 86400)
        return (int) eventUtcOffset;
    }

    /**
     * Implements folmulae <pre>
     * solarNoon = 12:00 - λ - E
     * </pre>
     *
     * @param longitude           &lambda; - longitude, in radians
     * @param equationOfTimeAngle E - equation of time, in radians
     * @return solar noon angle in radians
     */
    static double solarNoonAngle(
            final double longitude,
            final double equationOfTimeAngle) {
        return Math.PI - longitude - equationOfTimeAngle;
    }
}
