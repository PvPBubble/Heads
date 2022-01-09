package io.fazal.heads.tokenshop;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TokenshopItem {

    private int slot;
    private int price;
    private List<String> commands;
    private ItemStack item;
    private String name;

    public TokenshopItem(int slot, int price, List<String> commands, ItemStack item, String name) {
        this.slot = slot;
        this.price = price;
        this.commands = commands;
        this.item = item;
        this.name = name;
    }

    public void executeCommands(Player player) {
        commands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName())));
    }

    public int getSlot() {
        return slot;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public List<String> getCommands() {
        return commands;
    }

    public ItemStack getItem() {
        return item;
    }

}