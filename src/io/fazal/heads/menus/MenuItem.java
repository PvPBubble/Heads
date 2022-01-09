package io.fazal.heads.menus;

import io.fazal.heads.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class MenuItem {
    private Menu menu;
    private int slot;
    private ItemStack itemStack;
    private boolean clickable = true;

    void addToMenu(final Menu menu) {
        this.menu = menu;
    }

    public abstract void onClick(final Player p0, final InventoryClickType p1);

    void removeFromMenu(final Menu menu) {
        if (this.menu == menu) {
            this.menu = null;
        }
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack item, boolean update) {
        this.itemStack = item;

        if (update && (this.getMenu() != null)) {
            this.getMenu().addMenuItem(this, getSlot());
            this.getMenu().updateMenu();
        }
    }

    private Menu getMenu() {
        return this.menu;
    }

    private int getSlot() {
        return this.slot;
    }

    public void setSlot(final int slot) {
        this.slot = slot;
    }

    public boolean isClickable() {
        return clickable;
    }

    private void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public void setTemporaryIcon(ItemStack item, long time) {
        ItemStack currentItemStack = getItemStack();
        menu.getInventory().setItem(getSlot(), item);
        menu.updateMenu();
        setClickable(false);
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            setClickable(true);
            menu.getInventory().setItem(getSlot(), currentItemStack);
            menu.updateMenu();
        }, time);
    }

    @Override
    public String toString() {
        return "MenuItem{menu=" + menu.toString() + ", item=" + getItemStack().toString() + ", slot=" + slot + ", clickable=" + clickable + "}";
    }

    public abstract static class UnclickableMenuItem extends MenuItem {
        @Override
        public void onClick(final Player player, final InventoryClickType clickType) {
        }
    }

}