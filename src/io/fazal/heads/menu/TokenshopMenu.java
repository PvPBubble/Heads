package io.fazal.heads.menu;

import io.fazal.heads.Main;
import io.fazal.heads.category.CategoryManager;
import io.fazal.heads.menus.InventoryClickType;
import io.fazal.heads.menus.Menu;
import io.fazal.heads.menus.MenuItem;
import io.fazal.heads.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TokenshopMenu {

    public TokenshopMenu(Player player) {
        Menu menu = Utils.getInstance().getMenu("Menus.TokenshopMenu");
        CategoryManager.getInstance().getCategories().forEach(category -> menu.addMenuItem(new MenuItem() {
            @Override
            public void onClick(Player p0, InventoryClickType p1) {
                menu.closeMenu(player);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> category.openMenu(player, menu), 5L);
            }

            @Override
            public ItemStack getItemStack() {
                return Utils.getInstance().parsePlaceholders(player, category.getItem());
            }
        }, category.getSlot()));
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), player::updateInventory, 5L);
        Utils.getInstance().setupMenuBorder(menu, Utils.getInstance().loadItem("Menus.TokenshopMenu.Items.Border"));
        menu.openMenu(player);
    }

}