package io.fazal.heads.commands;

import io.fazal.heads.Main;
import io.fazal.heads.command.Command;
import io.fazal.heads.command.CommandInfo;
import io.fazal.heads.utils.Utils;
import io.fazal.heads.utils.set.ObjectSet;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(aliases = {"give"}, description = "Give tokens to a player!", permission = "give", usage = "(Player) (Amount)")
public class GiveCommand extends Command {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length != 2) {
            Utils.getInstance().sendMessage(sender, "INVALID_USAGE");
            return;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            Utils.getInstance().sendMessage(sender, "INVALID_PLAYER");
            return;
        }
        if (!Utils.getInstance().isInteger(args[1])) {
            Utils.getInstance().sendMessage(sender, "INVALID_NUMBER");
            return;
        }
        int amount = Integer.parseInt(args[1]);
        Main.getInstance().getTokenManager().give(player.getUniqueId(), amount, false, true);
        Utils.getInstance().sendMessage(sender, "GAVE_TOKENS", new ObjectSet("%player%", player.getName()), new ObjectSet("%tokens%", amount));
        Utils.getInstance().sendMessage(player, "RECEIVED_TOKENS", new ObjectSet("%tokens%", amount));
    }

}