package ru.Fronzter.ShadeAc.manager;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.check.Check;
import ru.Fronzter.ShadeAc.config.MitigationConfig;
import ru.Fronzter.ShadeAc.data.PlayerData;

public class MitigationManager {

    public void applyMitigation(PlayerData playerData, Check check) {

        MitigationConfig config = ShadeAc.getInstance().getConfigManager().getMitigationConfig(check);

        if (config == null || !config.isEnabled() || config.getMode() == MitigationConfig.MitigationType.NONE) {
            return;
        }

        long expiryTime = System.currentTimeMillis() + (config.getDurationTicks() * 50L);
        playerData.setMitigation(config.getMode(), expiryTime, config.getReduceAmount());
    }
}