package io.github.takkunlego0916.maintenance;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MaintenanceCommand implements CommandExecutor {

    private final Maintenance plugin;

    public MaintenanceCommand(Maintenance plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("maintenance.admin")) {
            sender.sendMessage("§cNo permission");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("/maintenance <on|off|status>");
            return true;
        }

        if (args[0].equalsIgnoreCase("on")) {

            plugin.getConfig().set("maintenance", true);
            plugin.saveConfig();

            for (Player player : Bukkit.getOnlinePlayers()) {

                if (!player.hasPermission("maintenance.admin")) {
                    player.kickPlayer("§cServer under maintenance");
                }

            }

            Bukkit.broadcastMessage("§cMaintenance enabled");
        }

        if (args[0].equalsIgnoreCase("off")) {

            plugin.getConfig().set("maintenance", false);
            plugin.saveConfig();

            Bukkit.broadcastMessage("§aMaintenance disabled");
        }

        if (args[0].equalsIgnoreCase("status")) {

            boolean maintenance = plugin.getConfig().getBoolean("maintenance");

            if (maintenance) {
                sender.sendMessage("§cMaintenance is enabled");
            } else {
                sender.sendMessage("§aMaintenance is disabled");
            }

        }

        return true;
    }
}