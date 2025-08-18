package ru.Fronzter.ShadeAc.util.math;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class StatisticUtil {

    private StatisticUtil() {}

    public static double getKurtosis(final Collection<? extends Number> data) {
        if (data == null || data.size() < 4) {
            return 0.0;
        }
        DescriptiveStatistics stats = new DescriptiveStatistics();
        data.forEach(n -> stats.addValue(n.doubleValue()));
        return stats.getKurtosis();
    }

    public static double getStandardDeviation(final Collection<? extends Number> data) {
        if (data == null || data.size() < 2) {
            return 0.0;
        }
        DescriptiveStatistics stats = new DescriptiveStatistics();
        data.forEach(n -> stats.addValue(n.doubleValue()));
        return stats.getStandardDeviation();
    }

    public static double getAverage(final Collection<? extends Number> data) {
        if (data == null || data.isEmpty()) {
            return 0.0;
        }
        return data.stream().mapToDouble(Number::doubleValue).average().orElse(0.0);
    }

    public static double getVolatility(final Collection<? extends Number> data) {
        if (data == null || data.isEmpty()) return 0.0;
        return data.stream().mapToDouble(n -> Math.pow(n.doubleValue(), 2)).sum();
    }

    public static double getJerk(final List<? extends Number> data) {
        if (data == null || data.size() < 3) return 0.0;

        List<Double> accelerations = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            double v1 = data.get(i - 1).doubleValue();
            double v2 = data.get(i).doubleValue();
            accelerations.add(v2 - v1);
        }

        if (accelerations.size() < 2) return 0.0;

        double totalJerk = 0;
        for (int i = 1; i < accelerations.size(); i++) {
            double a1 = accelerations.get(i - 1);
            double a2 = accelerations.get(i);
            totalJerk += Math.abs(a2 - a1);
        }

        return totalJerk / (accelerations.size() - 1);
    }
}
