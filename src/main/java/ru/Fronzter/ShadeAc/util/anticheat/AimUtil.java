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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@UtilityClass
public class AimUtil {

    public static float[] getRotations(Location from, Location to) {
        Vector direction = to.toVector().subtract(from.toVector());
        double distance = Math.sqrt(direction.getX() * direction.getX() + direction.getZ() * direction.getZ());
        float yaw = (float) (Math.toDegrees(Math.atan2(direction.getZ(), direction.getX())) - 90.0F);
        float pitch = (float) -Math.toDegrees(Math.atan(direction.getY() / distance));
        return new float[]{yaw, pitch};
    }

    public static float getAngleToEntity(Player player, Entity target) {
        Location playerEyeLoc = player.getEyeLocation();
        Location targetEyeLoc = target.getLocation().add(0, target.getHeight() * 0.9, 0);
        float[] requiredRotations = getRotations(playerEyeLoc, targetEyeLoc);
        return getAngleDifference(playerEyeLoc.getYaw(), requiredRotations[0]);
    }

    public static float[] getIdealDeltas(Player player, Entity target) {
        Location playerEyeLoc = player.getEyeLocation();
        Location targetEyeLoc = target.getLocation().add(0, target.getHeight() * 0.9, 0);
        float[] requiredRotations = getRotations(playerEyeLoc, targetEyeLoc);
        float deltaYaw = getAngleDifference(requiredRotations[0], playerEyeLoc.getYaw());
        float deltaPitch = requiredRotations[1] - playerEyeLoc.getPitch();
        return new float[]{deltaYaw, deltaPitch};
    }

    public static float getAngleDifference(float a, float b) {
        float diff = Math.abs(a - b) % 360.0f;
        if (diff > 180.0f) diff = 360.0f - diff;
        return diff;
    }
}
