package io.fazal.heads.head;

import io.fazal.heads.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.ThreadLocalRandom;

public class Head implements Listener {

    private EntityType entity;
    private ItemStack item;
    private ItemStack menuItem;
    private double chance;
    private int cost;
    private int slot;
    private String name;

    public Head(EntityType entity, ItemStack item, ItemStack menuItem, double chance, int cost, int slot, String name) {
        this.entity = entity;
        this.item = item;
        this.menuItem = menuItem;
        this.chance = chance;
        this.cost = cost;
        this.slot = slot;
        this.name = name;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            if (event.getEntity().getType().equals(entity)) {
                Player player = event.getEntity().getKiller();
                if (ThreadLocalRandom.current().nextInt(100) < chance) {
                    player.getWorld().dropItem(player.getLocation(), new ItemStack(item));
                }
            }
        }
    }

    public boolean isHead(ItemStack i) {
        if (i != null && i.hasItemMeta()) {
            ItemMeta meta = i.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(Main.getInstance(), "head");
            if (container.has(key, PersistentDataType.STRING)) {
                String id = container.get(key, PersistentDataType.STRING);
                return id.equals(name);
            }
        }
        return false;
    }

    public ItemStack getItem() {
        return item;
    }

    public ItemStack getMenuItem() {
        return menuItem;
    }

    public double getChance() {
        return chance;
    }

    public EntityType getEntity() {
        return entity;
    }

    public int getCost() {
        return cost;
    }

    public int getSlot() {
        return slot;
    }

    public String getName() {
        return name;
    }

}