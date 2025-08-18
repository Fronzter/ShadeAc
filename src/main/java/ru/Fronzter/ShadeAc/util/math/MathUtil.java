package ru.Fronzter.ShadeAc.util.math;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import lombok.experimental.UtilityClass;

import java.util.Collection;

@UtilityClass
public class MathUtil {

    public static long gcd(long a, long b) {
        while (b > 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    public static long getGcdFromCollection(final Collection<? extends Number> numbers) {
        if (numbers.isEmpty()) return 0;
        long result = numbers.iterator().next().longValue();
        for (Number val : numbers) {
            result = gcd(val.longValue(), result);
        }
        return result;
    }

    public static double clamp(double num, double min, double max) {
        if (num < min) return min;
        return Math.min(num, max);
    }

    public static int clamp(int num, int min, int max) {
        if (num < min) return min;
        return Math.min(num, max);
    }

    public static float clamp(float num, float min, float max) {
        if (num < min) return min;
        return Math.min(num, max);
    }

    public static double lerp(double amount, double start, double end) {
        return start + amount * (end - start);
    }

    public static double square(double num) {
        return num * num;
    }

    public static float square(float value) {
        return value * value;
    }

    public static boolean isBetween(double value, double min, double max) {
        return value > min && value < max;
    }

    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    public static boolean isNearlyEqual(double a, double b, double epsilon) {
        return Math.abs(a - b) < epsilon;
    }
}
