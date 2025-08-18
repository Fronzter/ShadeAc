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

@UtilityClass
public class MinecraftMath {

    public static final double MINIMUM_SENSITIVITY_DIVISOR = Math.cbrt(0.2) * 8 * 0.15 - 1e-3;

    private static final int[] DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};

    public static double getSensitivityGCD(double a, double b) {
        if (a < b) {
            double temp = a; a = b; b = temp;
        }
        while (b > MINIMUM_SENSITIVITY_DIVISOR) {
            double temp = a % b; a = b; b = temp;
        }
        return a;
    }

    public static int mojangFloor(double num) {
        final int floor = (int) num;
        return num < floor ? floor - 1 : floor;
    }

    public static long packBlockCoords(int x, int y, int z) {
        return (long)x & 67108863L | ((long)z & 67108863L) << 26 | (long)y << 52;
    }

    public static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    public static int smallestEncompassingPowerOfTwo(int value) {
        int output = value - 1;
        output |= output >> 1; output |= output >> 2;
        output |= output >> 4; output |= output >> 8;
        output |= output >> 16;
        return output + 1;
    }

    public static int log2(int value) {
        value = isPowerOfTwo(value) ? value : smallestEncompassingPowerOfTwo(value);
        return DE_BRUIJN_BIT_POSITION[(int)(value * 125613361L >> 27) & 31];
    }
}
