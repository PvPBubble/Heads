package io.fazal.heads;

import com.earth2me.essentials.Essentials;
import io.fazal.heads.command.CommandManager;
import io.fazal.heads.commands.HeadsCommand;
import io.fazal.heads.commands.TokenshopCommand;
import io.fazal.heads.hooks.HookManager;
import io.fazal.heads.menus.MenuAPI;
import io.fazal.heads.task.SaveTask;
import io.fazal.heads.token.TokenManager;
import io.fazal.heads.token.Tokens;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin {

    private static Main instance;
    private File tokensDBf;
    private YamlConfiguration tokensDB;
    private Essentials ess;
    private TokenManager tokenManager;
    private SaveTask saveThread;

    public static Main getInstance() {
        return instance;
    }

    public void onEnable() {
        log("§a[Heads] Attempting to execute start procedure...");
        saveDefaultConfig();
        instance = this;
        setupTokensDB();
        tokenManager = new TokenManager();
        Tokens.getInstance();
        HookManager.getInstance();
        loadListeners(MenuAPI.getInstance());
        getCommand("tokens").setExecutor(new CommandManager());
        getCommand("heads").setExecutor(new HeadsCommand());
        getCommand("tokenshop").setExecutor(new TokenshopCommand());
        ess = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
        saveThread = new SaveTask();
        saveThread.start();
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> tokenManager.dumpToRAM(), 20L);
        log("§a[Heads] Plugin successfully started...");
    }

    public void onDisable() {
        log("§a[Heads] Attempting to shut down...");
        tokenManager.shutdown();
        Bukkit.getScheduler().cancelTasks(this);
        if (saveThread != null) {
            saveThread.stop();
            saveThread = null;
        }
        log("§a[Heads] Plugin successfully shut down...");
    }

    private void setupTokensDB() {
        tokensDBf = new File(getDataFolder(), "tokensDB.yml");
        if (!tokensDBf.exists()) {
            tokensDBf.getParentFile().mkdirs();
            saveResource("tokensDB.yml", false);
        }
        tokensDB = new YamlConfiguration();
        try {
            tokensDB.load(tokensDBf);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getTokensDB() {
        return tokensDB;
    }

    public void reloadTokensDB() {
        YamlConfiguration.loadConfiguration(tokensDBf);
    }

    public void saveTokensDB() {
        if (tokensDB == null || tokensDBf == null) {
            return;
        }
        try {
            tokensDB.save(tokensDBf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public Essentials getEss() {
        return ess;
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }

}