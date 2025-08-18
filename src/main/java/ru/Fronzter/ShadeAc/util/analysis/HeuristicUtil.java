package ru.Fronzter.ShadeAc.util.analysis;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import lombok.experimental.UtilityClass;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.Collection;
import java.util.List;

@UtilityClass
public class HeuristicUtil {

    public static double getConsistency(final Collection<? extends Number> data) {
        if (data.size() < 2) return 0.0;
        long distinctCount = data.stream().distinct().count();
        return 1.0 - ((double) distinctCount / data.size());
    }

    public static int getOutliers(final Collection<? extends Number> data) {
        if (data.size() < 5) return 0;
        double[] array = data.stream().mapToDouble(Number::doubleValue).toArray();
        DescriptiveStatistics stats = new DescriptiveStatistics(array);
        double q1 = stats.getPercentile(25);
        double q3 = stats.getPercentile(75);
        double iqr = q3 - q1;
        double lowerBound = q1 - 1.5 * iqr;
        double upperBound = q3 + 1.5 * iqr;

        int outliers = 0;
        for (double val : array) {
            if (val < lowerBound || val > upperBound) outliers++;
        }
        return outliers;
    }

    public static double getCorrelation(final List<? extends Number> data1, final List<? extends Number> data2) {
        if (data1.size() < 5 || data1.size() != data2.size()) return Double.NaN;
        double[] arr1 = data1.stream().mapToDouble(Number::doubleValue).toArray();
        double[] arr2 = data2.stream().mapToDouble(Number::doubleValue).toArray();
        return new PearsonsCorrelation().correlation(arr1, arr2);
    }

    public static double getJitter(final List<? extends Number> data) {
        if (data.size() < 2) return 0.0;
        double sumOfDifferences = 0;
        for (int i = 1; i < data.size(); i++) {
            sumOfDifferences += Math.abs(data.get(i).doubleValue() - data.get(i-1).doubleValue());
        }
        return sumOfDifferences / (data.size() - 1);
    }
}
