package ru.Fronzter.ShadeAc.command.impl;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.command.SubCommand;
import java.util.Collections;
import java.util.List;

public class AlertsCommand extends SubCommand {
    @Override public String getName() { return "alerts"; }
    @Override public String getDescription() { return "Включает или выключает отображение алертов."; }
    @Override public String getSyntax() { return "/shadeac alerts"; }
    @Override public String getPermission() { return "shadeac.command.alerts"; }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return;
        }
        Player player = (Player) sender;
        boolean toggled = ShadeAc.getInstance().getAlertManager().toggleAlerts(player.getUniqueId());

        if (toggled) {
            player.sendMessage(ChatColor.GREEN + "Alerts enabled.");
        } else {
            player.sendMessage(ChatColor.RED + "Alerts disabled.");
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}