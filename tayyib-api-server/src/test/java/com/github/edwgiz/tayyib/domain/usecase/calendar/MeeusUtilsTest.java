package com.github.edwgiz.tayyib.domain.usecase.calendar;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Instant;

import static com.github.edwgiz.tayyib.commons.junit.Assertions.DoubleTolerance.TEN_TO_MINUS_13;
import static com.github.edwgiz.tayyib.commons.junit.Assertions.DoubleTolerance.TEN_TO_MINUS_3;
import static com.github.edwgiz.tayyib.commons.junit.Assertions.assertClose;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;


@TestInstance(PER_CLASS)
class MeeusUtilsTest {


    @Test
    void normalizeDegrees() {
        assertClose(0.1, MeeusUtils.normalizeDegrees(-719.9), TEN_TO_MINUS_13);
        assertClose(0.0, MeeusUtils.normalizeDegrees(-360.0), TEN_TO_MINUS_13);
        assertClose(0.1, MeeusUtils.normalizeDegrees(-359.9), TEN_TO_MINUS_13);
        assertEquals(0.0, MeeusUtils.normalizeDegrees(0.0));
        assertEquals(359.9, MeeusUtils.normalizeDegrees(359.9));
        assertEquals(0.0, MeeusUtils.normalizeDegrees(360.0));
        assertEquals(1.0, MeeusUtils.normalizeDegrees(361.0));
        assertEquals(359.9, MeeusUtils.normalizeDegrees(719.9));
    }


    @Nested
    final class JulianDayCase {
        @Test
        void test() {
            test(2461249.5, "2026-07-28T00:00:00Z");
            test(2461249.75, "2026-07-28T06:00:00Z");
            test(2461250.0, "2026-07-28T12:00:00Z");
            test(2461250.25, "2026-07-28T18:00:00Z");
        }


        private static void test(final double expectedJulian, final String givenGregorian) {
            assertEquals(expectedJulian, MeeusUtils.julianDay(Instant.parse(givenGregorian).toEpochMilli()));
        }
    }


    @Test
    void julianCentury() {
        assertEquals(0.0, MeeusUtils.julianCentury(2451545.0));
        assertEquals(1.0, MeeusUtils.julianCentury(2488070.0));
        assertEquals(0.2657084188911704, MeeusUtils.julianCentury(2461250.0));
    }


    @Test
    void meanObliquity() {
        assertEquals(toRadians(23.43929111111111), MeeusUtils.meanObliquity(0.0));
    }


    @Test
    void correctedObliquity() {
        final var T = 0.0;
        final var omega = MeeusUtils.omega(T);
        assertEquals(toRadians(23.437821291789415), MeeusUtils.correctedObliquity(T, omega));
    }


    @Test
    void solarDeclination() {
        // J2000-like obliquity and zero longitude: equator
        assertEquals(0, MeeusUtils.solarDeclination(toRadians(23.437821291789415), 0));
        // June solstice: maximum northern solarDeclination
        assertEquals(toRadians(23.43782129178942), MeeusUtils.solarDeclination(toRadians(23.437821291789415), toRadians(90)));
        // December solstice: maximum southern solarDeclination
        assertEquals(toRadians(-23.43782129178942), MeeusUtils.solarDeclination(toRadians(23.437821291789415), toRadians(270)));
        // Symmetry check
        assertEquals(
                MeeusUtils.solarDeclination(23.437821291789415, toRadians(45)),
                -MeeusUtils.solarDeclination(23.437821291789415, toRadians(225)));
    }


    @Test
    void solarDeclination_2026JuneSolstice_isApproximatelyCorrect() {
        final var jd = MeeusUtils.julianDay(Instant.parse("2026-06-21T12:00:00Z").toEpochMilli());
        final var T = MeeusUtils.julianCentury(jd);
        final var dailyAngles = MeeusUtils.dailyAngles(T);
        assertClose(toRadians(23.438051663157562), dailyAngles.solarDeclination(), TEN_TO_MINUS_13);
    }


    /*
     *     @Test
     *     void hourAngle() {
     *         assertEquals(toRadians(90.0), MeeusUtils.hourAngle(0.0, 0.0, 0.0));
     *         // For Moscow on a summer day:
     *         assertEquals(toRadians(137.07191812233398), MeeusUtils.hourAngle(toRadians(55.75), toRadians(20.0), toRadians(-6.0)));
     *     }
     */


    @Nested
    final class HourAngleTest {
        @Test
        void test() {
            test(toRadians(90.0), 0.0, 0.0, 0.0);
            test(toRadians(137.07191812233398), toRadians(55.75), toRadians(20.0), toRadians(-6.0));
        }


        private static void test(
                final double expected,
                final double latitude,
                final double declination,
                final double elevation
        ) {
            assertEquals(expected, MeeusUtils.hourAngle(sin(latitude), cos(latitude), declination, elevation));
        }
    }


    @Nested
    final class EquationOfTimeAngleTest {
        @Test
        void test() {
            test("2026-02-15T12:00:00Z", -0.21948133834143208, -0.061601 );
            test("2026-04-15T12:00:00Z", 0.17233223214931556, 0.0 );
            test("2026-06-13T12:00:00Z", 0.40537324674646724, 0.0);
            test("2026-09-01T12:00:00Z", 0.14252618105574263, 0.0);
            test("2026-11-01T12:00:00Z", -0.25344201093849356, 0.0718598);
            test("2026-12-25T12:00:00Z", -0.40816388486446276, 0.0);
        }

        private static void test(
                final String givenGregorian,
                final double expectedDeclinationAngle,
                final double expectedEquationOfTimeAngle) {
            final var jd = MeeusUtils.julianDay(Instant.parse(givenGregorian).toEpochMilli());
            final var T = MeeusUtils.julianCentury(jd);
            final var dailyAngles = MeeusUtils.dailyAngles(T);
            assertEquals(expectedDeclinationAngle, dailyAngles.solarDeclination());
            assertClose(expectedEquationOfTimeAngle, dailyAngles.equationOfTime(), TEN_TO_MINUS_3);
        }
    }

}
