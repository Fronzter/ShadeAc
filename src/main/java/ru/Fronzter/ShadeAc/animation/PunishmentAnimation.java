package ru.Fronzter.ShadeAc.animation;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.manager.PunishmentManager;

import java.awt.Color;

public class PunishmentAnimation extends BukkitRunnable {

    private final Player player;
    private final String finalCommand;
    private final PunishmentManager pm;
    private long ticksRun = 0;
    private double currentAngle = 0;
    private float hue = 0.0f;

    private final String style;
    private final long duration;
    private final double liftSpeed;
    private final Particle spiralParticle;
    private final boolean colorShifting;
    private final double colorShiftSpeed;
    private final int spiralParticleCount;
    private final double spiralRadius;
    private final double rotationSpeed;
    private final boolean verticalMovementEnabled;
    private final double verticalAmplitude;
    private final double verticalSpeed;
    private final boolean ringsEnabled;
    private final Particle ringParticle;
    private final int ringFrequency;
    private final int ringParticleCount;
    private final double ringRadius;

    public PunishmentAnimation(ShadeAc plugin, Player player, String finalCommand) {
        this.player = player;
        this.finalCommand = finalCommand;
        this.pm = plugin.getPunishmentManager();

        this.style = pm.getAnimationStyle();
        this.duration = pm.getAnimationDuration();
        this.liftSpeed = pm.getAnimationLiftSpeed();
        this.spiralParticle = pm.getSpiralParticle();
        this.colorShifting = pm.isSpiralColorShifting();
        this.colorShiftSpeed = pm.getColorShiftSpeed();
        this.spiralParticleCount = pm.getSpiralParticleCount();
        this.spiralRadius = pm.getSpiralRadius();
        this.rotationSpeed = pm.getSpiralRotationSpeed();
        this.verticalMovementEnabled = pm.isSpiralVerticalMovementEnabled();
        this.verticalAmplitude = pm.getSpiralVerticalAmplitude();
        this.verticalSpeed = pm.getSpiralVerticalSpeed();
        this.ringsEnabled = pm.isRingsEnabled();
        this.ringParticle = pm.getRingParticle();
        this.ringFrequency = pm.getRingFrequency();
        this.ringParticleCount = pm.getRingParticleCount();
        this.ringRadius = pm.getRingRadius();

        player.setInvulnerable(true);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    @Override
    public void run() {
        if (!player.isOnline() || ticksRun >= duration) {
            finish();
            return;
        }

        player.teleport(player.getLocation().add(0, liftSpeed, 0));

        switch (style) {
            case "DOUBLE_HELIX":
                drawDoubleHelix();
                break;
            case "SINGLE_SPIRAL":
            default:
                drawSingleSpiral();
                break;
        }

        if (ringsEnabled && ticksRun % ringFrequency == 0) {
            drawRing();
        }

        ticksRun++;
    }

    private void drawSingleSpiral() {
        drawSpiral(0);
    }

    private void drawDoubleHelix() {
        drawSpiral(0);
        drawSpiral(Math.PI);
    }

    private void drawSpiral(double angleOffset) {
        Location playerLoc = player.getLocation();
        for (int i = 0; i < spiralParticleCount; i++) {
            double angle = currentAngle + (i * (360.0 / spiralParticleCount));
            double angleRad = Math.toRadians(angle) + angleOffset;

            double x = playerLoc.getX() + spiralRadius * Math.cos(angleRad);
            double z = playerLoc.getZ() + spiralRadius * Math.sin(angleRad);

            double y = playerLoc.getY() + 1.0; // Центрируем на уровне головы
            if (verticalMovementEnabled) {
                double verticalOffset = verticalAmplitude * Math.sin(Math.toRadians(currentAngle * verticalSpeed));
                y += verticalOffset;
            }

            spawnSpiralParticle(new Location(player.getWorld(), x, y, z));
        }
        currentAngle += rotationSpeed / spiralParticleCount;
    }

    private void spawnSpiralParticle(Location location) {
        if (spiralParticle == Particle.REDSTONE) {
            Color awtColor = Color.getHSBColor(colorShifting ? hue : 0.0f, 1.0f, 1.0f);
            hue += colorShifting ? colorShiftSpeed : 0.0f;
            if (hue > 1.0f) hue = 0.0f;

            Particle.DustOptions dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue()), 1.0F);
            player.getWorld().spawnParticle(spiralParticle, location, 0, dustOptions);
        } else {
            player.getWorld().spawnParticle(spiralParticle, location, 0);
        }
    }

    private void drawRing() {
        Location center = player.getLocation().add(0, 0.5, 0);
        for (int i = 0; i < ringParticleCount; i++) {
            double angle = 2 * Math.PI * i / ringParticleCount;
            double x = center.getX() + ringRadius * Math.cos(angle);
            double z = center.getZ() + ringRadius * Math.sin(angle);
            Location loc = new Location(player.getWorld(), x, center.getY(), z);
            player.getWorld().spawnParticle(ringParticle, loc, 1, 0, 0, 0, 0);
        }
    }

    private void finish() {
        this.cancel();
        if (player.isOnline()) {
            player.setInvulnerable(false);
            player.setFlying(false);
            player.setAllowFlight(false);
        }
        pm.executeCommand(finalCommand);
    }
}