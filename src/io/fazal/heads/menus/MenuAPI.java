package io.fazal.heads.menus;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class MenuAPI implements Listener {
    private static MenuAPI instance;

    public static MenuAPI getInstance() {
        if (MenuAPI.instance == null) {
            synchronized (MenuAPI.class) {
                if (MenuAPI.instance == null) {
                    MenuAPI.instance = new MenuAPI();
                }
            }
        }
        return MenuAPI.instance;
    }

    public Menu createMenu(final String title, final int rows) {
        return new Menu(title, rows);
    }

    public Menu cloneMenu(final Menu menu) throws CloneNotSupportedException {
        return menu.clone();
    }

    public void removeMenu(final Menu menu) {
        for (final HumanEntity viewer : menu.getInventory().getViewers()) {
            if (viewer instanceof Player) {
                menu.closeMenu((Player) viewer);
            } else {
                viewer.closeInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMenuItemClicked(final InventoryClickEvent event) {
        final Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof Menu) {
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).updateInventory();
            final Menu menu = (Menu) inventory.getHolder();
            if (!(event.getWhoClicked() instanceof Player)) {
                return;
            }
            final Player player = (Player) event.getWhoClicked();
            if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) {
                if (menu.exitOnClickOutside()) {
                    menu.closeMenu(player);
                }
            } else {
                final int index = event.getRawSlot();
                if (index < inventory.getSize()) {
                    if (event.getAction() != InventoryAction.NOTHING) {
                        menu.selectMenuItem(player, index, InventoryClickType.fromInventoryAction(event.getAction()));
                    }
                } else {
                    if (menu.exitOnClickOutside()) {
                        menu.closeMenu(player);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMenuClosed(final InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            final Inventory inventory = event.getInventory();
            if (inventory.getHolder() instanceof Menu) {
                final Menu menu = (Menu) inventory.getHolder();
                final MenuCloseBehaviour menuCloseBehaviour = menu.getMenuCloseBehaviour();
                if (menuCloseBehaviour != null) {
                    menuCloseBehaviour.onClose((Player) event.getPlayer(), menu, menu.bypassMenuCloseBehaviour());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLogoutCloseMenu(final PlayerQuitEvent event) {
        if (event.getPlayer().getOpenInventory() == null || !(event.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof Menu)) {
            return;
        }
        final Menu menu = (Menu) event.getPlayer().getOpenInventory().getTopInventory().getHolder();
        menu.setBypassMenuCloseBehaviour(true);
        menu.setMenuCloseBehaviour(null);
        event.getPlayer().closeInventory();
    }

    public interface MenuCloseBehaviour {
        void onClose(final Player p0, final Menu p1, final boolean p2);
    }
}
