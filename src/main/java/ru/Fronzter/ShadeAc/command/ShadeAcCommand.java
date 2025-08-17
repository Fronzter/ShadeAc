package ru.Fronzter.ShadeAc.command;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.utils.color.ColorUtils;

import java.io.File;
import java.util.List;

public class ShadeAcCommand implements CommandExecutor {

    private final ShadeAc plugin;
    private FileConfiguration helpConfig;

    public ShadeAcCommand(ShadeAc plugin) {
        this.plugin = plugin;
        loadHelpFile();
    }

    private void loadHelpFile() {
        File helpFile = new File(plugin.getDataFolder(), "help.yml");
        if (!helpFile.exists()) {
            plugin.saveResource("help.yml", false);
        }
        this.helpConfig = YamlConfiguration.loadConfiguration(helpFile);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("shadeac.admin")) {
            sender.sendMessage(ColorUtils.colorize("&cНет прав для выполнения команды."));
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelpSection(sender, "main-commands");
            sendHelpSection(sender, "machine-learning-guide.introduction", false);
            sendHelpSection(sender, "machine-learning-guide.step1-collection");
            sendHelpSection(sender, "machine-learning-guide.step2-training");
            sendHelpSection(sender, "machine-learning-guide.step3-usage");
            return true;
        }

        sender.sendMessage(ColorUtils.colorize("&cНеизвестная подкоманда. Используй /" + label + " help"));
        return true;
    }

    private void sendHelpSection(CommandSender sender, String path) {
        sendHelpSection(sender, path, true);
    }

    private void sendHelpSection(CommandSender sender, String path, boolean showTitle) {
        if (showTitle && helpConfig.contains(path + ".title")) {
            sender.sendMessage("");
            sender.sendMessage(ColorUtils.colorize(helpConfig.getString(path + ".title")));
        }

        List<String> lines = helpConfig.getStringList(path + ".lines");
        if (lines.isEmpty()) {
            lines = helpConfig.getStringList(path);
        }

        for (String line : lines) {
            sender.sendMessage(ColorUtils.colorize(" &7" + line));
        }
    }
}
