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
import org.bukkit.util.Vector;

@UtilityClass
public class VectorUtil {

    public static Vector getDirection(Location from, Location to) {
        return to.toVector().subtract(from.toVector());
    }

    public static double getAngleBetweenVectors(Vector a, Vector b) {
        return Math.toDegrees(a.angle(b));
    }

    public static Vector project(Vector a, Vector b) {
        return b.clone().multiply(a.dot(b) / (b.lengthSquared()));
    }
}