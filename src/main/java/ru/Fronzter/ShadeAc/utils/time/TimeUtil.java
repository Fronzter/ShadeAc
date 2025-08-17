package ru.Fronzter.ShadeAc.utils.time;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */

public class TimeUtil {

    private TimeUtil() {
    }

    public static long getMillis() {
        return System.currentTimeMillis();
    }

    public static long timeSince(long fromTimestamp) {
        return getMillis() - fromTimestamp;
    }

    public static double millisToTicks(long millis) {
        return millis / 50.0;
    }

    public static long ticksToMillis(double ticks) {
        return (long) (ticks * 50.0);
    }
}
