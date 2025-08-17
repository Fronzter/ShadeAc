package ru.Fronzter.ShadeAc.nms.impl;
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
import ru.Fronzter.ShadeAc.nms.NMSHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NMS_v1_17_PLUS implements NMSHandler {

    private Method getHandlePlayer, getHandleEntity;
    private Field pingField;
    private Method getBoundingBoxMethod;

    public NMS_v1_17_PLUS() {
        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer");
            Class<?> craftEntityClass = Class.forName("org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity");
            getHandlePlayer = craftPlayerClass.getMethod("getHandle");
            getHandleEntity = craftEntityClass.getMethod("getHandle");

            Class<?> entityPlayerClass = Class.forName("net.minecraft.server.level.EntityPlayer");
            pingField = entityPlayerClass.getField("e");

            Class<?> entityClass = Class.forName("net.minecraft.world.entity.Entity");
            getBoundingBoxMethod = entityClass.getMethod("getBoundingBox");

        } catch (Exception ignored) {}
    }

    @Override
    public int getPing(Player player) {
        return player.getPing();
    }

    @Override
    public Object getBoundingBox(Entity entity) {
        try {
            Object nmsEntity = getHandleEntity.invoke(entity);
            return getBoundingBoxMethod.invoke(nmsEntity);
        } catch (Exception e) {
            return null;
        }
    }
}
