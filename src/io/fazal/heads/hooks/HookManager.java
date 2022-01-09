package io.fazal.heads.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HookManager {

    private static HookManager instance;

    private final List<Hook> hooks;

    public HookManager() {
        hooks = new ArrayList<>();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI").isEnabled()) {
            PAPIHook papiHook = new PAPIHook();
            papiHook.register();
            hooks.add(papiHook);
        }
    }

    public static HookManager getInstance() {
        if (instance == null) {
            synchronized (HookManager.class) {
                if (instance == null) {
                    instance = new HookManager();
                }
            }
        }
        return instance;
    }

    public String replacePlaceholders(Player player, String message) {
        for (Hook hook : hooks) {
            message = hook.parsePlaceholders(player, message);
        }
        return message;
    }

}