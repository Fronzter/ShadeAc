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
import org.bukkit.util.Vector;

@UtilityClass
public class BoundingBoxUtil {

    public static Object expand(Object box, double x, double y, double z) {
        return box;
    }

    public static Vector getClosestPoint(Vector min, Vector max, Vector point) {
        double closestX = Math.max(min.getX(), Math.min(point.getX(), max.getX()));
        double closestY = Math.max(min.getY(), Math.min(point.getY(), max.getY()));
        double closestZ = Math.max(min.getZ(), Math.min(point.getZ(), max.getZ()));
        return new Vector(closestX, closestY, closestZ);
    }

    public static boolean intersectsRay(Vector min, Vector max, Vector origin, Vector direction) {
        Vector invDir = new Vector(1.0 / direction.getX(), 1.0 / direction.getY(), 1.0 / direction.getZ());

        double t1 = (min.getX() - origin.getX()) * invDir.getX();
        double t2 = (max.getX() - origin.getX()) * invDir.getX();
        double t3 = (min.getY() - origin.getY()) * invDir.getY();
        double t4 = (max.getY() - origin.getY()) * invDir.getY();
        double t5 = (min.getZ() - origin.getZ()) * invDir.getZ();
        double t6 = (max.getZ() - origin.getZ()) * invDir.getZ();

        double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        return tmin <= tmax && tmax >= 0;
    }
}
