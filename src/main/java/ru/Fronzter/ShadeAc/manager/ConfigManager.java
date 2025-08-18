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
import org.bukkit.configuration.ConfigurationSection;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.check.Check;
import ru.Fronzter.ShadeAc.config.CheckConfig;
import ru.Fronzter.ShadeAc.config.MitigationConfig;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final ShadeAc plugin;
    private final Map<String, CheckConfig> checkConfigCache = new HashMap<>();
    private final Map<String, MitigationConfig> mitigationConfigCache = new HashMap<>();

    @Getter private String alertPermission;
    @Getter private String alertMessage;

    public ConfigManager(ShadeAc plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        load();
    }

    public void load() {
        plugin.reloadConfig();
        checkConfigCache.clear();
        mitigationConfigCache.clear();
        this.alertPermission = plugin.getConfig().getString("alerts.permission", "shadeac.alerts");
        this.alertMessage = plugin.getConfig().getString("alerts.message", "&8[&cError&8] &fAlert message not configured!");
    }

    private String getCheckPath(Check check) {
        return "checks."
                + check.getCategory().name().toLowerCase() + "."
                + check.getName().toLowerCase() + "."
                + check.getSubType();
    }

    public ConfigurationSection getCheckSection(Check check) {
        return plugin.getConfig().getConfigurationSection(getCheckPath(check));
    }

    public CheckConfig getCheckConfig(Check check) {
        return checkConfigCache.computeIfAbsent(check.getFullName(), fullName -> {
            ConfigurationSection section = getCheckSection(check);
            boolean enabled = (section != null) && section.getBoolean("enabled", true);
            return new CheckConfig(enabled);
        });
    }

    public MitigationConfig getMitigationConfig(Check check) {
        return mitigationConfigCache.computeIfAbsent(check.getFullName(), fullName -> {
            ConfigurationSection section = getCheckSection(check);
            if (section == null || !section.isConfigurationSection("mitigation")) {
                return new MitigationConfig(false, MitigationConfig.MitigationType.NONE, 0, 0);
            }

            ConfigurationSection mitSection = section.getConfigurationSection("mitigation");
            boolean enabled = mitSection.getBoolean("enabled", false);
            MitigationConfig.MitigationType mode;
            try {
                mode = MitigationConfig.MitigationType.valueOf(mitSection.getString("mode", "NONE").toUpperCase());
            } catch (IllegalArgumentException e) {
                mode = MitigationConfig.MitigationType.NONE;
            }

            int durationTicks = mitSection.getInt("duration-seconds", 5) * 20;
            double reduceAmount = mitSection.getDouble("reduce-amount", 0.5);

            return new MitigationConfig(enabled, mode, durationTicks, reduceAmount);
        });
    }

    public String getPunishmentKey(Check check) {
        return plugin.getConfig().getString(getCheckPath(check) + ".punishment-key");
    }
}
