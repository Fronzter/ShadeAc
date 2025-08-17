package ru.Fronzter.ShadeAc.check.impl.combat.killaura;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.check.Check;
import ru.Fronzter.ShadeAc.data.PlayerData;
import ru.Fronzter.ShadeAc.utils.location.RotationUtil;
import ru.Fronzter.ShadeAc.utils.location.SnapUtil;
import ru.Fronzter.ShadeAc.utils.math.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class KillauraA extends Check {

    private final double minCorrectionAngle, maxFinalAngle, minAcceleration, minSmoothnessRatio;

    public KillauraA(PlayerData data) {
        super(data, "Killaura", "A");

        ShadeAc ac = ShadeAc.getInstance();
        this.minCorrectionAngle = ac.getConfigManager().getCheckValueDouble(getName(), getType(), "min-correction-angle");
        this.maxFinalAngle = ac.getConfigManager().getCheckValueDouble(getName(), getType(), "max-final-angle");
        this.minAcceleration = ac.getConfigManager().getCheckValueDouble(getName(), getType(), "min-acceleration");
        this.minSmoothnessRatio = ac.getConfigManager().getCheckValueDouble(getName(), getType(), "min-smoothness-ratio");
    }

    public void handleUseEntity(PacketContainer packet) {
        if (data.getYawHistory().size() < 3) {
            return;
        }

        Entity target = packet.getEntityModifier(data.getPlayer().getWorld()).read(0);
        if (!(target instanceof Player)) {
            return;
        }

        List<Float> yaws = new ArrayList<>(data.getYawHistory());
        List<Float> pitches = new ArrayList<>(data.getPitchHistory());
        List<Float> deltaYaws = new ArrayList<>(data.getDeltaYawHistory());

        float yawBefore = yaws.get(yaws.size() - 2);
        float pitchBefore = pitches.get(pitches.size() - 2);
        Location locationBefore = data.getPlayer().getEyeLocation().clone();
        locationBefore.setYaw(yawBefore);
        locationBefore.setPitch(pitchBefore);

        float[] angleToTargetAfter = SnapUtil.getAngleToTarget(data.getPlayer(), target.getLocation().add(0, target.getHeight() * 0.8, 0));

        float[] angleToTargetBefore = RotationUtil.getRotations(locationBefore, target.getLocation().add(0, target.getHeight() * 0.8, 0));

        float yawCorrectionNeeded = Math.abs(MathUtil.wrapAngleTo180(locationBefore.getYaw() - angleToTargetBefore[0]));

        float finalMissAngle = Math.abs(angleToTargetAfter[0]);

        float acceleration = SnapUtil.getRotationAcceleration(deltaYaws.get(deltaYaws.size() - 2), deltaYaws.get(deltaYaws.size() - 1));
        double smoothness = SnapUtil.getRotationSmoothnessRatio(deltaYaws.get(deltaYaws.size() - 2), deltaYaws.get(deltaYaws.size() - 1));

        boolean neededCorrection = yawCorrectionNeeded > minCorrectionAngle;
        boolean wasAccurate = finalMissAngle < maxFinalAngle;
        boolean wasJerky = acceleration > minAcceleration || smoothness > minSmoothnessRatio;

        if (neededCorrection && wasAccurate && wasJerky) {
            flag(
                    String.format("needed: %.2f", yawCorrectionNeeded),
                    String.format("miss: %.2f", finalMissAngle),
                    String.format("accel: %.2f", acceleration),
                    String.format("smooth: %.2f", smoothness)
            );
        }
    }
}