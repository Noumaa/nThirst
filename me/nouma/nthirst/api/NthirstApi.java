package me.nouma.nthirst.api;

import me.nouma.nthirst.Main;
import me.nouma.nthirst.Utils;
import me.nouma.nthirst.api.events.PlayerThirstChangeEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NthirstApi {

    private Main main = Main.INSTANCE;

    public int getHydration(Player player) {
        if (!main.playerHydration.containsKey(player.getName())) loadPlayer(player);
        return main.playerHydration.get(player.getName());
    }

    public void setHydration(Player player, int i) {
        PlayerThirstChangeEvent e = new PlayerThirstChangeEvent(player, i, getHydration(player));
        Bukkit.getPluginManager().callEvent(e);
        if (e.isCancelled()) return;

        main.playerHydration.put(player.getName(), Utils.clamp(i, 0, 20));
        sendEffects(player);
        sendGauge(player);
    }

    public void loadPlayer(Player player) {
        if (!main.userdata.getConfig().contains(player.getUniqueId().toString())) {
            main.playerHydration.put(player.getName(), 20);
        } else {
            main.playerHydration.put(player.getName(), main.userdata.getConfig().getInt(player.getUniqueId().toString()));
        }
    }

    public void savePlayer(Player player) {
        main.userdata.getConfig().set(player.getUniqueId().toString(), main.playerHydration.get(player.getName()));
    }

    public void sendGauge(Player player) {
        if (main.getConfig().getString("gauge.place").equalsIgnoreCase("absorption")) {
            player.setAbsorptionAmount(getHydration(player));
        } else if (main.getConfig().getString("gauge.place").equalsIgnoreCase("bossbar")) {
            // Grabbing bossbar custom values
            String title = main.getConfig().getString("gauge.bossbar.title");
            BarColor color;
            try {
                color = BarColor.valueOf(main.getConfig().getString("gauge.bossbar.color"));
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().severe(String.format("[%s] Bossbar color " + main.getConfig().getString("gauge.bossbar.color") + " is incorrect!"));
                color = BarColor.BLUE;
            }
            BarStyle style;
            try {
                style = BarStyle.valueOf(main.getConfig().getString("gauge.bossbar.style"));
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().severe(String.format("[%s] Bossbar style " + main.getConfig().getString("gauge.bossbar.style") + " is incorrect!"));
                style = BarStyle.SEGMENTED_20;
            }

            // Creating the bossbar
            BossBar bossBar = Bukkit.createBossBar(title, color, style);

            if (main.playerBossbar.containsKey(player.getName())) bossBar = main.playerBossbar.get(player.getName()); // Check if it already have been created
            if (!bossBar.getPlayers().contains(player)) bossBar.addPlayer(player);
            if (bossBar.getTitle() != title) bossBar.setTitle(title); // Update bossbar in case of /nthirst reload
            if (bossBar.getColor() != color) bossBar.setColor(color); //
            if (bossBar.getStyle() != style) bossBar.setStyle(style); //
            if (!bossBar.isVisible()) bossBar.setVisible(true);
            bossBar.setProgress(((double) getHydration(player) * 5) / 100);
            main.playerBossbar.put(player.getName(), bossBar);
        } else {
            // Default is "actionbar"
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.getGauge(main.api.getHydration(player))));
        }
    }

    public void sendEffects(Player player) {
        Main.INSTANCE.effects.forEach(player::removePotionEffect);
        int duration = main.getConfig().getInt("dehydration.rate")*20;
        for (int i = 20; getHydration(player) <= i; --i) {
            if (main.getConfig().contains("effects." + i)) {
                main.getConfig().getStringList("effects." + i).forEach(effect -> {
                    PotionEffectType type;
                    try {
                        type = PotionEffectType.getByName(effect.split(":")[0]);
                    } catch (IllegalArgumentException e) {
                        Bukkit.getLogger().warning(String.format("[%s] Potion effect type " + effect.split(":")[0] + " doesn't exist!", main.getDescription().getName()));
                        return;
                    }
                    int amplifier;
                    try {
                        amplifier = Integer.parseInt(effect.split(":")[1]);
                    } catch (NumberFormatException e) {
                        Bukkit.getLogger().warning(String.format("[%s] Number " + effect.split(":")[1] + "'s incorrect!", main.getDescription().getName()));
                        return;
                    }
                    player.addPotionEffect(new PotionEffect(type, duration, amplifier, false, false, false));
                });
            }
        }
    }
}
