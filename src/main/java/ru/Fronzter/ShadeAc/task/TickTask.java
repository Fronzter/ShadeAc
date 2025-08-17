package ru.Fronzter.ShadeAc.task;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.data.PlayerData;

public class TickTask extends BukkitRunnable {

    private final ShadeAc plugin;

    public TickTask(ShadeAc plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // по всем игрокам онлайн и вызываем их обработчик тика
        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerData data = plugin.getPlayerManager().getPlayerData(player);
            if (data != null) {
                data.getCombatProcessor().onTick();
            }
        });
    }
}