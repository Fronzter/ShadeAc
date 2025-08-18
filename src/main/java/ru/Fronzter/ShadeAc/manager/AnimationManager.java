package ru.Fronzter.ShadeAc.manager;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import lombok.Getter;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.animation.PunishmentAnimationTask;
import ru.Fronzter.ShadeAc.config.AnimationConfig;
import ru.Fronzter.ShadeAc.data.PlayerData;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AnimationManager {

    private final ShadeAc plugin;
    @Getter private AnimationConfig animationConfig;

    @Getter
    private final Set<UUID> frozenPlayers = new HashSet<>();

    public AnimationManager(ShadeAc plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        File file = new File(plugin.getDataFolder(), "punish.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("animation-settings");

        if (section == null) {
            this.animationConfig = new AnimationConfig(false, "", 0, 0, Particle.BARRIER, 0, 0, 0, false, 0, false, Particle.BARRIER, Sound.UI_BUTTON_CLICK, 0, 0);
            return;
        }

        boolean enabled = section.getBoolean("enabled", false);
        String command = section.getString("post-animation-command", "kick %player% Animation finished");
        int duration = section.getInt("duration", 100);
        double liftSpeed = section.getDouble("lift-speed", 0.05);

        ConfigurationSection spiral = section.getConfigurationSection("spiral");
        Particle particle = Particle.valueOf(spiral.getString("particle-type", "REDSTONE").toUpperCase());
        int count = spiral.getInt("particle-count", 3);
        double radius = spiral.getDouble("radius", 1.5);
        double rotSpeed = Math.toRadians(spiral.getDouble("rotation-speed", 25.0) / 20.0);
        boolean colorShift = spiral.getBoolean("color-shifting", true);
        float colorShiftSpeed = (float) spiral.getDouble("color-shift-speed", 0.02);

        ConfigurationSection explosionSection = section.getConfigurationSection("explosion-effect");
        boolean explosionEnabled = explosionSection.getBoolean("enabled", true);
        Particle explosionParticle = Particle.valueOf(explosionSection.getString("particle-type", "EXPLOSION_HUGE").toUpperCase());
        Sound explosionSound = Sound.valueOf(explosionSection.getString("sound", "ENTITY_GENERIC_EXPLODE").toUpperCase());
        float explosionVolume = (float) explosionSection.getDouble("volume", 1.0);
        float explosionPitch = (float) explosionSection.getDouble("pitch", 1.2);

        this.animationConfig = new AnimationConfig(enabled, command, duration, liftSpeed, particle, count, radius, rotSpeed, colorShift, colorShiftSpeed, explosionEnabled, explosionParticle, explosionSound, explosionVolume, explosionPitch);
    }

    public boolean isEnabled() {
        return animationConfig.isEnabled();
    }

    public void playPunishmentAnimation(PlayerData playerData, String reason) {
        if (!isEnabled() || animationConfig.getDuration() <= 0) return;
        new PunishmentAnimationTask(playerData.getPlayer(), animationConfig, reason).runTaskTimer(plugin, 0L, 1L);
    }

    public void freezePlayer(UUID uuid) {
        frozenPlayers.add(uuid);
    }

    public void unfreezePlayer(UUID uuid) {
        frozenPlayers.remove(uuid);
    }

    public boolean isPlayerFrozen(UUID uuid) {
        return frozenPlayers.contains(uuid);
    }

    public void forceUnfreeze(Player player) {
        UUID uuid = player.getUniqueId();
        if (isPlayerFrozen(uuid)) {
            unfreezePlayer(uuid);
            player.setInvulnerable(false);
            player.setGravity(true);
        }
    }
}
