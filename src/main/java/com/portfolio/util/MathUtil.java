package com.portfolio.util;

public class MathUtil {
    public static double normCdf(double x) {
        double t = 1.0 / (1.0 + 0.2316419 * Math.abs(x));
        double d = 0.3989423 * Math.exp(-x * x / 2);
        double p = d * t * (0.31938153 + t * (-0.356563782 + t * (1.781477937 +
                t * (-1.821255978 + t * 1.330274429))));
        return x >= 0 ? 1 - p : p;
    }
}
