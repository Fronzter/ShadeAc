package ru.Fronzter.ShadeAc.utils.location;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.Fronzter.ShadeAc.utils.math.MathUtil;

@UtilityClass
public class SnapUtil {

    public static float[] getAngleToTarget(Player player, Location targetLocation) {
        Location playerEyeLocation = player.getEyeLocation();
        float currentYaw = playerEyeLocation.getYaw();
        float currentPitch = playerEyeLocation.getPitch();

        float[] requiredRotations = RotationUtil.getRotations(playerEyeLocation, targetLocation);
        float requiredYaw = requiredRotations[0];
        float requiredPitch = requiredRotations[1];

        float deltaYaw = MathUtil.wrapAngleTo180(currentYaw - requiredYaw);
        float deltaPitch = currentPitch - requiredPitch;

        return new float[]{deltaYaw, deltaPitch};
    }

    public static double getRotationSmoothnessRatio(float lastDelta, float currentDelta) {
        if (Math.abs(lastDelta) < 0.001f) {
            return Math.abs(currentDelta) > 0.1f ? 1000.0 : 1.0;
        }
        return Math.abs(currentDelta / lastDelta);
    }

    public static float getRotationAcceleration(float delta1, float delta2) {
        return Math.abs(delta2 - delta1);
    }
}
