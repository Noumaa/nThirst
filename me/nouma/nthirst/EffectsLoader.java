package me.nouma.nthirst;

import org.bukkit.potion.PotionEffectType;

public class EffectsLoader {

    public static void load() {
        Main.INSTANCE.getConfig().getKeys(true).forEach(key -> {
            for (int i = 0; i <= 20; ++i) {
                if (!Main.INSTANCE.getConfig().contains("effects." + i)) continue;
                Main.INSTANCE.getConfig().getStringList("effects." + i).forEach(s -> {
                    PotionEffectType type = PotionEffectType.getByName(s.split(":")[0]);
                    if (type == null) return;
                    if (Main.INSTANCE.effects.contains(type)) return;
                    Main.INSTANCE.effects.add(type);
                });
            }
        });
    }
}
