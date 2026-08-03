package com.github.edwgiz.tayyib.domain.usecase.calendar;


import com.github.edwgiz.tayyib.domain.model.CalendarDayPair;
import com.github.edwgiz.tayyib.domain.model.GeoLocation;
import com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod;
import com.github.edwgiz.tayyib.domain.usecase.calendar.GetFastingTimesUsecase.Cache.NearestAstronomicalBounds.AstronomicalSunriseAndSunset;
import com.github.edwgiz.tayyib.domain.usecase.calendar.GetFastingTimesUsecase.Cache.NearestAstronomicalBounds.AstronomicalSunriseAndSunset.AstronomicalEventOffset;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;

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
        MWL(-18.0, TWILIGHT_ANGLE),
        ISNA(-15.0, TWILIGHT_ANGLE),
        EGAS(-19.5, TWILIGHT_ANGLE),
        UMM_AL_QURA(-18.5, NONE),
        KARACHI(-18.0, TWILIGHT_ANGLE),
        TEHRAN(-17.7, TWILIGHT_ANGLE),
        SHIA_QUM(-16.0, TWILIGHT_ANGLE),
        GULF(-18.0, TWILIGHT_ANGLE),
        KUWAIT(-18.0, TWILIGHT_ANGLE),
        QATAR(-18.0, TWILIGHT_ANGLE),
        MUIS(-20.0, TWILIGHT_ANGLE),
        UOIF(-12.0, TWILIGHT_ANGLE),
        DIYANET(-18.0, TWILIGHT_ANGLE),
        SAMR(-16.0, TWILIGHT_ANGLE),
        MSC(-18.0, TWILIGHT_ANGLE),
        DUBAI(-18.2, TWILIGHT_ANGLE),
        JAKIM(-20.0, TWILIGHT_ANGLE),
        TUNISIA(-18.0, TWILIGHT_ANGLE),
        ALGERIA(-18.0, TWILIGHT_ANGLE),
        KEMENAG(-20.0, TWILIGHT_ANGLE),
        MOROCCO(-19.0, TWILIGHT_ANGLE),
        CIL(-18.0, TWILIGHT_ANGLE);


        private final double fajrAngle;
        private final HighLatitudeRule highLatitudeRule;

        Method(final double fajrAngle, HighLatitudeRule highLatitudeRule) {
            this.fajrAngle = toRadians(fajrAngle);
            this.highLatitudeRule = highLatitudeRule;
        }
    }


    public @Nullable Cache createCache(
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
        final var prerequisites = createAstronomicalEventOffsetPrerequisites(cache, startOfDay);
        final var sunrise = createLightTransitionEventOffset(cache, prerequisites, cache.sunriseAngle, true, Cache.NearestAstronomicalBounds::sunrise);
        final var sunset = createLightTransitionEventOffset(cache, prerequisites, cache.sunsetAngle, false, Cache.NearestAstronomicalBounds::sunset);

        var fajr = (Result.EventOffset) createAstronomicalEventOffset(cache, prerequisites, cache.fajrAngle, true);
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

    private Result.EventOffset createLightTransitionEventOffset(
            final Cache cache,
            final AstronomicalEventOffsetPrerequisites prerequisites,
            final double elevation,
            final boolean morning,
            final Function<Cache.NearestAstronomicalBounds, Cache.NearestAstronomicalBounds.InterpolationPrerequisites> getter) {
        var eventOffset = (Result.EventOffset) createAstronomicalEventOffset(cache, prerequisites, elevation, morning);
        if (eventOffset == null) {
            final var nearestAstronomicalBounds = getNearestAstronomicalBounds(cache, prerequisites.day());
            if (nearestAstronomicalBounds == null) {
                eventOffset = new Result.EventOffset.Undefined();
            } else {
                eventOffset = interpolateAstronomicalEvent(prerequisites.day(), nearestAstronomicalBounds, getter);
            }
        }
        return eventOffset;
    }


    private static GetFastingTimesUsecase.AstronomicalEventOffsetPrerequisites createAstronomicalEventOffsetPrerequisites(
            final Cache cache,
            final ZonedDateTime startOfDay) {
        final var timezoneOffset = startOfDay.getOffset().getTotalSeconds();
        final var jd = MeeusUtils.julianDay(startOfDay.plusHours(12).toEpochSecond() * 1000L);
        final var T = MeeusUtils.julianCentury(jd);
        final var dailyAngles = MeeusUtils.dailyAngles(T);
        final var solarNoonAngle = solarNoonAngle(cache.longitude, dailyAngles.equationOfTime());
        return new AstronomicalEventOffsetPrerequisites(startOfDay, timezoneOffset, dailyAngles, solarNoonAngle);
    }


    private record AstronomicalEventOffsetPrerequisites(
            ZonedDateTime day,
            int timezoneOffset,
            MeeusUtils.DailyAngles dailyAngles,
            double solarNoonAngle) {
    }


    private static Cache.@Nullable NearestAstronomicalBounds getNearestAstronomicalBounds(
            final Cache cache,
            final ZonedDateTime startOfDay) {
        if (cache.nearestAstronomicalBounds != null) {
            return cache.nearestAstronomicalBounds;
        }

        final var previous = createAstronomicalSunriseAndSunset(cache, startOfDay, true);
        final var next = createAstronomicalSunriseAndSunset(cache, startOfDay, false);
        if (previous != null && next != null) {
            cache.nearestAstronomicalBounds = new Cache.NearestAstronomicalBounds(
                    createNearestAstronomicalBoundsInterpolationPrerequisites(previous, next, AstronomicalSunriseAndSunset::sunrise),
                    createNearestAstronomicalBoundsInterpolationPrerequisites(previous, next, AstronomicalSunriseAndSunset::sunset));
        }
        return cache.nearestAstronomicalBounds;
    }

    private static Cache.NearestAstronomicalBounds.@NonNull InterpolationPrerequisites createNearestAstronomicalBoundsInterpolationPrerequisites(
            final AstronomicalSunriseAndSunset previous,
            final AstronomicalSunriseAndSunset next,
            final Function<AstronomicalSunriseAndSunset, AstronomicalEventOffset> getter) {
        final var previousEventOffset = getter.apply(previous);
        final var nextEventOffset = getter.apply(next);
        return new Cache.NearestAstronomicalBounds.InterpolationPrerequisites(
                previousEventOffset.astronomicalEventOffset().seconds(),
                previousEventOffset.epochSecond(),
                nextEventOffset.astronomicalEventOffset().seconds() - previousEventOffset.astronomicalEventOffset().seconds(),
                nextEventOffset.epochSecond() - previousEventOffset.epochSecond());
    }


    private static @Nullable AstronomicalSunriseAndSunset createAstronomicalSunriseAndSunset(
            final Cache cache,
            final ZonedDateTime startDay,
            final boolean backward) {
        AstronomicalEventOffset sunriseResult = null;
        AstronomicalEventOffset sunsetResult = null;
        final var direction = backward ? -1L : 1L;
        for (long i = 0; i < 366; i += 1) {
            final var day = startDay.plusDays(direction * i);
            final var prerequisites = createAstronomicalEventOffsetPrerequisites(cache, day);
            final var sunrise = createAstronomicalEventOffset(cache, prerequisites, cache.sunriseAngle, true);
            if (sunrise != null && sunriseResult == null) {
                sunriseResult = new AstronomicalEventOffset(day.toEpochSecond(), sunrise);
            }
            final var sunset = createAstronomicalEventOffset(cache, prerequisites, cache.sunsetAngle, false);
            if (sunset != null && sunsetResult == null) {
                sunsetResult = new AstronomicalEventOffset(day.toEpochSecond(), sunset);
            }
            if (sunriseResult != null && sunsetResult != null) {
                return new AstronomicalSunriseAndSunset(sunriseResult, sunsetResult);
            }
        }
        return null;
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


    private Result.EventOffset.AstronomicalEventOffset interpolateAstronomicalEvent(
            final ZonedDateTime day,
            final Cache.NearestAstronomicalBounds nearestAstronomicalBounds,
            final Function<Cache.NearestAstronomicalBounds, Cache.NearestAstronomicalBounds.InterpolationPrerequisites> getter) {

        final var epoch = day.toEpochSecond();
        final var prerequisites = getter.apply(nearestAstronomicalBounds);

        final var eventSeconds = prerequisites.previousEventOffset() +
                (epoch - prerequisites.previousEpoch())
                        * prerequisites.eventOffsetDelta()
                        / prerequisites.epochDelta();

        return new Result.EventOffset.AstronomicalEventOffset((int) eventSeconds);
    }


    public static class Cache {
        public record NearestAstronomicalBounds(
                InterpolationPrerequisites sunrise,
                InterpolationPrerequisites sunset
        ) {
            /**
             * All values are in seconds
             *
             * @param previousEventOffset previous existing event (sunrise or sunset) offset
             * @param previousEpoch       previous existing event (sunrise or sunset) day
             * @param eventOffsetDelta    offset delta between next and previous existing event pair
             * @param epochDelta          start day delta between next and previous existing event pair
             */
            public record InterpolationPrerequisites(
                    int previousEventOffset,
                    long previousEpoch,
                    int eventOffsetDelta,
                    long epochDelta
            ) {
            }

            public record AstronomicalSunriseAndSunset(
                    AstronomicalEventOffset sunrise,
                    AstronomicalEventOffset sunset
            ) {
                /**
                 * @param epochSecond             epoch second, UTC
                 * @param astronomicalEventOffset sunrise astronomical event offset
                 */
                public record AstronomicalEventOffset(
                        long epochSecond,
                        Result.EventOffset.AstronomicalEventOffset astronomicalEventOffset
                ) {
                }
            }
        }


        public record ProlongedCalendarEvent(
                List<CalendarDayPair.CalendarEventReason> reasons,
                int endMinute
        ) {
        }


        private final double latitudeSin;
        private final double latitudeCos;
        private final double longitude;
        private final ZoneId timezone;
        private final double fajrAngle;
        private final double sunriseAngle;
        private final double sunsetAngle;
        private final Method prayerTimeMethod;
        public final TreeMap<LocalDate, ProlongedCalendarEvent> prolongedCalendarEvents;
        private GetFastingTimesUsecase.Result.@Nullable EventOffset previousSunset;

        /**
         * The nearest sunrise and sunset bounds of polar day or night
         */
        private @Nullable NearestAstronomicalBounds nearestAstronomicalBounds;

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
            this.prolongedCalendarEvents = new TreeMap<>();
            this.prayerTimeMethod = prayerTimeMethod;
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
                            formatSeconds(seconds) +
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
                            formatSeconds(seconds) +
                            ", " + highLatitudeRule +
                            '}';
                }
            }

            record Undefined(
            ) implements EventOffset {
            }

            private static String formatSeconds(long seconds) {
                var dayOffsetCorrection = seconds / 86400;
                var dayOffset = dayOffsetCorrection;
                if (seconds < 0) {
                    seconds += 86400;
                    dayOffset--;
                }
                if (dayOffset == 0) {
                    return LocalTime.ofSecondOfDay(seconds).toString();
                } else {
                    return (dayOffset > 0 ? "+" : "") + dayOffset + " " + LocalTime.ofSecondOfDay(seconds - dayOffsetCorrection * 86400);
                }
            }
        }
    }


    static Result.EventOffset.AstronomicalEventOffset createAstronomicalEventOffset(
            final Cache cache,
            final AstronomicalEventOffsetPrerequisites prerequisites,
            final double elevation,
            final boolean morning) {

        final var hourAngle = MeeusUtils.hourAngle(
                cache.latitudeSin,
                cache.latitudeCos,
                prerequisites.dailyAngles().solarDeclination(),
                elevation);

        if (hourAngle == null) {
            return null;
        }

        final var utcOffset = eventUtcOffset(prerequisites.solarNoonAngle(), hourAngle, morning);

        return new Result.EventOffset.AstronomicalEventOffset(utcOffset + prerequisites.timezoneOffset());
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
     * Implements formulae <pre>
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
