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
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.command.SubCommand;
import java.util.Collections;
import java.util.List;

public class ReloadCommand extends SubCommand {
    @Override public String getName() { return "reload"; }
    @Override public String getDescription() { return "Перезагружает конфигурацию плагина."; }
    @Override public String getSyntax() { return "/shadeac reload"; }
    @Override public String getPermission() { return "shadeac.command.reload"; }

    @Override
    public void perform(CommandSender sender, String[] args) {
        ShadeAc plugin = ShadeAc.getInstance();
        plugin.getConfigManager().load();
        plugin.getPunishmentManager().load();
        plugin.getAnimationManager().load();
        sender.sendMessage(ChatColor.GREEN + "ShadeAc configuration has been reloaded.");
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}