package com.github.edwgiz.tayyib.domain.usecase.calendar;


import org.jspecify.annotations.Nullable;

import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;
import static java.lang.Math.toRadians;

/**
 * Implements Meeus' algorithm calculations
 */
public abstract class MeeusUtils {

    /*
     * Earth rotates 360° in 24 hours, therefore 2π radians in 86400 seconds
     */
    public static final double SECONDS_PER_RADIAN = 86400.0 / (2.0 * PI);


    static double julianDay(long epochMillis) {
        return epochMillis / 86_400_000.0 + 2_440_587.5;
    }

    /**
     * J2000.0 corresponds to Julian Day 2451545.0 `2000-01-01T12:00:00Z`.
     *
     * @param julianDay Julian day
     * @return number of Julian centuries since J2000.0
     */
    static double julianCentury(double julianDay) {
        return (julianDay - 2_451_545.0) / 36_525.0;
    }


    /**
     * <p>The hour angle is the angular distance between the Sun and the local
     * meridian. It is used to determine the time when the Sun reaches a given
     * elevation above or below the horizon.</p>
     *
     * <p>The calculation is based on the spherical astronomy formula:</p>
     *
     * <pre>
     * cos(H) = (sin(h) - sin(phi) * sin(delta)) / (cos(phi) * cos(delta))
     * </pre>
     * <p>
     *
     * <p>The returned value is the positive magnitude in radians.
     * Add or subtract it from solar noon to obtain afternoon or morning time.</p>
     *
     * @param latitudeSin sin(phi) - observer latitude sin
     * @param latitudeCos cos(phi) - observer latitude cos
     * @param declination delta - solar declination in radians
     * @param elevation   h - target solar elevation in radians
     * @return H - hour angle as the positive magnitude in radians
     * or {@code null} if the Sun never reaches the requested elevation
     */
    static @Nullable Double hourAngle(
            final double latitudeSin,
            final double latitudeCos,
            final double declination,
            final double elevation) {

        double denominator = latitudeCos * cos(declination);
        if (Math.abs(denominator) < 1e-10) {// Avoid division by zero (poles)
            return null;
        }
        final var cosH = (sin(elevation) - latitudeSin * sin(declination)) / denominator;

        // No sunrise/sunset exists for this elevation
        if (cosH < -1.0 || 1.0 < cosH) {
            return null;
        }

        return acos(cosH);
    }


    /**
     * <p>The equation of time angle is the difference between apparent solar angle
     * and mean solar angle. It accounts for the eccentricity of Earth's orbit
     * and the obliquity of the ecliptic.</p>
     *
     * @param correctedObliquity corrected obliquity of the ecliptic in radians
     * @param solarMeanLongitude solar mean longitude in radians
     * @param solarMeanAnomaly   solar mean anomaly in radians
     * @param T                  Julian centuries since J2000.0
     * @return equation of time in radians.
     * A positive value means the apparent solar longitude is ahead of the mean solar longitude.
     */
    static double equationOfTimeAngle(
            final double correctedObliquity,
            final double solarMeanLongitude,
            final double solarMeanAnomaly,
            final double T) {
        var y = tan(correctedObliquity / 2.0);
        y *= y;

        final var eccentricity = 0.016708634 - T * (0.000042037 + 0.0000001267 * T);
        return y * sin(2.0 * solarMeanLongitude)
                - 2.0 * eccentricity * sin(solarMeanAnomaly)
                + 4.0 * eccentricity * y * sin(solarMeanAnomaly) * cos(2.0 * solarMeanLongitude)
                - 0.5 * y * y * sin(4.0 * solarMeanLongitude)
                - 1.25 * eccentricity * eccentricity
                * sin(2.0 * solarMeanAnomaly);
    }


    /**
     *
     * <p>The solar solarDeclination is the angular distance of the Sun north or
     * south of the celestial equator. It is positive when the Sun is north
     * of the celestial equator and negative when it is south.</p>
     *
     * <pre>
     * &delta; = asin(sin(&epsilon;) * sin(&lambda;))
     * </pre>
     *
     * @param correctedObliquity     &epsilon; - corrected obliquity of the ecliptic in degrees
     * @param solarApparentLongitude &lambda; - solar apparent longitude
     * @return &delta; - solar solarDeclination in radians
     */
    static double solarDeclination(
            final double correctedObliquity,
            final double solarApparentLongitude) {
        return asin(sin(correctedObliquity) * sin(solarApparentLongitude));
    }


    /**
     * Calculates the corrected (apparent) obliquity of the ecliptic.
     *
     * <p>The obliquity is the angle between the Earth's equatorial plane
     * and the ecliptic plane. This method applies a simplified nutation
     * correction to the mean obliquity.</p>
     *
     * @param T Julian centuries since J2000.0
     * @return corrected obliquity of the ecliptic in radians
     */
    static double correctedObliquity(double T, double omega) {
        double meanObliquity = meanObliquity(T);
        return meanObliquity + 4.468042885105484E-5 * cos(omega);
    }


    /**
     * @param T Julian century
     * @return Meeus mean obliquity of the ecliptic in radians
     */
    static double meanObliquity(final double T) {
        double seconds = 21.448 + T * (-46.8150 + T * (-0.00059 + T * 0.001813));
        return toRadians(23.0 + 26.0 / 60.0 + seconds / 3600.0);
    }


    /**
     * @param T Julian century
     * @return precomputed angles
     */
    public static DailyAngles dailyAngles(final double T) {
        double solarMeanLongitudeDegrees = normalizeDegrees(280.46646 + T * (36000.76983 + 0.0003032 * T));
        double solarMeanAnomalyDegrees = normalizeDegrees(357.52911 + T * (35999.05029 - 0.0001537 * T));
        double solarMeanAnomaly = toRadians(solarMeanAnomalyDegrees);
        double equationOfCenterDegrees = (1.914602 - T * (0.004817 + 0.000014 * T)) * sin(solarMeanAnomaly)
                + (0.019993 - 0.000101 * T) * sin(2.0 * solarMeanAnomaly)
                + 0.000289 * sin(3.0 * solarMeanAnomaly);
        double trueLongitudeDegrees = solarMeanLongitudeDegrees + equationOfCenterDegrees;
        double omega = omega(T);
        double apparentLongitude = toRadians(normalizeDegrees(trueLongitudeDegrees - 0.00569 - 0.00478 * sin(omega)));

        final var obliquity = MeeusUtils.correctedObliquity(T, omega);

        return new DailyAngles(
                MeeusUtils.solarDeclination(obliquity, apparentLongitude),
                MeeusUtils.equationOfTimeAngle(obliquity, toRadians(solarMeanLongitudeDegrees), solarMeanAnomaly, T));
    }


    /**
     * All angular values are stored in radians.
     */
    public record DailyAngles(
            double solarDeclination,
            double equationOfTime
    ) {
    }


    static double omega(double T) {
        return toRadians(125.04 - 1934.136 * T);
    }


    static double normalizeDegrees(double degrees) {
        degrees %= 360.0;
        return degrees < 0.0 ? degrees + 360.0 : degrees;
    }


    /**
     * Calculates the Earth's radius at a given geodetic latitude using the
     * WGS84 reference ellipsoid.
     *
     * @param latitudeSin geodetic latitude sin
     * @param latitudeCos geodetic latitude cos
     * @return Earth radius in meters
     */
    static double earthRadius(
            final double latitudeSin,
            final double latitudeCos) {

        final var equatorialRadius = 6_378_137.0;
        final var polarRadius = 6_356_752.314245;

        final var numerator = pow(equatorialRadius * equatorialRadius * latitudeCos, 2)
                + pow(polarRadius * polarRadius * latitudeSin, 2);

        final var denominator = pow(equatorialRadius * latitudeCos, 2) + pow(polarRadius * latitudeSin, 2);

        return sqrt(numerator / denominator);
    }


    private MeeusUtils() {
    }
}
