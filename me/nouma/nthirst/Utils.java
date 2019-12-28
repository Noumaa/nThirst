package me.nouma.nthirst;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class Utils {

    private static Main main = Main.INSTANCE;

    public static boolean isWaterBottle(ItemStack item) {
        ItemStack waterBottle = new ItemStack(Material.POTION);
        PotionMeta waterBottleMeta = (PotionMeta) waterBottle.getItemMeta();
        waterBottleMeta.setBasePotionData(new PotionData(PotionType.WATER));
        waterBottle.setItemMeta(waterBottleMeta);
        return item.equals(waterBottle);
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static String getGauge(int water) {
        StringBuilder gauge = new StringBuilder(main.getConfig().getString("gauge.actionbar.color_full").replaceAll("&", "§"));
        for (int i = 0; i < water; i++) gauge.append(main.getConfig().getString("gauge.actionbar.character"));
        gauge.append(main.getConfig().getString("gauge.actionbar.color_empty").replaceAll("&", "§"));
        for (int i = water; i < 20; i++) gauge.append(main.getConfig().getString("gauge.actionbar.character"));
        return gauge.toString();
    }
}
