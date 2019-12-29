package me.nouma.nthirst.schedulers;

import me.nouma.nthirst.Main;
import me.nouma.nthirst.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;

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

            main.api.sendEffects(player);

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
