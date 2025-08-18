package ru.Fronzter.ShadeAc.check;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import com.comphenix.protocol.events.PacketEvent;
import lombok.Getter;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.config.CheckConfig;
import ru.Fronzter.ShadeAc.data.PlayerData;
import ru.Fronzter.ShadeAc.data.update.RotationUpdate;

@Getter
public abstract class Check {

    protected final PlayerData playerData;
    private final CheckInfo info;
    private final CheckConfig config;
    private double violationLevel;
    private long lastFlagTime;

    public Check(PlayerData playerData) {
        this.playerData = playerData;
        this.info = getClass().getAnnotation(CheckInfo.class);
        if (info == null) {
            throw new IllegalStateException("Check " + getClass().getSimpleName() + " is missing @CheckInfo annotation!");
        }

        this.config = ShadeAc.getInstance().getConfigManager().getCheckConfig(this);
        this.lastFlagTime = System.currentTimeMillis();
    }

    public void onPacketReceiving(PacketEvent event) { }
    public void onRotation(RotationUpdate update) { }

    public void tick() {
        long timeSinceLastFlag = System.currentTimeMillis() - lastFlagTime;
        if (timeSinceLastFlag > 2000) {
            decay();
        }
    }

    protected void flag(String debugInfo) {
        this.violationLevel++;
        this.lastFlagTime = System.currentTimeMillis();
        ShadeAc.getInstance().getMitigationManager().applyMitigation(playerData, this);
        ShadeAc.getInstance().getAlertManager().handleViolation(this, debugInfo);
    }

    protected void decay() {
        this.violationLevel = Math.max(0.0, violationLevel - 0.25);
    }

    public String getName() { return info.name(); }
    public String getSubType() { return info.subType(); }
    public CheckCategory getCategory() { return info.category(); }
    public String getFullName() { return info.name() + " (" + info.subType() + ")"; }
}
