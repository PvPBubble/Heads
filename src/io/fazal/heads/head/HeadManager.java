package io.fazal.heads.head;

import io.fazal.heads.Main;
import io.fazal.heads.utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HeadManager {

    private static HeadManager instance;
    private final List<Head> heads;

    public HeadManager() {
        heads = new ArrayList<>();
        for (String key : Main.getInstance().getConfig().getConfigurationSection("Heads").getKeys(false)) {
            ConfigurationSection section = Main.getInstance().getConfig().getConfigurationSection("Heads." + key);
            ItemStack itemStack = Utils.getInstance().loadItem("Heads." + key + ".DropItem");
            NamespacedKey keyP = new NamespacedKey(Main.getInstance(), "head");
            ItemMeta meta = itemStack.getItemMeta();
            meta.getPersistentDataContainer().set(keyP, PersistentDataType.STRING, key);
            itemStack.setItemMeta(meta);
            Head head = new Head(EntityType.valueOf(section.getString("Mob")), itemStack, Utils.getInstance().loadItem("Heads." + key + ".MenuItem"), section.getDouble("Chance"), section.getInt("Cost"), section.getInt("Slot"), key);
            heads.add(head);
            Main.getInstance().loadListeners(head);
        }
    }

    public static HeadManager getInstance() {
        if (instance == null) {
            synchronized (HeadManager.class) {
                if (instance == null) {
                    instance = new HeadManager();
                }
            }
        }
        return instance;
    }

    public Head getByName(String name) {
        return heads.stream().filter(head -> head.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Head> getHeads() {
        return heads;
    }

}