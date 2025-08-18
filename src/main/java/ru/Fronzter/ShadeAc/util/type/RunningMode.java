package ru.Fronzter.ShadeAc.util.type;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class RunningMode {

    private final Deque<Double> samples;
    private final Map<Double, Integer> frequencyMap;
    private final int windowSize;

    private Map.Entry<Double, Integer> modeEntry;

    public RunningMode(int windowSize) {
        this.windowSize = windowSize;
        this.samples = new ArrayDeque<>(windowSize);
        this.frequencyMap = new HashMap<>();
    }

    public void add(double value) {

        samples.add(value);
        frequencyMap.merge(value, 1, Integer::sum);

        if (samples.size() > windowSize) {
            double oldest = samples.removeFirst();
            frequencyMap.compute(oldest, (k, v) -> (v == null || v == 1) ? null : v - 1);
        }

        modeEntry = null;
    }

    public Map.Entry<Double, Integer> getMode() {
        if (modeEntry == null) {
            modeEntry = frequencyMap.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);
        }
        return modeEntry;
    }

    public int size() {
        return samples.size();
    }
}