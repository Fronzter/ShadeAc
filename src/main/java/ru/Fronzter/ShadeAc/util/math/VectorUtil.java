package ru.Fronzter.ShadeAc.util.math;
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
import org.bukkit.util.Vector;

@UtilityClass
public class VectorUtil {

    public static double getAngle(final Vector from, final Vector to) {
        return from.angle(to);
    }

    public static Vector getDirection(final Location from, final Location to) {
        return to.toVector().subtract(from.toVector()).normalize();
    }

    public static Vector getLookVector(final Location playerLocation) {
        Vector vector = new Vector();
        double rotX = playerLocation.getYaw();
        double rotY = playerLocation.getPitch();
        vector.setY(-Math.sin(Math.toRadians(rotY)));
        double h = Math.cos(Math.toRadians(rotY));
        vector.setX(-h * Math.sin(Math.toRadians(rotX)));
        vector.setZ(h * Math.cos(Math.toRadians(rotX)));
        return vector;
    }

    public static Vector clamp(Vector vector) {
        double x = MathUtil.clamp(vector.getX(), -3.0E7, 3.0E7);
        double y = MathUtil.clamp(vector.getY(), -2.0E7, 2.0E7);
        double z = MathUtil.clamp(vector.getZ(), -3.0E7, 3.0E7);

        vector.setX(x);
        vector.setY(y);
        vector.setZ(z);

        return vector;
    }
}
