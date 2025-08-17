package ru.Fronzter.ShadeAc.check.impl.combat.autoclicker;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.check.Check;
import ru.Fronzter.ShadeAc.data.PlayerData;
import ru.Fronzter.ShadeAc.utils.math.DataAnalyzer;

public class AutoclickerA extends Check {

    private final double maxStdDev, minKurtosis, minCps;

    public AutoclickerA(PlayerData data) {
        super(data, "Autoclicker", "A");

        this.maxStdDev = ShadeAc.getInstance().getConfigManager().getCheckValueDouble(getName(), getType(), "max-std-dev");
        this.minKurtosis = ShadeAc.getInstance().getConfigManager().getCheckValueDouble(getName(), getType(), "min-kurtosis");
        this.minCps = ShadeAc.getInstance().getConfigManager().getCheckValueDouble(getName(), getType(), "min-cps");
    }

    public void handleAttack() {
        DataAnalyzer analyzer = data.getClickTimings();

        if (analyzer.getSampleSize() < 15) return;

        double stdDev = analyzer.getStandardDeviation();
        double kurtosis = analyzer.getKurtosis();

        if (stdDev < this.maxStdDev && kurtosis > this.minKurtosis) {
            double meanDelay = analyzer.getMean();
            if (meanDelay == 0) return; // Избегаем деления на ноль

            double cps = 1000.0 / meanDelay;

            if (cps > this.minCps) {
                flag("stdDev: " + String.format("%.2f", stdDev), "kurt: " + String.format("%.2f", kurtosis), "cps: " + String.format("%.1f", cps));
            }
        }
    }
}