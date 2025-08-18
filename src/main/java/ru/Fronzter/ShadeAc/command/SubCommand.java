package ru.Fronzter.ShadeAc.command;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import org.bukkit.command.CommandSender;
import java.util.List;

public abstract class SubCommand {

    public abstract String getName();
    public abstract String getDescription();
    public abstract String getSyntax();
    public abstract String getPermission();
    public abstract void perform(CommandSender sender, String[] args);
    public abstract List<String> getSubcommandArguments(CommandSender sender, String[] args);
}
