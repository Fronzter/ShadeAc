package ru.Fronzter.ShadeAc.command.impl;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.command.SubCommand;
import ru.Fronzter.ShadeAc.data.PlayerData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PunishCommand extends SubCommand {
    @Override public String getName() { return "punish"; }
    @Override public String getDescription() { return "Запускает анимированное наказание для игрока."; }
    @Override public String getSyntax() { return "/shadeac punish <player>"; }
    @Override public String getPermission() { return "shadeac.command.punish"; }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getSyntax());
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return;
        }

        PlayerData playerData = ShadeAc.getInstance().getPlayerDataManager().getData(target);
        if (playerData == null) {
            sender.sendMessage(ChatColor.RED + "Could not retrieve player data for that player.");
            return;
        }

        ShadeAc.getInstance().getAnimationManager().playPunishmentAnimation(playerData, "Manual Punishment by " + sender.getName());
        sender.sendMessage(ChatColor.GREEN + "Started punishment animation for " + target.getName());
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        if (args.length == 2) {
            List<String> playerNames = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(p -> playerNames.add(p.getName()));
            return StringUtil.copyPartialMatches(args[1], playerNames, new ArrayList<>());
        }
        return Collections.emptyList();
    }
}