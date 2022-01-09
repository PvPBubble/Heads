package io.fazal.heads.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class TokenGainEvent extends PlayerEvent {

    private static final HandlerList handlers;
    private int amount;

    static {
        handlers = new HandlerList();
    }

    public TokenGainEvent(Player player, int amount) {
        super(player);
        this.amount = amount;
    }

    public HandlerList getHandlers() {
        return TokenGainEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return TokenGainEvent.handlers;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

}