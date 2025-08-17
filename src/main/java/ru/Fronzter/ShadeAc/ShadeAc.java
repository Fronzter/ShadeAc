package ru.Fronzter.ShadeAc;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.Fronzter.ShadeAc.command.ShadeAcCommand;
import ru.Fronzter.ShadeAc.command.ShadeAcMLCommand;
import ru.Fronzter.ShadeAc.command.ShadeAcPunishCommand;
import ru.Fronzter.ShadeAc.listener.MitigationListener;
import ru.Fronzter.ShadeAc.listener.PacketListener;
import ru.Fronzter.ShadeAc.listener.PlayerListener;
import ru.Fronzter.ShadeAc.manager.AlertManager;
import ru.Fronzter.ShadeAc.manager.CheckManager;
import ru.Fronzter.ShadeAc.manager.ConfigManager;
import ru.Fronzter.ShadeAc.manager.PlayerManager;
import ru.Fronzter.ShadeAc.manager.PunishmentManager;
import ru.Fronzter.ShadeAc.ml.DataCollectionManager;
import ru.Fronzter.ShadeAc.nms.NMSManager;
import ru.Fronzter.ShadeAc.task.TickTask;

@Getter
public final class ShadeAc extends JavaPlugin {

    @Getter private static ShadeAc instance;

    private ConfigManager configManager;
    private AlertManager alertManager;
    private PunishmentManager punishmentManager;
    private PlayerManager playerManager;
    private CheckManager checkManager;
    private NMSManager nmsManager;
    private DataCollectionManager dataCollectionManager;

    @Override
    public void onEnable() {
        instance = this;

        this.configManager = new ConfigManager(this);
        this.alertManager = new AlertManager(this);
        this.punishmentManager = new PunishmentManager(this);
        this.nmsManager = new NMSManager(this);

        if (!this.isEnabled()) return;

        this.playerManager = new PlayerManager();
        this.checkManager = new CheckManager();
        this.dataCollectionManager = new DataCollectionManager(this);

        new PlayerListener(this);
        new PacketListener(this);
        new MitigationListener(this);

        getCommand("shadeac").setExecutor(new ShadeAcCommand(this));
        getCommand("shadeacpunish").setExecutor(new ShadeAcPunishCommand(this));
        getCommand("shadeacml").setExecutor(new ShadeAcMLCommand(this));

        new TickTask(this).runTaskTimer(this, 1L, 1L);
    }

    @Override
    public void onDisable() {
    }
}
