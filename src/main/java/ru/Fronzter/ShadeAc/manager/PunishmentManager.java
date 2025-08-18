package ru.Fronzter.ShadeAc.manager;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.config.PunishmentConfig;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PunishmentManager {

    private final ShadeAc plugin;
    private final Map<String, PunishmentConfig> punishmentCache = new HashMap<>();

    public PunishmentManager(ShadeAc plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        punishmentCache.clear();

        File file = new File(plugin.getDataFolder(), "punish.yml");
        if (!file.exists()) plugin.saveResource("punish.yml", false);

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("punishments");

        if (section == null) return;

        for (String key : section.getKeys(false)) {
            boolean animate = section.getBoolean(key + ".animate", false);
            int maxVl = section.getInt(key + ".max-vl", 20);
            String reason = section.getString(key + ".reason", "Unfair Advantage");
            List<String> commands = section.getStringList(key + ".commands");

            PunishmentConfig punishment = new PunishmentConfig(animate, maxVl, reason, commands);
            punishmentCache.put(key.toLowerCase(), punishment);
        }
    }

    public PunishmentConfig getPunishment(String key) {
        if (key == null || key.isEmpty()) return null;
        return punishmentCache.get(key.toLowerCase());
    }
}
