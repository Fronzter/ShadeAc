package ru.Fronzter.ShadeAc.utils.math;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */

import java.util.Collection;

public class MathUtil {

    public static long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public static float gcd(float a, float b) {
        float n = 10000.0f;
        return (float) gcd((long)(a * n), (long)(b * n)) / n;
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    public static float wrapAngleTo180(float angle) {
        angle %= 360.0F;
        if (angle >= 180.0F) angle -= 360.0F;
        if (angle < -180.0F) angle += 360.0F;
        return angle;
    }

    public static double getAverage(Collection<? extends Number> collection) {
        if (collection.isEmpty()) return 0.0;
        return collection.stream().mapToDouble(Number::doubleValue).average().getAsDouble();
    }

    public static double lerp(double a, double b, double f) {
        return a + f * (b - a);
    }
}
