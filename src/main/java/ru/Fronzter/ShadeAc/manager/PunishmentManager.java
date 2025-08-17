package ru.Fronzter.ShadeAc.manager;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.animation.PunishmentAnimation;
import ru.Fronzter.ShadeAc.check.Check;

import java.io.File;
import java.util.List;

public class PunishmentManager {

    private final ShadeAc plugin;
    private FileConfiguration punishmentConfig;

    public PunishmentManager(ShadeAc plugin) {
        this.plugin = plugin;
        loadPunishments();
    }

    private void loadPunishments() {
        File configFile = new File(plugin.getDataFolder(), "punish.yml");
        if (!configFile.exists()) {
            plugin.saveResource("punish.yml", false);
        }
        this.punishmentConfig = YamlConfiguration.loadConfiguration(configFile);
    }

    public void executePunishment(Player player, Check check) {
        String path = getCheckPath(check);

        if (punishmentConfig.getBoolean("animated-punishment.enabled") && punishmentConfig.getBoolean(path + ".animate")) {
            String reason = punishmentConfig.getString(path + ".reason", "Cheating");
            String commandTemplate = getAnimatedPunishmentCommand();

            String finalCommand = commandTemplate
                    .replace("%player%", player.getName())
                    .replace("%reason%", reason);

            new PunishmentAnimation(plugin, player, finalCommand).runTaskTimer(plugin, 0L, 1L);
        } else {
            List<String> commands = punishmentConfig.getStringList(path + ".commands");
            if (commands.isEmpty()) return;

            commands.forEach(cmd -> executeCommand(cmd.replace("%player%", player.getName())));
        }
    }

    public void executeCommand(String command) {
        Bukkit.getScheduler().runTask(plugin, () ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }

    private String getCheckPath(Check check) {
        return String.format("checks.%s.%s", check.getName().toLowerCase(), check.getType());
    }

    public int getPunishVL(Check check) {
        String path = getCheckPath(check) + ".max-vl";
        return punishmentConfig.getInt(path, Integer.MAX_VALUE);
    }

    public String getAnimatedPunishmentCommand() {
        return punishmentConfig.getString("animated-punishment.command", "kick %player% %reason%");
    }
    public String getAnimationStyle() {
        return punishmentConfig.getString("animated-punishment.animation.style", "DOUBLE_HELIX").toUpperCase();
    }
    public long getAnimationDuration() {
        return punishmentConfig.getLong("animated-punishment.animation.duration", 100L);
    }
    public double getAnimationLiftSpeed() {
        return punishmentConfig.getDouble("animated-punishment.animation.lift-speed", 0.05);
    }
    public Particle getSpiralParticle() {
        try {
            return Particle.valueOf(punishmentConfig.getString("animated-punishment.animation.spiral.particle", "REDSTONE").toUpperCase());
        } catch (Exception e) {
            return Particle.REDSTONE;
        }
    }
    public boolean isSpiralColorShifting() {
        return punishmentConfig.getBoolean("animated-punishment.animation.spiral.color-shifting", true);
    }
    public double getColorShiftSpeed() {
        return punishmentConfig.getDouble("animated-punishment.animation.spiral.color-shift-speed", 0.01);
    }
    public int getSpiralParticleCount() {
        return punishmentConfig.getInt("animated-punishment.animation.spiral.particle-count", 3);
    }
    public double getSpiralRadius() {
        return punishmentConfig.getDouble("animated-punishment.animation.spiral.radius", 1.5);
    }
    public double getSpiralRotationSpeed() {
        return punishmentConfig.getDouble("animated-punishment.animation.spiral.rotation-speed", 25.0);
    }

    public boolean isSpiralVerticalMovementEnabled() {
        return punishmentConfig.getBoolean("animated-punishment.animation.spiral.vertical-movement-enabled", true);
    }
    public double getSpiralVerticalAmplitude() {
        return punishmentConfig.getDouble("animated-punishment.animation.spiral.vertical-amplitude", 1.0);
    }
    public double getSpiralVerticalSpeed() {
        return punishmentConfig.getDouble("animated-punishment.animation.spiral.vertical-speed", 1.0);
    }

    public boolean isRingsEnabled() {
        return punishmentConfig.getBoolean("animated-punishment.animation.rings.enabled", true);
    }
    public Particle getRingParticle() {
        try {
            return Particle.valueOf(punishmentConfig.getString("animated-punishment.animation.rings.particle", "CRIT_MAGIC").toUpperCase());
        } catch (Exception e) {
            return Particle.CRIT_MAGIC;
        }
    }
    public int getRingFrequency() {
        return punishmentConfig.getInt("animated-punishment.animation.rings.frequency", 20);
    }
    public int getRingParticleCount() {
        return punishmentConfig.getInt("animated-punishment.animation.rings.particle-count", 50);
    }
    public double getRingRadius() {
        return punishmentConfig.getDouble("animated-punishment.animation.rings.radius", 2.0);
    }
}
