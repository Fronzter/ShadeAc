package ru.Fronzter.ShadeAc.listener;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.manager.AnimationManager;

public class PlayerMoveListener implements Listener {

    private final AnimationManager animationManager = ShadeAc.getInstance().getAnimationManager();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        if (animationManager.isPlayerFrozen(event.getPlayer().getUniqueId())) {

            Location from = event.getFrom();
            Location to = event.getTo();

            if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
                event.setTo(from);
            }
        }
    }
}