package ru.Fronzter.ShadeAc.nms;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface NMSHandler {

    int getPing(Player player);
    Object getBoundingBox(Entity entity);
}