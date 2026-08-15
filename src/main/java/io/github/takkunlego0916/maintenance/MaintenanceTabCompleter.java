package io.github.takkunlego0916.maintenance;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class MaintenanceTabCompleter implements TabCompleter {

    private static final List<String> SUBCOMMANDS = List.of("on", "off", "status", "reload", "help");
    private static final List<String> DURATIONS = List.of("10m", "30m", "1h", "6h", "1d");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (!sender.hasPermission("maintenance.admin")) {
            return List.of();
        }

        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {

            for (String subcommand : SUBCOMMANDS) {
                if (subcommand.startsWith(args[0].toLowerCase())) {
                    suggestions.add(subcommand);
                }
            }

        } else if (args.length == 2 && args[0].equalsIgnoreCase("on")) {

            for (String duration : DURATIONS) {
                if (duration.startsWith(args[1].toLowerCase())) {
                    suggestions.add(duration);
                }
            }
        }

        return suggestions;
    }
}
