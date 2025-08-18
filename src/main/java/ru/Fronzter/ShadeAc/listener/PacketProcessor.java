package ru.Fronzter.ShadeAc.listener;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.GamePhase;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.check.Check;
import ru.Fronzter.ShadeAc.data.PlayerData;
import ru.Fronzter.ShadeAc.data.update.RotationUpdate;

import static com.comphenix.protocol.events.ListenerPriority.NORMAL;

public class PacketProcessor implements PacketListener {

    private final ShadeAc plugin = ShadeAc.getInstance();

    public void register() {
        plugin.getProtocolManager().addPacketListener(
                new PacketAdapter(plugin,
                        PacketType.Play.Client.USE_ENTITY,
                        PacketType.Play.Client.FLYING,
                        PacketType.Play.Client.POSITION,
                        PacketType.Play.Client.LOOK,
                        PacketType.Play.Client.POSITION_LOOK,
                        PacketType.Play.Client.ARM_ANIMATION
                ) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        handlePacket(event);
                    }
                }
        );
        plugin.getProtocolManager().addPacketListener(this);
    }

    private void handlePacket(PacketEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        PlayerData data = plugin.getPlayerDataManager().getData(player);
        if (data == null) return;

        PacketType type = event.getPacketType();

        boolean isMovementPacket = type == PacketType.Play.Client.FLYING || type == PacketType.Play.Client.POSITION || type == PacketType.Play.Client.POSITION_LOOK || type == PacketType.Play.Client.LOOK;
        if (isMovementPacket) {
            data.getChecks().forEach(Check::tick);
        }

        boolean hasRotationUpdate = type == PacketType.Play.Client.LOOK || type == PacketType.Play.Client.POSITION_LOOK;
        if (hasRotationUpdate) {
            PacketContainer packet = event.getPacket();
            float yaw = packet.getFloat().read(0);
            float pitch = packet.getFloat().read(1);
            boolean onGround = packet.getBooleans().read(0);

            Location from = data.getLastLocation();
            Location to = from.clone();
            to.setYaw(yaw);
            to.setPitch(pitch);

            RotationUpdate update = RotationUpdate.create(data, to, from, onGround);

            data.getRotationProcessor().process(update);
            data.getChecks().forEach(check -> check.onRotation(update));

            data.setLastLocation(player.getLocation().clone());
        }

        data.getChecks().forEach(check -> check.onPacketReceiving(event));
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        PlayerData data = plugin.getPlayerDataManager().getData(player);
        if (data == null) return;

        if (event.getPacketType() == PacketType.Play.Server.ENTITY_VELOCITY) {
            if (event.getPacket().getIntegers().read(0) == player.getEntityId()) {
                data.getMovement().setTicksSinceVelocity(0);
            }
        }

        if (event.getPacketType() == PacketType.Play.Server.POSITION) {
            data.getMovement().setTicksSinceTeleport(0);
        }
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
    }

    @Override
    public ListeningWhitelist getSendingWhitelist() {
        return ListeningWhitelist.newBuilder()
                .priority(NORMAL)
                .types(PacketType.Play.Server.ENTITY_VELOCITY, PacketType.Play.Server.POSITION)
                .gamePhase(GamePhase.PLAYING)
                .build();
    }

    @Override
    public ListeningWhitelist getReceivingWhitelist() {
        return ListeningWhitelist.newBuilder().build();
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }
}