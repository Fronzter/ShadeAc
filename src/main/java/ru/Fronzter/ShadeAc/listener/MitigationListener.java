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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.data.PlayerData;
import ru.Fronzter.ShadeAc.mitigation.MitigationType;

public class MitigationListener implements Listener {

    private final ShadeAc plugin;

    public MitigationListener(ShadeAc plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player damager = (Player) event.getDamager();
        PlayerData data = plugin.getPlayerManager().getPlayerData(damager);
        if (data == null || data.getNextMitigation() == MitigationType.NONE) {
            return;
        }

        MitigationType type = data.getNextMitigation();

        switch (type) {
            case CANCEL_DAMAGE:
                event.setCancelled(true);
                break;
            case REDUCE_DAMAGE:
                double currentDamage = event.getDamage();
                event.setDamage(currentDamage * 0.5);
                break;
        }

        data.setNextMitigation(MitigationType.NONE);
    }
}