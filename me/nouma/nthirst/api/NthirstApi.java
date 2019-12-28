package me.nouma.nthirst.api;

import me.nouma.nthirst.Main;
import me.nouma.nthirst.Utils;
import me.nouma.nthirst.api.events.PlayerThirstChangeEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;

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
        } else {
            // Default is "actionbar"
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.getGauge(main.api.getHydration(player))));
        }
    }
}
