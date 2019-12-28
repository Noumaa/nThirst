package me.nouma.nthirst.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PlayerThirstChangeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private int hydration, oldHydration;
    private boolean isCancelled;

    public PlayerThirstChangeEvent(Player player, int hydration, int oldHydration) {
        this.player = player;
        this.hydration = hydration;
        this.oldHydration = oldHydration;
    }

    public Player getPlayer() {
        return player;
    }

    public int getHydration() {
        return hydration;
    }

    public int getOldHydration() {
        return oldHydration;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        isCancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
