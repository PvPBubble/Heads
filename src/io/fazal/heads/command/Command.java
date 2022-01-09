package io.fazal.heads.command;

import org.bukkit.command.CommandSender;

public abstract class Command {

    public abstract void onCommand(CommandSender sender, String[] args);

}