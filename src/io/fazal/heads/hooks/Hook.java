package io.fazal.heads.hooks;

import org.bukkit.entity.Player;

public interface Hook {

    String parsePlaceholders(Player player, String message);

}
