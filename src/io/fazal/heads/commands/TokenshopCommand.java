package io.fazal.heads.commands;

import io.fazal.heads.menu.TokenshopMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TokenshopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tokenshop")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                new TokenshopMenu(player);
            }
        }
        return false;
    }

}