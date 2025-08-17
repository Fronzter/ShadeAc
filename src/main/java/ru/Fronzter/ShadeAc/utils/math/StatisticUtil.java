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
import java.util.List;
import java.util.stream.Collectors;

public class StatisticUtil {

    public static double getVariance(Collection<? extends Number> data) {
        if (data.size() < 2) return 0.0;
        double mean = MathUtil.getAverage(data);
        double temp = 0;
        for (Number a : data) {
            double diff = a.doubleValue() - mean;
            temp += diff * diff;
        }
        return temp / (data.size() - 1);
    }

    public static double getStandardDeviation(Collection<? extends Number> data) {
        return Math.sqrt(getVariance(data));
    }

    public static double getKurtosis(Collection<? extends Number> data) {
        if (data.size() < 4) return 0.0;
        double mean = MathUtil.getAverage(data);
        double stdDev = getStandardDeviation(data);
        if (stdDev == 0) return 0.0;

        double n = data.size();
        double sum = data.stream().mapToDouble(x -> Math.pow(x.doubleValue() - mean, 4)).sum();
        return (sum / (n * Math.pow(stdDev, 4))) - 3;
    }

    public static List<Double> getOutliers(Collection<? extends Number> data) {
        List<Double> sortedData = data.stream()
                .map(Number::doubleValue)
                .sorted()
                .collect(Collectors.toList());

        if (sortedData.size() < 4) return java.util.Collections.emptyList();

        int size = sortedData.size();
        double q1 = sortedData.get((int) (size * 0.25));
        double q3 = sortedData.get((int) (size * 0.75));
        double iqr = q3 - q1;
        double lowerBound = q1 - 1.5 * iqr;
        double upperBound = q3 + 1.5 * iqr;

        return sortedData.stream()
                .filter(val -> val < lowerBound || val > upperBound)
                .collect(Collectors.toList());
    }
}
