package ru.Fronzter.ShadeAc.manager;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */

import org.bukkit.configuration.file.FileConfiguration;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.mitigation.MitigationType;

public class ConfigManager {

    private final ShadeAc plugin;
    private FileConfiguration config;

    public ConfigManager(ShadeAc plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public String getAlertMessage() {
        return config.getString("alerts.message", "&cAlert message not found in config!"); // алерта нету в конфиге, я думаю и так понятно, крч, пон
    }

    public String getAlertPermission() {
        return config.getString("alerts.permission", "shadeac.alerts");
    }

    public boolean isCheckEnabled(String checkName, String type) {
        String path = String.format("checks.combat.%s.%s.enabled", checkName.toLowerCase(), type);
        return config.getBoolean(path, true);
    }

    public double getCheckValueDouble(String checkName, String type, String valuePath) {
        String path = String.format("checks.combat.%s.%s.%s", checkName.toLowerCase(), type, valuePath);
        return config.getDouble(path);
    }

    public boolean getCheckValueBoolean(String checkName, String type, String valuePath) {
        String path = String.format("checks.combat.%s.%s.%s", checkName.toLowerCase(), type, valuePath);
        return config.getBoolean(path, false);
    }

    private String getMitigationPath(String checkName, String type) {
        return String.format("checks.combat.%s.%s.mitigation.", checkName.toLowerCase(), type);
    }

    public boolean isMitigationEnabled(String checkName, String type) {
        return config.getBoolean(getMitigationPath(checkName, type) + "enabled", false);
    }

    public MitigationType getMitigationType(String checkName, String type) {
        String mode = config.getString(getMitigationPath(checkName, type) + "mode", "NONE");
        try {
            return MitigationType.valueOf(mode.toUpperCase());
        } catch (IllegalArgumentException e) {
            return MitigationType.NONE;
        }
    }

    public double getMitigationValue(String checkName, String type, String valuePath) {
        return config.getDouble(getMitigationPath(checkName, type) + valuePath, 0.0);
    }
}
