package ru.Fronzter.ShadeAc.check.impl.combat.killaura;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.configuration.ConfigurationSection;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.check.Check;
import ru.Fronzter.ShadeAc.check.CheckCategory;
import ru.Fronzter.ShadeAc.check.CheckInfo;
import ru.Fronzter.ShadeAc.data.PlayerData;
import ru.Fronzter.ShadeAc.data.update.RotationUpdate;
import ru.Fronzter.ShadeAc.util.math.StatisticUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CheckInfo(
        name = "Killaura",
        subType = "B",
        category = CheckCategory.COMBAT,
        description = "Heuristically analyzes rotation patterns over time."
)
public class KillauraB extends Check {

    private static final float HIT_MARKER = 12345.0f;
    private static final float SWING_MARKER = 12346.0f;

    private final List<Float> samples = new ArrayList<>();
    private int ticksSinceAttack = 21;

    private final int sampleSize;
    private final int failsThreshold;
    private final double volatilityThreshold;
    private final double jerkThreshold;

    public KillauraB(PlayerData playerData) {
        super(playerData);
        ConfigurationSection section = ShadeAc.getInstance().getConfigManager().getCheckSection(this);
        if (section != null) {
            this.sampleSize = section.getInt("sample-size", 210);
            this.failsThreshold = section.getInt("fails-threshold", 7);
            this.volatilityThreshold = section.getDouble("volatility-threshold", 120);
            this.jerkThreshold = section.getDouble("jerk-threshold", 60);
        } else {
            this.sampleSize = 210;
            this.failsThreshold = 7;
            this.volatilityThreshold = 120;
            this.jerkThreshold = 60;
        }
    }

    @Override
    public void onRotation(RotationUpdate update) {
        ticksSinceAttack++;
        if (ticksSinceAttack <= 20) {
            samples.add(update.getDeltaYaw());
            if (samples.size() > sampleSize) {
                analyze();
                samples.clear();
            }
        }
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketType type = event.getPacketType();
        if (type == PacketType.Play.Client.USE_ENTITY &&
                event.getPacket().getEntityUseActions().read(0) == EnumWrappers.EntityUseAction.ATTACK) {
            samples.add(HIT_MARKER);
            ticksSinceAttack = 0;
        } else if (type == PacketType.Play.Client.ARM_ANIMATION) {
            samples.add(SWING_MARKER);
        }
    }

    private void analyze() {
        int hits = 0, swings = 0, fails = 0;
        List<Float> shortHistory = new ArrayList<>();

        for (Float sample : samples) {
            if (sample == HIT_MARKER) hits++;
            else if (sample == SWING_MARKER) swings++;

            shortHistory.add(sample);
            if (shortHistory.size() > 6) shortHistory.remove(0);

            if (shortHistory.size() == 6) {
                float v0 = Math.abs(shortHistory.get(0)), v1 = Math.abs(shortHistory.get(1));
                float v2 = shortHistory.get(2), v3 = shortHistory.get(3);
                float v4 = Math.abs(shortHistory.get(4)), v5 = Math.abs(shortHistory.get(5));

                boolean snapping = Math.abs(v0 * 2 - v1 * 2) < 1 && v0 > 10;
                boolean lowOne = v0 < 5, highTwo = v1 > 30;
                boolean hit = v2 == HIT_MARKER, swing = v3 == SWING_MARKER;

                if (lowOne && highTwo && hit && swing) {
                    if (v4 < 5 || (v4 > 30 && v5 < 5)) fails++;
                }

                if (snapping && hit && swing) fails++;
                shortHistory.clear();
            }
        }

        List<Float> clearFrames = samples.stream()
                .filter(s -> s != HIT_MARKER && s != SWING_MARKER)
                .collect(Collectors.toList());

        if (clearFrames.size() < 20) return;

        List<Float> high = clearFrames.stream().filter(s -> s > 10).toList();
        double highAvg = high.stream().mapToDouble(f -> f).average().orElse(0.0);

        double volatility = StatisticUtil.getVolatility(clearFrames);
        double jerk = StatisticUtil.getJerk(clearFrames);

        boolean checkA = fails > failsThreshold && Math.abs(hits - swings) < 2 && hits > 10;
        boolean checkB = Math.abs(hits - swings) < 2 && hits > 10 && volatility > volatilityThreshold
                && highAvg > 50 && high.size() < 45;
        boolean checkC = Math.abs(hits - swings) < 2 && hits > 10 && jerk > jerkThreshold && highAvg > 30;

        if (checkA || checkB || checkC) {
            flag(String.format("F:%d H:%d S:%d V:%.1f J:%.1f", fails, hits, swings, volatility, jerk));
        }
    }
}
