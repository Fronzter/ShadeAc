package ru.Fronzter.ShadeAc.data.update;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import ru.Fronzter.ShadeAc.data.PlayerData;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class RotationUpdate {

    private final PlayerData playerData;
    private final Location from;
    private final Location to;
    private final float deltaYaw;
    private final float deltaPitch;
    private final long timestamp;
    private final boolean onGround;

    public static RotationUpdate create(final PlayerData playerData, final Location to, final Location from, final boolean onGround) {
        float deltaYaw = getAngleDifference(to.getYaw(), from.getYaw());
        float deltaPitch = to.getPitch() - from.getPitch();

        return new RotationUpdate(playerData, from, to, deltaYaw, deltaPitch, System.currentTimeMillis(), onGround);
    }

    private static float getAngleDifference(float angle1, float angle2) {
        float diff = (angle1 - angle2 + 180) % 360 - 180;
        return diff < -180 ? diff + 360 : diff;
    }

    public float getAbsDeltaYaw() {
        return Math.abs(deltaYaw);
    }

    public float getAbsDeltaPitch() {
        return Math.abs(deltaPitch);
    }
}
