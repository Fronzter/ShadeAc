package ru.Fronzter.ShadeAc.utils.math;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class DataAnalyzer {

    private final DescriptiveStatistics stats;

    public DataAnalyzer(int windowSize) {
        this.stats = new DescriptiveStatistics(windowSize);
    }

    public void addDataPoint(double value) {
        stats.addValue(value);
    }

    public void clear() {
        stats.clear();
    }

    public long getSampleSize() {
        return stats.getN();
    }

    public double getMean() {
        return stats.getMean();
    }

    public double getVariance() {
        return stats.getVariance();
    }

    public double getStandardDeviation() {
        return stats.getStandardDeviation();
    }

    public double getKurtosis() {
        return stats.getKurtosis();
    }

    public double getSkewness() {
        return stats.getSkewness();
    }

    public double getMin() {
        return stats.getMin();
    }

    public double getMax() {
        return stats.getMax();
    }

    public double getSum() {
        return stats.getSum();
    }
}