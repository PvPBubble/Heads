package io.fazal.heads.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.fazal.heads.Main;
import io.fazal.heads.hooks.HookManager;
import io.fazal.heads.menus.InventoryClickType;
import io.fazal.heads.menus.Menu;
import io.fazal.heads.menus.MenuItem;
import io.fazal.heads.utils.chat.ChatCenter;
import io.fazal.heads.utils.set.ObjectSet;
import io.fazal.heads.utils.sound.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Utils {

    private static Utils instance;

    public static Utils getInstance() {
        if (instance == null) {
            synchronized (Utils.class) {
                if (instance == null) {
                    instance = new Utils();
                }
            }
        }
        return instance;
    }

    public String formatNumber(long n) {
        if (n > 1.0E9) {
            return Math.round(n / 1000000.0) / 1000.0 + "B";
        }
        if (n > 1000000.0) {
            return Math.round(n / 10000.0) / 100.0 + "M";
        }
        if (n > 1000.0) {
            return Math.round(n / 100.0) / 10.0 + "k";
        }
        return new StringBuilder(String.valueOf(n)).toString();
    }

    public String toColor(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public ItemStack parsePlaceholders(Player player, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        String displayname = meta.getDisplayName();
        List<String> lore = meta.getLore();
        displayname = HookManager.getInstance().replacePlaceholders(player, displayname);
        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            line = HookManager.getInstance().replacePlaceholders(player, line);
            line = toColor(line);
            lore.set(i, line);
        }
        displayname = toColor(displayname);
        meta.setDisplayName(displayname);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void playSound(Player player, String path) {
        path = "Sounds." + path;
        float volume = (float) Main.getInstance().getConfig().getDouble(path + ".Volume");
        float pitch = (float) Main.getInstance().getConfig().getDouble(path + ".Pitch");
        player.playSound(player.getLocation(), Sounds.valueOf(Main.getInstance().getConfig().getString(path + ".Sound").toUpperCase()).bukkitSound(), volume, pitch);
    }

    public void sendMessage(CommandSender sender, String config, ObjectSet... objectSets) {
        for (String message : Main.getInstance().getConfig().getStringList("Messages." + config)) {
            for (ObjectSet set : objectSets) {
                message = message.replace(set.getObject1().toString(), set.getObject2().toString());
            }
            if (message.startsWith("[center]")) {
                message = ChatCenter.getCenteredMessage(message.replace("[center]", ""));
            }
            message = toColor(message);
            sender.sendMessage(message);
        }
    }

    public Menu getMenu(String path, ObjectSet... objectSets) {
        ConfigurationSection section = Main.getInstance().getConfig().getConfigurationSection(path);
        if (section == null) {
            Main.getInstance().log("§c[Heads] \"" + path + "\" path does not exist");
            return null;
        }
        String title = section.getString("Title");
        for (ObjectSet set : objectSets) {
            title = title.replace(set.getObject1().toString(), set.getObject2().toString());
        }
        return new Menu(toColor(title), section.getInt("Size") / 9);
    }

    public void setupMenuBorder(Menu menu, ItemStack border) {
        for (int i = 0; i < menu.getInventory().getSize(); i++) {
            if (menu.getInventory().getItem(i) == null) {
                menu.addMenuItem(new MenuItem.UnclickableMenuItem() {
                    @Override
                    public void onClick(Player player, InventoryClickType clickType) {
                        super.onClick(player, clickType);
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return border;
                    }
                }, i);
            }
        }
    }

    public void addItem(Menu menu, Player player, ItemStack item, int slot, Runnable runnable) {
        menu.addMenuItem(new MenuItem() {
            @Override
            public void onClick(Player player, InventoryClickType clickType) {
                runnable.run();
            }

            @Override
            public ItemStack getItemStack() {
                return parsePlaceholders(player, item);
            }
        }, slot);

    }

    public ItemStack loadItem(String path) {
        return loadItem(path, null);
    }

    @SuppressWarnings("deprecation")
    public ItemStack loadItem(String path, Player player) {
        ItemStack itemStack;
        ConfigurationSection section = Main.getInstance().getConfig().getConfigurationSection(path);
        if (section == null) {
            System.out.print("[C0d3rCrates] \"" + path + "\" path does not exist");
            return null;
        }
        String materialName = section.getString("Material", "STONE");
        Material material = Material.getMaterial(materialName);
        int data = 0;
        String name = section.getString("Name");
        name = toColor(name);
        List<String> lore = new ArrayList<>();
        section.getStringList("Lore").stream().map(this::toColor).forEach(lore::add);
        if (material == null) {
            if (isInteger(materialName)) {
                material = Material.getMaterial(materialName);
            } else if (materialName.contains(";")) {
                String[] materialArgs = materialName.trim().split(";");
                if (isInteger(materialArgs[0])) {
                    material = Material.getMaterial(materialArgs[0]);
                    data = Integer.parseInt(materialArgs[1]);
                }
            } else if (materialName.contains(":")) {
                String[] materialArgs = materialName.trim().split(":");
                if (isInteger(materialArgs[0])) {
                    material = Material.getMaterial(materialArgs[0]);
                    data = Integer.parseInt(materialArgs[1]);
                }
            } else if (materialName.contains(",")) {
                String[] materialArgs = materialName.trim().split(",");
                if (isInteger(materialArgs[0])) {
                    material = Material.getMaterial(materialArgs[0]);
                    data = Integer.parseInt(materialArgs[1]);
                }
            } else if (Main.getInstance().getEss() != null) {
                try {
                    material = Main.getInstance().getEss().getItemDb().get(materialName, 1).getType();
                } catch (Exception ignore) {
                }
            }
            if (material == null) {
                material = Material.STONE;
                System.out.print("[C0d3rCrates] Unable to load material \"" + materialName + "\" for path \"" + path
                        + "\", so it has been set to STONE.");
            }
        }
        if (data == 0) {
            data = section.getInt("Data", 0);
        }
        itemStack = new ItemStack(material, 1);
        itemStack.setDurability((short) data);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        if (!lore.isEmpty()) {
            meta.setLore(lore);
        }
        if (itemStack.getType() == Material.PLAYER_HEAD && section.getString("Owner") != null) {
            /*if (player != null && player.getName() != null && section.getString("owner").equalsIgnoreCase("%player%")) {
                ((SkullMeta) meta).setOwner(player.getName());
            } else {
                ((SkullMeta) meta).setOwner(section.getString("owner", "Fazal"));
            }*/
            setOwner(meta, section.getString("Owner"));
        }
        itemStack.setAmount(section.getInt("Amount", 1));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public void setOwner(ItemMeta meta, String name) {
        final SkullMeta itemMeta = (SkullMeta) meta;
        final GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", name));
        Field profileField = null;

        try {
            profileField = itemMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(itemMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            return;
        }

        if (name.length() <= 16) {
            itemMeta.setOwner(name);
        }
    }

    public boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

}