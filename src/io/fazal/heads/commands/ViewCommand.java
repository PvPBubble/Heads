package io.fazal.heads.commands;

import io.fazal.heads.Main;
import io.fazal.heads.command.Command;
import io.fazal.heads.command.CommandInfo;
import io.fazal.heads.utils.Utils;
import io.fazal.heads.utils.set.ObjectSet;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(aliases = {"view"}, description = "View how many tokens a player has", permission = "view", usage = "(Player)")
public class ViewCommand extends Command {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            Utils.getInstance().sendMessage(sender, "INVALID_USAGE");
            return;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            Utils.getInstance().sendMessage(sender, "INVALID_PLAYER");
            return;
        }
        int tokens = Main.getInstance().getTokenManager().get(player.getUniqueId());
        Utils.getInstance().sendMessage(player, "VIEW_TOKENS", new ObjectSet("%player%", player.getName()), new ObjectSet("%tokens%", tokens));
    }

}