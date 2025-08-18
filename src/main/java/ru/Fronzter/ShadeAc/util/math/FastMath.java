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
import org.jetbrains.annotations.Contract;

@UtilityClass
public class FastMath {

    private static final float[] SIN_TABLE = new float[65536];

    static {
        for (int i = 0; i < SIN_TABLE.length; ++i) {
            SIN_TABLE[i] = (float) StrictMath.sin(i * Math.PI * 2.0D / 65536.0D);
        }
    }

    @Contract(pure = true)
    public static float sin(float value) {
        return SIN_TABLE[(int) (value * 10430.378f) & 0xFFFF];
    }

    @Contract(pure = true)
    public static float cos(float value) {
        return SIN_TABLE[(int) (value * 10430.378f + 16384.0f) & 0xFFFF];
    }
}