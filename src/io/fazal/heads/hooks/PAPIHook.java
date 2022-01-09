package io.fazal.heads.hooks;

import io.fazal.heads.Main;
import io.fazal.heads.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.text.NumberFormat;

public class PAPIHook extends PlaceholderExpansion implements Hook {

    @Override
    public String onPlaceholderRequest(Player player, String subplaceholder) {
        if (subplaceholder.equalsIgnoreCase("balance")) {
            return NumberFormat.getInstance().format(Main.getInstance().getTokenManager().get(player.getUniqueId()));
        } else if (subplaceholder.equalsIgnoreCase("balance_formatted")) {
            return Utils.getInstance().formatNumber(Main.getInstance().getTokenManager().get(player.getUniqueId()));
        }
        return "&cThe placeholder was not found.";
    }

    @Override
    public String parsePlaceholders(Player player, String message) {
        return PlaceholderAPI.setPlaceholders(player, message);
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return Main.getInstance().getDescription().getAuthors().get(0);
    }

    @Override
    public String getIdentifier() {
        return "heads";
    }

    @Override
    public String getVersion() {
        return Main.getInstance().getDescription().getVersion();
    }

}