package io.fazal.heads.menu;

import io.fazal.heads.Main;
import io.fazal.heads.head.HeadManager;
import io.fazal.heads.menus.InventoryClickType;
import io.fazal.heads.menus.Menu;
import io.fazal.heads.menus.MenuItem;
import io.fazal.heads.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DepositMenu {

    public DepositMenu(Player player) {
        Menu menu = Utils.getInstance().getMenu("Menus.DepositMenu");
        HeadManager.getInstance().getHeads().forEach(head -> menu.addMenuItem(new MenuItem() {
            @Override
            public void onClick(Player player, InventoryClickType clickType) {
                int amountNeeded = head.getCost();
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
                    Main.getInstance().getTokenManager().give(player.getUniqueId(), 1, true, true);
                    for (ItemStack itemStack : otherItemsToDelete) {
                        player.getInventory().remove(itemStack);
                    }
                } else {
                    Utils.getInstance().sendMessage(player, "NOT_ENOUGH_HEADS");
                    Utils.getInstance().playSound(player, "NOT_ENOUGH_HEADS");
                    menu.closeMenu(player);
                }
            }

            @Override
            public ItemStack getItemStack() {
                return Utils.getInstance().parsePlaceholders(player, head.getMenuItem());
            }
        }, head.getSlot()));
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), player::updateInventory, 5L);
        Utils.getInstance().setupMenuBorder(menu, Utils.getInstance().loadItem("Menus.DepositMenu.Items.Border"));
        menu.openMenu(player);
    }

}