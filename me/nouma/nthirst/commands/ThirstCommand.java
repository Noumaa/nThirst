package me.nouma.nthirst.commands;

import me.nouma.nthirst.Main;
import me.nouma.nthirst.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ThirstCommand implements CommandExecutor {

    private Main main = Main.INSTANCE;

    private final String prefix = "§bnThirst §8» §r";
    private final String noPerm = prefix + "§cYou don't have the permission to do this!";
    private final String noPlayer = prefix + "§cOnly players can do this!";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                if (!sender.hasPermission("nthirst.reload")) {
                    sender.sendMessage(noPerm);
                    return true;
                }
                main.reload();
                sender.sendMessage(prefix + "§aPlugin successfully reloaded!");
                return true;
            } else if (args[0].equalsIgnoreCase("debug")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(noPlayer);
                    return true;
                }
                if (!sender.hasPermission("nthirst.debug")) {
                    sender.sendMessage(noPerm);
                    return true;
                }
                main.api.sendGauge((Player) sender);
                sender.sendMessage(prefix + "§aHydration gauge successfully updated!");
                return true;
            } else if (args[0].equalsIgnoreCase("set")) {
                if (args.length == 2) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(noPlayer);
                        return true;
                    }
                    if (!sender.hasPermission("nthirst.set.yourself")) {
                        sender.sendMessage(noPerm);
                        return true;
                    }
                    int i;
                    try {
                        i = Integer.parseInt(args[1]);
                    } catch (Exception e) {
                        sender.sendMessage(prefix + "§cIncorrect number!");
                        return true;
                    }
                    main.api.setHydration((Player) sender, i);
                    sender.sendMessage(prefix + "§aSuccessfully changed your hydration level to §e" + Utils.clamp(i, 0, 20) + "§a!");
                    return true;
                } else if (args.length == 3) {
                    if (!sender.hasPermission("nthirst.set.others")) {
                        sender.sendMessage(noPerm);
                        return true;
                    }
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(prefix + "§cPlayer not found!");
                        return true;
                    }
                    int i;
                    try {
                        i = Integer.parseInt(args[2]);
                    } catch (Exception e) {
                        sender.sendMessage(prefix + "§cIncorrect number!");
                        return true;
                    }
                    main.api.setHydration(target, i);
                    sender.sendMessage(prefix + "§aSuccessfully changed §e" + target.getName() + "§a's hydration level to §e" + Utils.clamp(i, 0, 20) + "§a!");
                    return true;
                } else {
                    sender.sendMessage("§8Correct usage: §7/nthirst set §c<number> §8or §7/nthirst set §c<player> <number>");
                    return true;
                }
            }
        }

        sender.sendMessage(" §8§m          §b nThirst §7v" + main.getDescription().getVersion() + " §8§m          ");
        sender.sendMessage("§7/nthirst §freload");
        sender.sendMessage("§7/nthirst §fset §8<player/number> [number]");
        sender.sendMessage("§7/nthirst §fdebug");
        sender.sendMessage("");
        return true;
    }
}
