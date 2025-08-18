package ru.Fronzter.ShadeAc.config;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Particle;
import org.bukkit.Sound;

@Getter
@RequiredArgsConstructor
public class AnimationConfig {

    private final boolean enabled;
    private final String postAnimationCommand;
    private final int duration;
    private final double liftSpeed;
    private final Particle particleType;
    private final int particleCount;
    private final double radius;
    private final double rotationSpeed;
    private final boolean colorShifting;
    private final float colorShiftSpeed;
    private final boolean explosionEnabled;
    private final Particle explosionParticle;
    private final Sound explosionSound;
    private final float explosionVolume;
    private final float explosionPitch;
}