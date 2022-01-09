package io.fazal.heads.category;

import io.fazal.heads.Main;
import io.fazal.heads.menus.Menu;
import io.fazal.heads.menus.MenuAPI;
import io.fazal.heads.tokenshop.TokenshopItem;
import io.fazal.heads.utils.Utils;
import io.fazal.heads.utils.set.ObjectSet;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private List<TokenshopItem> items;
    private int size;
    private int slot;
    private ItemStack item;
    private String name;

    public Category(String key) {
        items = new ArrayList<>();
        ConfigurationSection section = Main.getInstance().getConfig().getConfigurationSection("Categories." + key);
        this.size = section.getInt("Size");
        this.slot = section.getInt("Slot");
        this.item = Utils.getInstance().loadItem("Categories." + key + ".Item");
        this.name = key;
        for (String key2 : Main.getInstance().getConfig().getConfigurationSection("Categories." + key + ".Items").getKeys(false)) {
            ConfigurationSection section2 = Main.getInstance().getConfig().getConfigurationSection("Categories." + key + ".Items." + key2);
            items.add(new TokenshopItem(section2.getInt("Slot"), section2.getInt("Price"), section2.getStringList("Commands"), Utils.getInstance().loadItem("Categories." + key + ".Items." + key2 + ".Item"), key2));
        }
    }

    public void openMenu(Player player, Menu oldMenu) {
        Menu menu = MenuAPI.getInstance().createMenu(Utils.getInstance().toColor(Main.getInstance().getConfig().getString("Menus.CategoryMenu.Title").replace("%category%", name)), size / 9);
        if (oldMenu != null) {
            menu.setParent(oldMenu);
        }
        menu.setMenuCloseBehaviour((player1, menu1, bypass) -> {
            if (!bypass) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                    if (!menu1.bypassMenuCloseBehaviour() && (menu1.getParent() != null)) {
                        menu1.getParent().openMenu(player1);
                    }
                    Utils.getInstance().playSound(player, "CLOSE_CATEGORY");
                }, 5L);
            }
        });
        Utils.getInstance().setupMenuBorder(menu, Utils.getInstance().loadItem("Menus.CategoryMenu.Items.Border"));
        for (TokenshopItem item : items) {
            Utils.getInstance().addItem(menu, player, item.getItem(), item.getSlot(), () -> {
                menu.setBypassMenuCloseBehaviour(true);
                if (Main.getInstance().getTokenManager().get(player.getUniqueId()) >= item.getPrice()) {
                    Main.getInstance().getTokenManager().remove(player.getUniqueId(), item.getPrice());
                    item.executeCommands(player);
                    Utils.getInstance().sendMessage(player, "PURCHASE_SUCCESS", new ObjectSet("%item%", item.getName()), new ObjectSet("%tokens%", item.getPrice()));
                    Utils.getInstance().playSound(player, "PURCHASE_SUCCESS");
                } else {
                    Utils.getInstance().sendMessage(player, "NOT_ENOUGH_TOKENS");
                    Utils.getInstance().playSound(player, "NOT_ENOUGH_TOKENS");
                    menu.closeMenu(player);
                }
            });
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), player::updateInventory, 5L);
        menu.openMenu(player);
    }

    public ItemStack getItem() {
        return item;
    }

    public int getSlot() {
        return slot;
    }

    public int getSize() {
        return size;
    }

    public List<TokenshopItem> getItems() {
        return items;
    }

    public String getName() {
        return name;
    }

}