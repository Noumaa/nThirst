package me.nouma.nthirst.schedulers;

import me.nouma.nthirst.Main;
import me.nouma.nthirst.Utils;
import me.nouma.nthirst.api.events.PlayerThirstChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DehydrationScheduler {

    private Main main = Main.INSTANCE;
    private int id = -1;
    private int updateTime;

    public DehydrationScheduler(int updateTime) {
        this.updateTime = updateTime;
    }

    public void setUpdateTime(int i) {
        updateTime = i;
        start();
    }

    public void start() {
        if (id != -1) stop();
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.getGameMode() == GameMode.CREATIVE) return;

            int water = main.api.getHydration(player);
            int newWater = Utils.clamp(water - main.getConfig().getInt("dehydration.points"), 0, 20);

            if (water > 0) main.api.setHydration(player, newWater);

            // Apply effects TODO extract this to a sendEffects' method
            int duration = main.getConfig().getInt("dehydration.rate")*20;
            for (int i = 20; newWater < i; --i) {
                if (main.getConfig().contains("effects." + i)) main.getConfig().getStringList("effects." + i).forEach(effect -> player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(effect.split(":")[0]), duration, Integer.parseInt(effect.split(":")[1]), false, false, false)));
            }

            // Send alert sound
            Sound sound;
            try {
                sound = Sound.valueOf(main.getConfig().getString("play_sound.sound"));
            } catch (IllegalArgumentException e) {
                // Default sound if there's an error
                sound = Sound.BLOCK_NOTE_BLOCK_BELL;
            }
            if (main.getConfig().getBoolean("play_sound.enable") && main.api.getHydration(player) <= main.getConfig().getInt("play_sound.points")) player.playSound(player.getLocation(), sound, 1, 1);
        }), 5*20, updateTime*20);
    }

    public void stop() {
        if (id != -1) Bukkit.getScheduler().cancelTask(id);
    }
}
