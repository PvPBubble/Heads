package io.fazal.heads.commands;

import io.fazal.heads.Main;
import io.fazal.heads.command.Command;
import io.fazal.heads.command.CommandInfo;
import io.fazal.heads.utils.Utils;
import io.fazal.heads.utils.set.ObjectSet;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(aliases = {"take"}, description = "Take tokens from a player!", permission = "take", usage = "(Player) (Amount)")
public class TakeCommand extends Command {

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
        int playerTokens = Main.getInstance().getTokenManager().get(player.getUniqueId());
        if (playerTokens < 1) {
            Utils.getInstance().sendMessage(sender, "NOT_ENOUGH_TOKENS_TAKE", new ObjectSet("%player%", player.getName()));
            return;
        }
        if (amount > playerTokens) {
            amount = playerTokens;
        }
        Main.getInstance().getTokenManager().remove(player.getUniqueId(), amount);
        Utils.getInstance().sendMessage(sender, "TAKE_TOKENS", new ObjectSet("%player%", player.getName()), new ObjectSet("%tokens%", amount));
    }

}