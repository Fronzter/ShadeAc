package ru.Fronzter.ShadeAc.animation;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.config.AnimationConfig;
import ru.Fronzter.ShadeAc.manager.AnimationManager;

public class PunishmentAnimationTask extends BukkitRunnable {

    private final Player player;
    private final AnimationConfig config;
    private final AnimationManager animationManager;
    private final String finalCommand;

    private final boolean hadGravity;
    private final boolean wasInvulnerable;

    private int ticksElapsed = 0;
    private float currentHue = 0.0f;

    public PunishmentAnimationTask(Player player, AnimationConfig config, String reason) {
        this.player = player;
        this.config = config;
        this.animationManager = ShadeAc.getInstance().getAnimationManager();
        this.finalCommand = config.getPostAnimationCommand()
                .replace("%player%", player.getName())
                .replace("%reason%", reason);

        this.hadGravity = player.hasGravity();
        this.wasInvulnerable = player.isInvulnerable();

        preparePlayer();
    }

    private void preparePlayer() {
        animationManager.freezePlayer(player.getUniqueId());
        player.setInvulnerable(true);
        player.setGravity(false);
    }

    private void cleanupPlayer() {
        if (player.isOnline()) {
            animationManager.unfreezePlayer(player.getUniqueId());
            player.setInvulnerable(wasInvulnerable);
            player.setGravity(hadGravity);
        }
    }

    @Override
    public void run() {
        if (ticksElapsed >= config.getDuration() || !player.isOnline()) {
            this.cancel();
            return;
        }

        Location newLoc = player.getLocation().add(0, config.getLiftSpeed(), 0);
        player.teleport(newLoc);

        Location playerLoc = player.getLocation();
        double angle = ticksElapsed * config.getRotationSpeed();

        for (int i = 0; i < config.getParticleCount(); i++) {
            double x = playerLoc.getX() + config.getRadius() * Math.cos(angle);
            double z = playerLoc.getZ() + config.getRadius() * Math.sin(angle);
            double y = playerLoc.getY() + 1;

            Object particleData = null;
            if (config.getParticleType() == Particle.REDSTONE) {
                java.awt.Color awtColor = java.awt.Color.getHSBColor(currentHue, 1f, 1f);
                Color bukkitColor = Color.fromRGB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
                particleData = new Particle.DustOptions(bukkitColor, 1.0f);
            }

            player.getWorld().spawnParticle(config.getParticleType(), x, y, z, 0, 0, 0, 0, particleData);

            double angle2 = angle + Math.PI;
            double x2 = playerLoc.getX() + config.getRadius() * Math.cos(angle2);
            double z2 = playerLoc.getZ() + config.getRadius() * Math.sin(angle2);
            player.getWorld().spawnParticle(config.getParticleType(), x2, y, z2, 0, 0, 0, 0, particleData);
        }

        if (config.isColorShifting()) {
            currentHue += config.getColorShiftSpeed();
            if (currentHue > 1.0f) currentHue -= 1.0f;
        }

        ticksElapsed++;
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();

        if (player.isOnline() && config.isExplosionEnabled()) {
            Location loc = player.getLocation().add(0, 1, 0);
            player.getWorld().spawnParticle(config.getExplosionParticle(), loc, 1);
            player.getWorld().playSound(loc, config.getExplosionSound(), config.getExplosionVolume(), config.getExplosionPitch());
        }

        Bukkit.getScheduler().runTaskLater(ShadeAc.getInstance(), () -> {
            cleanupPlayer();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
        }, 5L);
    }
}
