package ru.Fronzter.ShadeAc.util.anticheat;
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
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

@UtilityClass
public class MovementUtil {

    private static final double BASE_SPEED = 0.21585;
    private static final double SPRINT_SPEED = 0.2806;

    public static double getMaximumSpeed(Player player, Location from, Location to) {
        float forward = 0f, strafe = 0f;
        Vector moveDirection = to.toVector().subtract(from.toVector());
        moveDirection.setY(0);

        if (moveDirection.lengthSquared() > 0.001) {
            float moveAngle = (float) Math.toDegrees(Math.atan2(-moveDirection.getX(), moveDirection.getZ()));
            float yaw = from.getYaw();
            float angleDiff = AimUtil.getAngleDifference(yaw, moveAngle);

            if (Math.abs(angleDiff) <= 46) forward = 1f;
            if (Math.abs(angleDiff) >= 134) forward = -1f;
            if (angleDiff >= -134 && angleDiff <= -46) strafe = 1f;
            if (angleDiff <= 134 && angleDiff >= 46) strafe = -1f;
        }

        double baseSpeed = player.isSprinting() ? SPRINT_SPEED : BASE_SPEED;

        if (forward != 0f && strafe != 0f) {
            baseSpeed *= Math.sqrt(2.0);
        }

        if (player.hasPotionEffect(PotionEffectType.SPEED)) {
            int level = player.getPotionEffect(PotionEffectType.SPEED).getAmplifier() + 1;
            baseSpeed *= 1.0 + (level * 0.2);
        }

        baseSpeed += 0.03;

        return baseSpeed;
    }
}
