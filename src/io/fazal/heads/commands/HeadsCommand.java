package io.fazal.heads.commands;

import io.fazal.heads.head.Head;
import io.fazal.heads.head.HeadManager;
import io.fazal.heads.menu.DepositMenu;
import io.fazal.heads.utils.Utils;
import io.fazal.heads.utils.set.ObjectSet;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HeadsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("heads")) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    new DepositMenu(player);
                }
            } else if (sender.hasPermission("heads.admin")) {
                if (args.length < 4) {
                    sender.sendMessage("§c/heads give <Player> <Type> <Amount>");
                    sender.sendMessage("§c/heads remove <Player> <Type> <Amount>");
                    return false;
                }
                if (args[0].equalsIgnoreCase("give")) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player != null) {
                        String name = args[2];
                        if (HeadManager.getInstance().getByName(name) != null) {
                            Head head = HeadManager.getInstance().getByName(name);
                            if (Utils.getInstance().isInteger(args[3])) {
                                int amount = Integer.parseInt(args[3]);
                                ItemStack itemStack = new ItemStack(head.getItem());
                                itemStack.setAmount(Math.max(amount, 1));
                                player.getInventory().addItem(itemStack);
                                player.updateInventory();
                            }
                        } else {
                            Utils.getInstance().sendMessage(sender, "INVALID_HEAD");
                        }
                    } else {
                        Utils.getInstance().sendMessage(sender, "INVALID_PLAYER");
                    }
                } else if (args[0].equalsIgnoreCase("remove")) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player != null) {
                        String name = args[2];
                        if (HeadManager.getInstance().getByName(name) != null) {
                            Head head = HeadManager.getInstance().getByName(name);
                            if (Utils.getInstance().isInteger(args[3])) {
                                int amountNeeded = Integer.parseInt(args[3]);
                                ItemStack itemToDelete = null;
                                List<ItemStack> otherItemsToDelete = new ArrayList<>();
                                for (ItemStack item : player.getInventory().getContents()) {
                                    if (head.isHead(item)) {
                                        if (item.getAmount() > amountNeeded) {
                                            item.setAmount(item.getAmount() - amountNeeded);
                                            amountNeeded = -1;
                                            break;
                                        } else if (item.getAmount() == amountNeeded) {
                                            itemToDelete = item;
                                            amountNeeded = -1;
                                            break;
                                        } else {
                                            if (item.getAmount() < amountNeeded) {
                                                amountNeeded -= item.getAmount();
                                                otherItemsToDelete.add(item);
                                            }
                                        }
                                    }
                                }
                                if (itemToDelete != null) {
                                    player.getInventory().remove(itemToDelete);
                                }
                                player.updateInventory();
                                if (amountNeeded == -1) {
                                    for (ItemStack itemStack : otherItemsToDelete) {
                                        player.getInventory().remove(itemStack);
                                    }
                                } else {
                                    Utils.getInstance().sendMessage(sender, "NOT_ENOUGH_HEADS_COMMAND", new ObjectSet("%player%", player.getName()));
                                }
                            }
                        }
                    }
                } else {
                    sender.sendMessage("§c/heads give <Player> <Type> <Amount>");
                    sender.sendMessage("§c/heads remove <Player> <Type> <Amount>");
                }
            }
        }
        return false;
    }

}