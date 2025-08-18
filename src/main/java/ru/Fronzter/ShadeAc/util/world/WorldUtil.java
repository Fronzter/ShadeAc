package ru.Fronzter.ShadeAc.util.world;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.EnumSet;

@UtilityClass
public class WorldUtil {

    private static final EnumSet<Material> LIQUID_MATERIALS = EnumSet.of(Material.WATER, Material.LAVA);
    private static final EnumSet<Material> CLIMBABLE_MATERIALS = EnumSet.of(Material.LADDER, Material.VINE);
    private static final EnumSet<Material> SLIME_LIKE_MATERIALS = EnumSet.of(Material.SLIME_BLOCK, Material.HONEY_BLOCK);

    public static boolean isInLiquid(Player player) {
        return LIQUID_MATERIALS.contains(player.getLocation().getBlock().getType()) ||
                LIQUID_MATERIALS.contains(player.getEyeLocation().getBlock().getType());
    }

    public static boolean isOnClimbable(Player player) {
        for (double yOffset = 0; yOffset <= 1.0; yOffset += 1.0) {
            Block block = player.getLocation().clone().add(0, yOffset, 0).getBlock();
            if (CLIMBABLE_MATERIALS.contains(block.getType())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInWeb(Player player) {
        return player.getLocation().getBlock().getType() == Material.COBWEB;
    }

    public static boolean isNearSlime(Player player) {
        Location location = player.getLocation();
        for (double x = -0.3; x <= 0.3; x += 0.3) {
            for (double z = -0.3; z <= 0.3; z += 0.3) {
                Block block = location.clone().add(x, -0.501, z).getBlock();
                if (SLIME_LIKE_MATERIALS.contains(block.getType())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Block getBlockSafety(Location loc) {
        if (loc.getWorld() == null) return null;

        int chunkX = loc.getBlockX() >> 4;
        int chunkZ = loc.getBlockZ() >> 4;

        if (loc.getWorld().isChunkLoaded(chunkX, chunkZ)) {
            return loc.getBlock();
        }
        return null;
    }

    public static boolean isUnderBlock(Player player) {
        Location headTopLocation = player.getLocation().clone().add(0, 2.0, 0);
        Block blockAbove = getBlockSafety(headTopLocation);
        return blockAbove != null && blockAbove.getType().isSolid();
    }
}
