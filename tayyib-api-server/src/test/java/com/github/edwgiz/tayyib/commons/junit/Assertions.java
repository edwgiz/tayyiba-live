package com.github.edwgiz.tayyib.commons.junit;

import static java.lang.Double.isNaN;
import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.fail;

public final class Assertions {

    public enum DoubleTolerance {
        TEN_TO_MINUS_3(1e-3),
        TEN_TO_MINUS_13(1e-13);


        private final double delta;

        DoubleTolerance(final double delta) {
            this.delta = delta;
        }
    }


    public static void assertClose(double expected, double actual, DoubleTolerance tolerance) {
        if ((isNaN(expected) && isNaN(actual))
                || expected == actual
                || abs(expected - actual) <= tolerance.delta) {
            return;
        }
        fail("expected <%s> ± %s but was <%s>".formatted(expected, tolerance.delta, actual));
    }


    private Assertions() {
    }
}
