package ru.Fronzter.ShadeAc.command;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import ru.Fronzter.ShadeAc.command.impl.AlertsCommand;
import ru.Fronzter.ShadeAc.command.impl.PunishCommand;
import ru.Fronzter.ShadeAc.command.impl.ReloadCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final List<SubCommand> subCommands = new ArrayList<>();

    public CommandManager() {
        subCommands.add(new ReloadCommand());
        subCommands.add(new AlertsCommand());
        subCommands.add(new PunishCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getName().equalsIgnoreCase(args[0])) {
                    if (sender.hasPermission(subCommand.getPermission())) {
                        subCommand.perform(sender, args);
                    } else {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to use this command. (no pravo)");
                    }
                    return true;
                }
            }
        }
        sendHelpMessage(sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return subCommands.stream()
                    .filter(sub -> sender.hasPermission(sub.getPermission()))
                    .map(SubCommand::getName)
                    .filter(name -> StringUtil.startsWithIgnoreCase(name, args[0]))
                    .collect(Collectors.toList());
        } else if (args.length > 1) {
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getName().equalsIgnoreCase(args[0]) && sender.hasPermission(subCommand.getPermission())) {
                    return subCommand.getSubcommandArguments(sender, args);
                }
            }
        }
        return new ArrayList<>();
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "ShadeAc Commands");
        for (SubCommand sub : subCommands) {
            if (sender.hasPermission(sub.getPermission())) {
                sender.sendMessage(ChatColor.GRAY + sub.getSyntax() + " - " + ChatColor.WHITE + sub.getDescription());
            }
        }
    }
}
