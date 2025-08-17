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
import ru.Fronzter.ShadeAc.ml.ModelTrainer;
import ru.Fronzter.ShadeAc.utils.color.ColorUtils;

public class ShadeAcMLCommand implements CommandExecutor {
    private final ShadeAc plugin;

    public ShadeAcMLCommand(ShadeAc plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("shadeac.ml.admin")) {
            sender.sendMessage(ColorUtils.colorize("&cНет прав для выполнения команды."));
            return true;
        }

        if (args.length < 1) {
            sendHelp(sender, label);
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "start":
                if (args.length < 3) {
                    sender.sendMessage(ColorUtils.colorize("&eИспользование: /" + label + " start <игрок> <legit|cheat>"));
                    return true;
                }
                Player targetStart = Bukkit.getPlayer(args[1]);
                if (targetStart == null) {
                    sender.sendMessage(ColorUtils.colorize("&cИгрок '" + args[1] + "' не найден."));
                    return true;
                }
                String type = args[2].toLowerCase();
                if (!type.equals("legit") && !type.equals("cheat")) {
                    sender.sendMessage(ColorUtils.colorize("&cТип должен быть 'legit' или 'cheat'."));
                    return true;
                }
                plugin.getDataCollectionManager().startSession(targetStart, type);
                sender.sendMessage(ColorUtils.colorize("&aСессия сбора данных для игрока &f" + targetStart.getName() + " &aзапущена."));
                break;

            case "stop":
                if (args.length < 2) {
                    sender.sendMessage(ColorUtils.colorize("&eИспользование: /" + label + " stop <игрок>"));
                    return true;
                }
                Player targetStop = Bukkit.getPlayer(args[1]);
                if (targetStop == null) {
                    sender.sendMessage(ColorUtils.colorize("&cИгрок '" + args[1] + "' не найден."));
                    return true;
                }
                plugin.getDataCollectionManager().stopSession(targetStop);
                sender.sendMessage(ColorUtils.colorize("&aСессия сбора данных для игрока &f" + targetStop.getName() + " &aостановлена и сохранена."));
                break;

            case "train":
                int epochs = 100;
                if (args.length > 1) {
                    try {
                        epochs = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ColorUtils.colorize("&cКоличество эпох должно быть числом."));
                        return true;
                    }
                }
                sender.sendMessage(ColorUtils.colorize("&aНачато обучение модели (&e" + epochs + " &aэпох)."));
                new ModelTrainer(plugin, sender, epochs).runTaskAsynchronously(plugin);
                break;

            default:
                sendHelp(sender, label);
                break;
        }

        return true;
    }

    private void sendHelp(CommandSender sender, String label) {
        sender.sendMessage(ColorUtils.colorize("&bShadeAc ML"));
        sender.sendMessage(ColorUtils.colorize("&e/" + label + " start <игрок> <legit|cheat> &7- Запустить сбор данных."));
        sender.sendMessage(ColorUtils.colorize("&e/" + label + " stop <игрок> &7- Остановить сбор данных."));
        sender.sendMessage(ColorUtils.colorize("&e/" + label + " train [эпохи] &7- Начать обучение модели."));
    }
}
