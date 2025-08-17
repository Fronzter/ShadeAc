package ru.Fronzter.ShadeAc.nms;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */

import org.bukkit.Bukkit;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.nms.impl.NMS_v1_16_R3;
import ru.Fronzter.ShadeAc.nms.impl.NMS_v1_17_PLUS;

public class NMSManager {

    private static NMSHandler nmsHandler;
    private final ShadeAc plugin;

    public NMSManager(ShadeAc plugin) {
        this.plugin = plugin;
        setupNMSHandler();
    }

    private void setupNMSHandler() {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        switch (version) {
            case "v1_16_R3":
                nmsHandler = new NMS_v1_16_R3();
                break;
            case "v1_17_R1":
            case "v1_18_R1":
            case "v1_18_R2":
            case "v1_19_R1":
            case "v1_19_R2":
            case "v1_19_R3":
            case "v1_20_R1":
            case "v1_20_R2":
            case "v1_20_R3":
            case "v1_20_R4":
            case "v1_21_R1":
                nmsHandler = new NMS_v1_17_PLUS();
                break;
            default:
                Bukkit.getPluginManager().disablePlugin(plugin);
                break;
        }
    }

    public static NMSHandler getNmsHandler() {
        return nmsHandler;
    }
}
