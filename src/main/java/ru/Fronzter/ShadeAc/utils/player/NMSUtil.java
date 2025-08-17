package ru.Fronzter.ShadeAc.utils.player;
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
import ru.Fronzter.ShadeAc.nms.NMSManager;

public class NMSUtil {

    private NMSUtil() {
    }

    public static int getPing(Player player) {
        return NMSManager.getNmsHandler().getPing(player);
    }

    public static Object getBoundingBox(Entity entity) {
        return NMSManager.getNmsHandler().getBoundingBox(entity);
    }
}
