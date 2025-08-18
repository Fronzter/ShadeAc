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
import ru.Fronzter.ShadeAc.config.PunishmentConfig;
import ru.Fronzter.ShadeAc.data.PlayerData;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AlertManager {

    private final Set<UUID> alertsToggled = new HashSet<>();

    public void handleViolation(Check check, String debugInfo) {
        PlayerData playerData = check.getPlayerData();
        ConfigManager configManager = ShadeAc.getInstance().getConfigManager();

        String message = ChatColor.translateAlternateColorCodes('&', configManager.getAlertMessage()
                .replace("%player%", playerData.getPlayer().getName())
                .replace("%check%", check.getName())
                .replace("%type%", check.getSubType())
                .replace("%vl%", String.valueOf((int) Math.ceil(check.getViolationLevel())))
                .replace("%debug%", debugInfo)
        );
        sendAlert(message, configManager.getAlertPermission());

        handlePunishment(check);
    }

    private void handlePunishment(Check check) {
        PlayerData playerData = check.getPlayerData();
        ConfigManager configManager = ShadeAc.getInstance().getConfigManager();
        PunishmentManager punishmentManager = ShadeAc.getInstance().getPunishmentManager();
        AnimationManager animationManager = ShadeAc.getInstance().getAnimationManager();

        String punishmentKey = configManager.getPunishmentKey(check);
        if (punishmentKey == null) return;

        PunishmentConfig punishment = punishmentManager.getPunishment(punishmentKey);
        if (punishment == null) return;

        if (check.getViolationLevel() >= punishment.getMaxVl() && !playerData.isPunished(check)) {
            playerData.setPunished(check, true);

            if (punishment.isAnimate() && animationManager.isEnabled()) {
                animationManager.playPunishmentAnimation(playerData, punishment.getReason());
            } else {
                for (String command : punishment.getCommands()) {
                    String formattedCommand = command
                            .replace("%player%", playerData.getPlayer().getName())
                            .replace("%reason%", punishment.getReason());

                    Bukkit.getScheduler().runTask(ShadeAc.getInstance(), () ->
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formattedCommand)
                    );
                }
            }
        }
    }

    private void sendAlert(String message, String permission) {
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission(permission) && alertsToggled.contains(p.getUniqueId()))
                .forEach(p -> p.sendMessage(message));
    }

    public boolean toggleAlerts(UUID uuid) {
        if (alertsToggled.contains(uuid)) {
            alertsToggled.remove(uuid);
            return false;
        } else {
            alertsToggled.add(uuid);
            return true;
        }
    }
}
