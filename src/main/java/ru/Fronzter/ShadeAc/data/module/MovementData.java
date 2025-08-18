package ru.Fronzter.ShadeAc.data.module;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import lombok.Getter;
import lombok.Setter;
import ru.Fronzter.ShadeAc.data.PlayerData;

@Getter
@Setter
public class MovementData {
    private final PlayerData data;

    private double deltaX, deltaY, deltaZ, deltaXZ;
    private double lastDeltaX, lastDeltaY, lastDeltaZ, lastDeltaXZ;
    private boolean onGround, lastOnGround;
    private int airTicks, groundTicks;
    private float friction;
    private int ticksSinceVelocity;
    private int ticksSinceTeleport;
    private int ticksSinceJump;
    private double verticalVelocity;


    public MovementData(PlayerData data) {
        this.data = data;
    }
}