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
import org.bukkit.ChatColor;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.check.Check;
import ru.Fronzter.ShadeAc.data.PlayerData;

public class AlertManager {

    private final ShadeAc plugin;

    public AlertManager(ShadeAc plugin) {
        this.plugin = plugin;
    }

    public void sendAlert(PlayerData playerData, Check check, String debugInfo) {
        String messageFormat = plugin.getConfigManager().getAlertMessage();
        String permission = plugin.getConfigManager().getAlertPermission();

        String rawMessage = messageFormat
                .replace("%player%", playerData.getPlayer().getName())
                .replace("%check%", check.getName())
                .replace("%type%", check.getType())
                .replace("%vl%", String.valueOf(check.getViolations()))
                .replace("%debug%", debugInfo);

        final String finalMessage = ChatColor.translateAlternateColorCodes('&', rawMessage);

        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission(permission))
                .forEach(player -> player.sendMessage(finalMessage));
    }
}