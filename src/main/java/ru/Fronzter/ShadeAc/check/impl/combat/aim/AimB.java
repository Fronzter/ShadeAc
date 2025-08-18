package ru.Fronzter.ShadeAc.check.impl.combat.aim;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.check.Check;
import ru.Fronzter.ShadeAc.check.CheckCategory;
import ru.Fronzter.ShadeAc.check.CheckInfo;
import ru.Fronzter.ShadeAc.data.PlayerData;
import ru.Fronzter.ShadeAc.data.update.RotationUpdate;
import ru.Fronzter.ShadeAc.util.anticheat.AimUtil;
import ru.Fronzter.ShadeAc.util.analysis.HeuristicUtil;
import ru.Fronzter.ShadeAc.util.math.StatisticUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(
        name = "Aim",
        subType = "B",
        category = CheckCategory.COMBAT,
        description = "Detects Smooth/Silent Aim by analyzing rotation correlation and error distribution."
)
public class AimB extends Check {

    private Entity lastTarget;
    private final List<Double> realYawDeltas = new ArrayList<>();
    private final List<Double> idealYawDeltas = new ArrayList<>();

    private final double minCorrelation;
    private final double maxKurtosis;
    private final double minKurtosis;

    public AimB(PlayerData playerData) {
        super(playerData);
        ConfigurationSection section = ShadeAc.getInstance().getConfigManager().getCheckSection(this);
        if (section != null) {
            this.minCorrelation = section.getDouble("min-correlation", 0.98);
            this.maxKurtosis = section.getDouble("max-kurtosis", 0.5);
            this.minKurtosis = section.getDouble("min-kurtosis", -0.5);
        } else {
            this.minCorrelation = 0.98;
            this.maxKurtosis = 0.5;
            this.minKurtosis = -0.5;
        }
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY &&
                event.getPacket().getEntityUseActions().read(0) == EnumWrappers.EntityUseAction.ATTACK) {
            this.lastTarget = event.getPacket().getEntityModifier(playerData.getPlayer().getWorld()).read(0);
        }
    }

    @Override
    public void onRotation(RotationUpdate update) {
        if (lastTarget != null && lastTarget.isValid()) {
            Player player = playerData.getPlayer();
            float[] idealDeltas = AimUtil.getIdealDeltas(player, lastTarget);
            realYawDeltas.add((double) update.getDeltaYaw());
            idealYawDeltas.add((double) idealDeltas[0]);

            if (realYawDeltas.size() >= 20) {
                analyze();
                clear();
            }
        } else {
            clear();
        }
        lastTarget = null;
    }

    private void analyze() {
        double correlation = HeuristicUtil.getCorrelation(realYawDeltas, idealYawDeltas);

        List<Double> errors = new ArrayList<>();
        for (int i = 0; i < realYawDeltas.size(); i++) {
            errors.add(realYawDeltas.get(i) - idealYawDeltas.get(i));
        }
        double kurtosis = StatisticUtil.getKurtosis(errors);

        boolean highCorrelation = !Double.isNaN(correlation) && correlation > minCorrelation;
        boolean abnormalDistribution = kurtosis < minKurtosis || kurtosis > maxKurtosis;

        if (highCorrelation && abnormalDistribution) {
            flag(String.format("corr:%.3f kurt:%.3f", correlation, kurtosis));
        } else {
            decay();
        }
    }

    private void clear() {
        realYawDeltas.clear();
        idealYawDeltas.clear();
    }
}
