package ru.Fronzter.ShadeAc.util.time;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
public final class TimeUtil {

    private TimeUtil() {}

    public static long now() {
        return System.currentTimeMillis();
    }

    public static long timeElapsed(long fromTimestamp) {
        return now() - fromTimestamp;
    }

    public static long ticksToMillis(int ticks) {
        return ticks * 50L;
    }

    public static long millisToTicks(long millis) {
        return millis / 50L;
    }
}
