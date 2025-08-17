package ru.Fronzter.ShadeAc.command;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.animation.PunishmentAnimation;
import ru.Fronzter.ShadeAc.utils.color.ColorUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ShadeAcPunishCommand implements CommandExecutor {

    private final ShadeAc plugin;

    public ShadeAcPunishCommand(ShadeAc plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("shadeac.punish")) {
            sender.sendMessage(ColorUtils.colorize("&cНет прав для использования этой команды."));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ColorUtils.colorize("&eИспользование: /" + label + " <игрок> [причина]"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ColorUtils.colorize("&cИгрок не найден."));
            return true;
        }

        String reason = "Наказан администратором.";
        if (args.length > 1) {
            reason = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        }

        String commandTemplate = plugin.getPunishmentManager().getAnimatedPunishmentCommand();
        String finalCommand = commandTemplate
                .replace("%player%", target.getName())
                .replace("%reason%", reason);

        new PunishmentAnimation(plugin, target, finalCommand).runTaskTimer(plugin, 0L, 1L);
        sender.sendMessage(ColorUtils.colorize("&aАнимация наказания запущена для &f" + target.getName()));

        return true;
    }
}
