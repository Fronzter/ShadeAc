package ru.Fronzter.ShadeAc.check;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import lombok.Getter;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.data.PlayerData;
import ru.Fronzter.ShadeAc.manager.PunishmentManager;
import ru.Fronzter.ShadeAc.mitigation.MitigationType;

@Getter
public abstract class Check {

    protected final PlayerData data;
    private final String name;
    private final String type;

    private int violations;
    private final boolean enabled;
    private final int maxViolations;

    // настройки митигации
    private final boolean mitigationEnabled;
    private final MitigationType mitigationType;

    public Check(PlayerData data, String name, String type) {
        this.data = data;
        this.name = name;
        this.type = type;

        ShadeAc ac = ShadeAc.getInstance();
        this.enabled = ac.getConfigManager().isCheckEnabled(name, type);
        this.maxViolations = ac.getPunishmentManager().getPunishVL(this);

        // настройки митигации при создании чека
        this.mitigationEnabled = ac.getConfigManager().isMitigationEnabled(name, type);
        this.mitigationType = ac.getConfigManager().getMitigationType(name, type);
    }

    protected void flag(String... debugInfo) {
        if (!enabled) {
            return;
        }

        this.violations++;
        String debug = String.join(", ", debugInfo);

        // алерт
        ShadeAc.getInstance().getAlertManager().sendAlert(data, this, debug);

        // флаг митигации если он врублен
        if (mitigationEnabled) {
            data.setNextMitigation(this.mitigationType);
        }

        // проверять не пора ли выдать наказание :>
        if (violations >= maxViolations && maxViolations != Integer.MAX_VALUE) {
            handlePunishment();
        }
    }

    private void handlePunishment() {
        PunishmentManager pm = ShadeAc.getInstance().getPunishmentManager();
        pm.executePunishment(data.getPlayer(), this);
        this.violations = 0;
    }
}