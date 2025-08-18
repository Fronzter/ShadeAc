package ru.Fronzter.ShadeAc.check.impl.combat.killaura;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.check.Check;
import ru.Fronzter.ShadeAc.check.CheckCategory;
import ru.Fronzter.ShadeAc.check.CheckInfo;
import ru.Fronzter.ShadeAc.data.PlayerData;
import ru.Fronzter.ShadeAc.data.update.RotationUpdate;
import ru.Fronzter.ShadeAc.util.anticheat.AimUtil;

@CheckInfo(
        name = "Killaura",
        subType = "A",
        category = CheckCategory.COMBAT,
        description = "Detects impossibly fast and accurate aim snaps onto targets."
)
public class KillauraA extends Check {

    private RotationUpdate previousUpdate, currentUpdate;

    private final double minCorrectionAngle;
    private final double maxFinalAngle;
    private final double minAcceleration;

    public KillauraA(PlayerData playerData) {
        super(playerData);
        ConfigurationSection section = ShadeAc.getInstance().getConfigManager().getCheckSection(this);
        if (section != null) {
            this.minCorrectionAngle = section.getDouble("min-correction-angle", 5.0);
            this.maxFinalAngle = section.getDouble("max-final-angle", 0.8);
            this.minAcceleration = section.getDouble("min-acceleration", 10.0);
        } else {
            this.minCorrectionAngle = 5.0;
            this.maxFinalAngle = 0.8;
            this.minAcceleration = 10.0;
        }
    }

    @Override
    public void onRotation(RotationUpdate update) {
        this.previousUpdate = this.currentUpdate;
        this.currentUpdate = update;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.USE_ENTITY) return;
        if (event.getPacket().getEntityUseActions().read(0) != EnumWrappers.EntityUseAction.ATTACK) return;
        if (previousUpdate == null || currentUpdate == null) return;

        Entity target = event.getPacket().getEntityModifier(playerData.getPlayer().getWorld()).read(0);
        if (target == null) return;

        float angleBefore = AimUtil.getAngleToEntity(playerData.getPlayer(), target);
        float angleBeforeSnap = AimUtil.getAngleDifference(angleBefore, currentUpdate.getDeltaYaw());
        float angleAfterSnap = angleBefore;
        float acceleration = currentUpdate.getAbsDeltaYaw() - previousUpdate.getAbsDeltaYaw();

        boolean isHighAcceleration = acceleration > minAcceleration;
        boolean wasCorrection = angleBeforeSnap > minCorrectionAngle;
        boolean isPrecise = angleAfterSnap < maxFinalAngle;

        if (isHighAcceleration && wasCorrection && isPrecise) {
            flag(String.format("before:%.2f after:%.2f accel:%.2f",
                    angleBeforeSnap, angleAfterSnap, acceleration));
        }
    }
}
