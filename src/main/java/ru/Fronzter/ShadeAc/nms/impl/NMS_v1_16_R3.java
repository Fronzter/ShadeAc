package ru.Fronzter.ShadeAc.nms.impl;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import ru.Fronzter.ShadeAc.nms.NMSHandler;

public class NMS_v1_16_R3 implements NMSHandler {

    @Override
    public int getPing(Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }

    @Override
    public Object getBoundingBox(Entity entity) {
        return ((CraftEntity) entity).getHandle().getBoundingBox();
    }
}