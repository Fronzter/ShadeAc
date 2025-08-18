package ru.Fronzter.ShadeAc.listener;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.manager.AlertManager;
import ru.Fronzter.ShadeAc.manager.AnimationManager;
import ru.Fronzter.ShadeAc.manager.PlayerDataManager;

public class PlayerConnectionListener implements Listener {

    private final PlayerDataManager playerDataManager = ShadeAc.getInstance().getPlayerDataManager();
    private final AlertManager alertManager = ShadeAc.getInstance().getAlertManager();
    private final AnimationManager animationManager = ShadeAc.getInstance().getAnimationManager();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playerDataManager.createData(player);

        if (player.hasPermission("shadeac.command.alerts")) {
            if (!alertManager.toggleAlerts(player.getUniqueId())) {
                alertManager.toggleAlerts(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        animationManager.forceUnfreeze(player);
        playerDataManager.removeData(player);

        if (alertManager.toggleAlerts(player.getUniqueId())) {
            alertManager.toggleAlerts(player.getUniqueId());
        }
    }
}
