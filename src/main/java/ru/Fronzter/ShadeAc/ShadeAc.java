package ru.Fronzter.ShadeAc;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.Fronzter.ShadeAc.command.CommandManager;
import ru.Fronzter.ShadeAc.listener.MitigationListener;
import ru.Fronzter.ShadeAc.listener.PacketProcessor;
import ru.Fronzter.ShadeAc.listener.PlayerConnectionListener;
import ru.Fronzter.ShadeAc.listener.PlayerMoveListener;
import ru.Fronzter.ShadeAc.manager.*;

import java.util.Set;
import java.util.UUID;

@Getter
public final class ShadeAc extends JavaPlugin {

    @Getter
    private static ShadeAc instance;

    private ConfigManager configManager;
    private PunishmentManager punishmentManager;
    private AnimationManager animationManager;
    private PlayerDataManager playerDataManager;
    private CheckManager checkManager;
    private MitigationManager mitigationManager;
    private AlertManager alertManager;

    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        instance = this;
        this.protocolManager = ProtocolLibrary.getProtocolManager();

        this.configManager = new ConfigManager(this);
        this.punishmentManager = new PunishmentManager(this);
        this.animationManager = new AnimationManager(this);
        this.playerDataManager = new PlayerDataManager();
        this.checkManager = new CheckManager();
        this.mitigationManager = new MitigationManager();
        this.alertManager = new AlertManager();

        new PacketProcessor().register();

        this.getServer().getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        this.getServer().getPluginManager().registerEvents(new MitigationListener(), this);

        CommandManager commandManager = new CommandManager();
        getCommand("shadeac").setExecutor(commandManager);
        getCommand("shadeac").setTabCompleter(commandManager);
    }

    @Override
    public void onDisable() {
        for (UUID uuid : Set.copyOf(animationManager.getFrozenPlayers())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                animationManager.forceUnfreeze(player);
            }
        }
    }
}
