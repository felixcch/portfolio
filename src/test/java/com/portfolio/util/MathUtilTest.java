package com.portfolio.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MathUtilTest {

    private static final double DELTA = 0.0001;

    @Test
    public void normCdf_shouldReturnHalfWhenXIsZero() {
        double result = MathUtil.normCdf(0.0);
        assertEquals(0.5, result, DELTA, "normCdf(0) should be 0.5");
    }

    @Test
    public void normCdf_shouldReturnCorrectValueForPositiveX() {
        // Known value: normCdf(1) ≈ 0.8413
        double result = MathUtil.normCdf(1.0);
        assertEquals(0.8413, result, DELTA, "normCdf(1) should be approximately 0.8413");

        // Known value: normCdf(3) ≈ 0.99865
        result = MathUtil.normCdf(3.0);
        assertEquals(0.99865, result, DELTA, "normCdf(3) should be approximately 0.99865");
    }

    @Test
    public void normCdf_shouldReturnCorrectValueForNegativeX() {
        // Known value: normCdf(-1) ≈ 0.1587
        double result = MathUtil.normCdf(-1.0);
        assertEquals(0.1587, result, DELTA, "normCdf(-1) should be approximately 0.1587");

        // Known value: normCdf(-3) ≈ 0.00135
        result = MathUtil.normCdf(-3.0);
        assertEquals(0.00135, result, DELTA, "normCdf(-3) should be approximately 0.00135");
    }

    @Test
    public void normCdf_shouldReturnOneForPositiveInfinity() {
        double result = MathUtil.normCdf(Double.POSITIVE_INFINITY);
        assertEquals(1.0, result, DELTA, "normCdf(+Infinity) should be 1.0");
    }

    @Test
    public void normCdf_shouldReturnZeroForNegativeInfinity() {
        double result = MathUtil.normCdf(Double.NEGATIVE_INFINITY);
        assertEquals(0.0, result, DELTA, "normCdf(-Infinity) should be 0.0");
    }

    @Test
    public void normCdf_shouldHandleNaNInput() {
        double result = MathUtil.normCdf(Double.NaN);
        assertTrue(Double.isNaN(result), "normCdf(NaN) should return NaN");
    }

}
