package me.nouma.nthirst.listeners;

import me.nouma.nthirst.Main;
import me.nouma.nthirst.Utils;
import me.nouma.nthirst.api.events.PlayerThirstChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;

public class ThirstListener implements Listener {

    private Main main = Main.INSTANCE;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        main.api.loadPlayer(e.getPlayer());
        main.api.sendGauge(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        main.userdata.getConfig().set(e.getPlayer().getUniqueId().toString(), main.api.getHydration(e.getPlayer()));
        main.userdata.save();
        main.playerHydration.remove(e.getPlayer().getName());
    }

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent e) {
        if (!Utils.isWaterBottle(e.getItem())) {
            // Cancel absorption's effect if, for example, the player ate a gapple
            if (main.getConfig().getString("gauge.place").equalsIgnoreCase("absorption")) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                    e.getPlayer().removePotionEffect(PotionEffectType.ABSORPTION);
                    main.api.sendGauge(e.getPlayer());
                }, 1);
            }
            return;
        }

        int water = main.api.getHydration(e.getPlayer());
        int newWater = Utils.clamp(water + main.getConfig().getInt("rehydrate.points"), 0, 20);

        if (water < 20) main.api.setHydration(e.getPlayer(), newWater);

        // Clear effects TODO call updateEffects' method
        e.getPlayer().getActivePotionEffects().forEach(potionEffect -> e.getPlayer().removePotionEffect(potionEffect.getType()));
    }

    // Make damages doesn't affect absorptions when gauge is here
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!main.getConfig().getString("gauge.place").equalsIgnoreCase("absorption")) return;
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();

        double damage = e.getDamage();
        double health = player.getHealth() - damage;

        // I'm tired
        if (health <= 0) {
            e.setDamage(40);
        } else {
            e.setDamage(0);
            player.setHealth(health);
        }
    }
}
