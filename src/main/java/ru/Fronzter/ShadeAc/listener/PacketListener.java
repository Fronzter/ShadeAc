package ru.Fronzter.ShadeAc.listener;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import org.bukkit.entity.Player;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.check.impl.combat.autoclicker.AutoclickerA;
import ru.Fronzter.ShadeAc.check.impl.combat.killaura.KillauraA;
import ru.Fronzter.ShadeAc.check.impl.combat.killaura.KillauraB;
import ru.Fronzter.ShadeAc.check.impl.combat.ml.KillauraHandmadeML;
import ru.Fronzter.ShadeAc.data.PlayerData;
import ru.Fronzter.ShadeAc.utils.math.MathUtil;
import ru.Fronzter.ShadeAc.utils.time.TimeUtil;
import java.util.Deque;

public class PacketListener extends PacketListenerAbstract {
    private static final int ROTATION_HISTORY_SIZE = 10;
    private final ShadeAc plugin;

    public PacketListener(ShadeAc plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {

        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getPlayer();

        PlayerData data = plugin.getPlayerManager().getPlayerData(player);
        if (data == null) return;

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);

            if (flying.hasRotationChanged()) {
                float yaw = flying.getLocation().getYaw();
                float pitch = flying.getLocation().getPitch();
                float deltaYaw = MathUtil.wrapAngleTo180(yaw - data.getLastYaw());
                float deltaPitch = pitch - data.getLastPitch();

                addToHistory(data.getYawHistory(), yaw);
                addToHistory(data.getPitchHistory(), pitch);
                addToHistory(data.getDeltaYawHistory(), deltaYaw);
                addToHistory(data.getDeltaPitchHistory(), deltaPitch);
                data.setLastYaw(yaw);
                data.setLastPitch(pitch);

                plugin.getDataCollectionManager().onRotation(player, deltaYaw, deltaPitch);

                data.getChecks().stream()
                        .filter(c -> c instanceof KillauraHandmadeML)
                        .forEach(c -> ((KillauraHandmadeML) c).handleRotation(deltaYaw, deltaPitch));
            }
        }

        else if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity useEntity = new WrapperPlayClientInteractEntity(event);

            if (useEntity.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                data.getCombatProcessor().onAttack();

                long now = TimeUtil.getMillis();
                if (data.getLastAttackTime() > 0) {
                    data.getClickTimings().addDataPoint(now - data.getLastAttackTime());
                }
                data.setLastAttackTime(now);

                data.getChecks().forEach(check -> {

                    if (check instanceof KillauraA) ((KillauraA) check).handleUseEntity(useEntity);
                    else if (check instanceof KillauraB) ((KillauraB) check).handleUseEntity(useEntity);
                    else if (check instanceof AutoclickerA) ((AutoclickerA) check).handleAttack();
                    else if (check instanceof KillauraHandmadeML) ((KillauraHandmadeML) check).handleAttack();
                });
            }
        }
    }

    private <T> void addToHistory(Deque<T> deque, T value) {
        deque.addLast(value);
        if (deque.size() > ROTATION_HISTORY_SIZE) deque.removeFirst();
    }
}