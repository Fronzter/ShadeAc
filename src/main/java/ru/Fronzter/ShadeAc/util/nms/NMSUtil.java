package ru.Fronzter.ShadeAc.util.nms;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@UtilityClass
public class NMSUtil {

    private Method getHandleMethod;
    private Field onGroundField;
    private Field frictionFactorField;

    static {
        try {
            String version = org.bukkit.Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
            getHandleMethod = craftPlayerClass.getMethod("getHandle");

            Class<?> entityPlayerClass = Class.forName("net.minecraft.server." + version + ".EntityPlayer");
            onGroundField = entityPlayerClass.getDeclaredField("onGround");
            onGroundField.setAccessible(true);

            Class<?> blockClass = Class.forName("net.minecraft.server." + version + ".Block");
            frictionFactorField = blockClass.getDeclaredField("frictionFactor");
            frictionFactorField.setAccessible(true);
        } catch (Exception ignored) {
        }
    }

    public static boolean isServerOnGround(Player player) {
        if (getHandleMethod == null || onGroundField == null) return false;
        try {
            Object nmsPlayer = getHandleMethod.invoke(player);
            return onGroundField.getBoolean(nmsPlayer);
        } catch (Exception e) {
            return false;
        }
    }

    public static float getFriction(Player player) {
        if (getHandleMethod == null || frictionFactorField == null) return 0.6f;
        try {
            Object nmsPlayer = getHandleMethod.invoke(player);
            String version = org.bukkit.Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

            Object blockPosition = Class.forName("net.minecraft.server." + version + ".BlockPosition")
                    .getConstructor(double.class, double.class, double.class)
                    .newInstance(player.getLocation().getX(), player.getBoundingBox().getMinY() - 0.500001D, player.getLocation().getZ());

            Object nmsWorld = nmsPlayer.getClass().getField("world").get(nmsPlayer);
            Object iBlockData = nmsWorld.getClass().getMethod("getType", blockPosition.getClass()).invoke(nmsWorld, blockPosition);
            Object block = iBlockData.getClass().getMethod("getBlock").invoke(iBlockData);

            return frictionFactorField.getFloat(block);
        } catch (Exception e) {
            return 0.6f;
        }
    }
}
