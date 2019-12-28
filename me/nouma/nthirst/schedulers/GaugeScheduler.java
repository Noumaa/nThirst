package me.nouma.nthirst.schedulers;

import me.nouma.nthirst.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;

public class GaugeScheduler {

    private static Main main = Main.INSTANCE;
    private int id = -1;
    private int updateTime;

    public GaugeScheduler(int updateTime) {
        this.updateTime = updateTime;
    }

    public void setUpdateTime(int i) {
        updateTime = i;
        start();
    }

    public void start() {
        if (id != -1) stop();
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.getGameMode() != GameMode.CREATIVE) main.api.sendGauge(player);
        }), 5*20, updateTime*20);
    }

    public void stop() {
        if (id != -1) Bukkit.getScheduler().cancelTask(id);
    }
}
