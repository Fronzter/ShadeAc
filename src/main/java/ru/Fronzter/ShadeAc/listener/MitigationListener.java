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

public class MitigationListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player damager = (Player) event.getDamager();
        PlayerData playerData = ShadeAc.getInstance().getPlayerDataManager().getData(damager);

        if (playerData == null || !playerData.isMitigated()) {
            return;
        }

        switch (playerData.getMitigationType()) {
            case CANCEL_HITS:
                event.setCancelled(true);
                break;
            case REDUCE_DAMAGE:
                double newDamage = event.getDamage() * playerData.getDamageReductionFactor();
                event.setDamage(newDamage);
                break;
        }
    }
}