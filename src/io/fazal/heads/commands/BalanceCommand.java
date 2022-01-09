package io.fazal.heads.commands;

import io.fazal.heads.Main;
import io.fazal.heads.command.Command;
import io.fazal.heads.command.CommandInfo;
import io.fazal.heads.utils.Utils;
import io.fazal.heads.utils.set.ObjectSet;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(aliases = {"balance"}, description = "View how many tokens you have", permission = "none", usage = "")
public class BalanceCommand extends Command {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            int tokens = Main.getInstance().getTokenManager().get(player.getUniqueId());
            Utils.getInstance().sendMessage(player, "VIEW_TOKENS_SELF", new ObjectSet("%tokens%", tokens));
        }
    }

}