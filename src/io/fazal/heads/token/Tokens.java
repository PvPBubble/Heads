package io.fazal.heads.token;

import io.fazal.heads.Main;
import io.fazal.heads.utils.Utils;
import io.fazal.heads.utils.set.ObjectSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Tokens implements Listener {

    private static Tokens instance;

    public static Tokens getInstance() {
        if (instance == null) {
            synchronized (Tokens.class) {
                if (instance == null) {
                    instance = new Tokens();
                }
            }
        }
        return instance;
    }

    private Map<EntityType, Integer> mobs;
    private ItemStack item;

    public Tokens() {
        mobs = new HashMap<>();
        for (String key : Main.getInstance().getConfig().getConfigurationSection("Tokens.Chances").getKeys(false)) {
            EntityType type = EntityType.valueOf(key);
            int chance = Main.getInstance().getConfig().getInt("Tokens.Chances." + key);
            mobs.put(type, chance);
        }
        item = Utils.getInstance().loadItem("Tokens.Item");
        NamespacedKey key = new NamespacedKey(Main.getInstance(), "token");
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
        item.setItemMeta(meta);
        Main.getInstance().loadListeners(this);
    }

    public boolean isToken(ItemStack i) {
        if (i != null && i.hasItemMeta()) {
            ItemMeta meta = i.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(Main.getInstance(), "token");
            return container.has(key, PersistentDataType.INTEGER);
        }
        return false;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();

            if (item == null) {
                return;
            }

            if (!item.hasItemMeta()) {
                return;
            }

            if (!item.getItemMeta().hasDisplayName()) {
                return;
            }

            if (isToken(item)) {
                event.setCancelled(true);
                int amount = event.getPlayer().getItemInHand().getAmount();
                event.getPlayer().setItemInHand(null);
                event.getPlayer().updateInventory();
                Utils.getInstance().sendMessage(event.getPlayer(), "TOKEN_CLAIM", new ObjectSet("%tokens%", amount));
                Utils.getInstance().playSound(event.getPlayer(), "TOKEN_CLAIM");
                Main.getInstance().getTokenManager().give(event.getPlayer().getUniqueId(), amount, true, true);
            }

        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            if (mobs.containsKey(event.getEntityType())) {
                if (ThreadLocalRandom.current().nextInt(101) < mobs.get(event.getEntityType())) {
                    Player player = event.getEntity().getKiller();
                    player.getWorld().dropItem(player.getLocation(), new ItemStack(item));
                }
            }
        }
    }

    public Map<EntityType, Integer> getMobs() {
        return mobs;
    }

    public ItemStack getItem() {
        return item;
    }

}