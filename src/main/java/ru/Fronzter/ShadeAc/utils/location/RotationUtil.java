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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@UtilityClass
public class RotationUtil {

    private static final double RAD_TO_DEG = 180.0 / Math.PI;

    public static float[] getRotations(Location from, Location to) {
        double deltaX = to.getX() - from.getX();
        double deltaY = to.getY() - from.getY();
        double deltaZ = to.getZ() - from.getZ();

        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) (Math.atan2(deltaZ, deltaX) * RAD_TO_DEG) - 90.0F;
        float pitch = (float) -(Math.atan2(deltaY, horizontalDistance) * RAD_TO_DEG);

        return new float[]{yaw, pitch};
    }

    public static float[] getRotationsToEntity(Player player, Entity target) {
        Location playerEyeLoc = player.getEyeLocation();
        Location targetLoc = target.getLocation().add(0, target.getHeight() / 2, 0);
        return getRotations(playerEyeLoc, targetLoc);
    }

    public static double getRotationDistance(float[] a, float[] b) {
        float yawDiff = Math.abs(ru.Fronzter.ShadeAc.utils.math.MathUtil.wrapAngleTo180(a[0] - b[0]));
        float pitchDiff = Math.abs(a[1] - b[1]);
        return Math.hypot(yawDiff, pitchDiff);
    }
}
