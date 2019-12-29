package me.nouma.nthirst;

import me.nouma.nthirst.api.NthirstApi;
import me.nouma.nthirst.commands.ThirstCommand;
import me.nouma.nthirst.listeners.ThirstListener;
import me.nouma.nthirst.schedulers.DehydrationScheduler;
import me.nouma.nthirst.schedulers.GaugeScheduler;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main extends JavaPlugin implements CommandExecutor {

    public static Main INSTANCE;

    public NthirstApi api;

    public UserdataFile userdata;

    public List<PotionEffectType> effects = new ArrayList<>();

    public GaugeScheduler gaugeScheduler;
    public DehydrationScheduler dehydrationScheduler;

    public HashMap<String, Integer> playerHydration = new HashMap<>();
    public HashMap<String, BossBar> playerBossbar = new HashMap<>();

    @Override
    public void onEnable() {
        INSTANCE = this;

        api = new NthirstApi();

        setupDefaultConfig();
        userdata = new UserdataFile();

        EffectsLoader.load();

        // Dehydration
        dehydrationScheduler = new DehydrationScheduler(getConfig().getInt("dehydration.rate"));
        dehydrationScheduler.start();
        // Gauge
        gaugeScheduler = new GaugeScheduler(getConfig().getInt("gauge.refresh_rate"));
        gaugeScheduler.start();

        getServer().getPluginManager().registerEvents(new ThirstListener(), this);
        getCommand("nthirst").setExecutor(new ThirstCommand());

        // Load players
        Bukkit.getOnlinePlayers().forEach(player -> {
            api.loadPlayer(player);
            api.sendGauge(player);
        });

        Metrics metrics = new Metrics(this);
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(api::savePlayer);
        userdata.save();
    }

    public void setupDefaultConfig() {
        getConfig().addDefault("dehydration.rate", 60);
        getConfig().addDefault("dehydration.points", 1);

        getConfig().addDefault("rehydrate.points", 6);

        // TODO remove absorption when switching
        getConfig().addDefault("gauge.place", "absorption");
        getConfig().addDefault("gauge.refresh_rate", 20);

        getConfig().addDefault("gauge.actionbar.character", "#");
        getConfig().addDefault("gauge.actionbar.color_full", "&b");
        getConfig().addDefault("gauge.actionbar.color_empty", "&7");

        getConfig().addDefault("gauge.bossbar.title", "§bHydration");
        getConfig().addDefault("gauge.bossbar.color", "BLUE");
        getConfig().addDefault("gauge.bossbar.style", "SEGMENTED_20");

        getConfig().addDefault("play_sound.enable", true);
        getConfig().addDefault("play_sound.points", 6);
        getConfig().addDefault("play_sound.sound", "BLOCK_CONDUIT_AMBIENT");

        if (!getConfig().contains("effects")) {
            List<String> effects = new ArrayList<>();
            effects.add("CONFUSION:0");
            getConfig().set("effects.0", effects);
        }

        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public void reload() {
        reloadConfig();
        EffectsLoader.load();
        gaugeScheduler.setUpdateTime(getConfig().getInt("gauge.refresh_rate"));
        dehydrationScheduler.setUpdateTime(getConfig().getInt("dehydration.rate"));
    }
}
