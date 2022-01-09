package io.fazal.heads.commands;

import io.fazal.heads.Main;
import io.fazal.heads.command.Command;
import io.fazal.heads.command.CommandInfo;
import io.fazal.heads.utils.Utils;
import io.fazal.heads.utils.set.ObjectSet;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(aliases = {"pay"}, description = "Pay another player using tokens", permission = "none", usage = "(Player) (Tokens)")
public class PayCommand extends Command {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (args.length != 2) {
                Utils.getInstance().sendMessage(sender, "INVALID_USAGE");
                return;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                Utils.getInstance().sendMessage(sender, "INVALID_PLAYER");
                return;
            }
            if (!Utils.getInstance().isInteger(args[1])) {
                Utils.getInstance().sendMessage(sender, "INVALID_NUMBER");
                return;
            }
            Player player = (Player) sender;
            int amount = Integer.parseInt(args[1]);
            if (amount < 1) {
                Utils.getInstance().sendMessage(sender, "INVALID_NUMBER");
                return;
            }
            if (Main.getInstance().getTokenManager().get(player.getUniqueId()) >= amount) {
                Main.getInstance().getTokenManager().give(target.getUniqueId(), amount, false, true);
                Main.getInstance().getTokenManager().remove(player.getUniqueId(), amount);
                Utils.getInstance().sendMessage(sender, "PAID_TOKENS", new ObjectSet("%player%", target.getName()), new ObjectSet("%tokens%", amount));
                Utils.getInstance().sendMessage(sender, "RECEIVED_PAY_TOKENS", new ObjectSet("%tokens%", amount), new ObjectSet("%player%", player.getName()));
            } else {
                Utils.getInstance().sendMessage(player, "NOT_ENOUGH_TOKENS");
            }
        } else sender.sendMessage("§cOnly players can execute this command!");
    }

}