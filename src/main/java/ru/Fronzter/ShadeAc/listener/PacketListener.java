package ru.Fronzter.ShadeAc.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
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

public class PacketListener {
    private static final int ROTATION_HISTORY_SIZE = 10;

    public PacketListener(ShadeAc plugin) {
        registerPacketListener(plugin);
    }

    private void registerPacketListener(ShadeAc plugin) {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(plugin,
                        PacketType.Play.Client.USE_ENTITY,
                        PacketType.Play.Client.POSITION,
                        PacketType.Play.Client.LOOK,
                        PacketType.Play.Client.POSITION_LOOK) {

                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        Player player = event.getPlayer();
                        if (player == null) return;
                        PlayerData data = ShadeAc.getInstance().getPlayerManager().getPlayerData(player);
                        if (data == null) return;

                        PacketType type = event.getPacketType();

                        if (type == PacketType.Play.Client.POSITION_LOOK || type == PacketType.Play.Client.LOOK) {
                            float yaw = event.getPacket().getFloat().read(0);
                            float pitch = event.getPacket().getFloat().read(1);
                            float deltaYaw = MathUtil.wrapAngleTo180(yaw - data.getLastYaw());
                            float deltaPitch = pitch - data.getLastPitch();

                            addToHistory(data.getYawHistory(), yaw);
                            addToHistory(data.getPitchHistory(), pitch);
                            addToHistory(data.getDeltaYawHistory(), deltaYaw);
                            addToHistory(data.getDeltaPitchHistory(), deltaPitch);
                            data.setLastYaw(yaw);
                            data.setLastPitch(pitch);

                            ShadeAc.getInstance().getDataCollectionManager().onRotation(player, deltaYaw, deltaPitch);

                            data.getChecks().stream()
                                    .filter(c -> c instanceof KillauraHandmadeML)
                                    .forEach(c -> ((KillauraHandmadeML) c).handleRotation(deltaYaw, deltaPitch));
                        }

                        if (type == PacketType.Play.Client.USE_ENTITY) {
                            if (event.getPacket().getEntityUseActions().read(0) == EnumWrappers.EntityUseAction.ATTACK) {

                                data.getCombatProcessor().onAttack();

                                long now = TimeUtil.getMillis();
                                if (data.getLastAttackTime() > 0) {
                                    data.getClickTimings().addDataPoint(now - data.getLastAttackTime());
                                }
                                data.setLastAttackTime(now);

                                data.getChecks().forEach(check -> {
                                    if (check instanceof KillauraA) ((KillauraA) check).handleUseEntity(event.getPacket());
                                    else if (check instanceof KillauraB) ((KillauraB) check).handleUseEntity(event.getPacket());
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
        );
    }
}