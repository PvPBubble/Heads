package io.fazal.heads.token;

import io.fazal.heads.Main;
import io.fazal.heads.events.TokenGainEvent;
import io.fazal.heads.utils.Utils;
import io.fazal.heads.utils.set.ObjectSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class TokenManager implements Listener {

    private Map<UUID, Integer> tokensDB;

    public TokenManager() {
        System.out.print("[Heads] Registered Token System");
        tokensDB = new HashMap<>();
    }

    public void dumpToFile() {
        if (tokensDB == null || tokensDB.isEmpty()) return;
        for (UUID uuid : tokensDB.keySet()) {
            Main.getInstance().getTokensDB().set("data." + uuid.toString(), tokensDB.get(uuid));
        }
        Main.getInstance().saveTokensDB();
        Main.getInstance().reloadTokensDB();
    }

    public void dumpToRAM() {
        if (Main.getInstance().getTokensDB().getConfigurationSection("data") == null || !Main.getInstance().getTokensDB().isConfigurationSection("data"))
            return;
        for (String uuid : Main.getInstance().getTokensDB().getConfigurationSection("data").getKeys(false)) {
            tokensDB.put(UUID.fromString(uuid), Main.getInstance().getTokensDB().getInt("data." + uuid));
            Main.getInstance().getTokensDB().set("data." + uuid, 0);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!tokensDB.containsKey(player.getUniqueId())) {
                tokensDB.put(player.getUniqueId(), 0);
            }
        }
    }

    public void shutdown() {
        dumpToFile();
        tokensDB = null;
        System.out.print("[Heads] Shutting down TokenManager and dumping data to file!");
    }

    public void set(UUID uuid, int amount) {
        tokensDB.remove(uuid);
        tokensDB.put(uuid, amount);
    }

    public void give(UUID uuid, int amount, boolean callEvent, boolean message) {
        int currentAmount;
        if (tokensDB.containsKey(uuid)) {
            currentAmount = tokensDB.get(uuid);
            tokensDB.remove(uuid);
        } else currentAmount = 0;
        currentAmount += amount;
        tokensDB.put(uuid, currentAmount);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && message) {
            Utils.getInstance().sendMessage(player, "GAIN", new ObjectSet("%amount%", NumberFormat.getInstance(Locale.US).format(amount)));
        }
        if (callEvent) {
            TokenGainEvent gainEvent = new TokenGainEvent(Bukkit.getPlayer(uuid), amount);
            Bukkit.getPluginManager().callEvent(gainEvent);
        }
    }

    public void remove(UUID uuid, int amount) {
        int currentAmount;
        if (tokensDB.containsKey(uuid)) {
            currentAmount = tokensDB.get(uuid);
            tokensDB.remove(uuid);
        } else currentAmount = 0;
        currentAmount -= amount;
        if (currentAmount < 0) {
            currentAmount = 0;
        }
        tokensDB.put(uuid, currentAmount);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            Utils.getInstance().sendMessage(player, "LOSS", new ObjectSet("%amount%", NumberFormat.getInstance(Locale.US).format(amount)));
        }
    }

    public int get(UUID uuid) {
        return tokensDB.getOrDefault(uuid, 0);
    }

    public boolean hasBalance(UUID uuid, int amount) {
        return get(uuid) >= amount;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!tokensDB.containsKey(e.getPlayer().getUniqueId())) {
            tokensDB.put(e.getPlayer().getUniqueId(), 0);
        }
    }

}
