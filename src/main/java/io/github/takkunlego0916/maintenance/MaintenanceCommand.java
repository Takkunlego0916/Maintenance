package io.github.takkunlego0916.maintenance;

import io.github.takkunlego0916.maintenance.util.DurationUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Map;

public class MaintenanceCommand implements CommandExecutor {

    private final Maintenance plugin;
    private final MaintenanceManager maintenanceManager;

    public MaintenanceCommand(Maintenance plugin, MaintenanceManager maintenanceManager) {
        this.plugin = plugin;
        this.maintenanceManager = maintenanceManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("maintenance.admin")) {
            sender.sendMessage(plugin.getLang().get(sender, "command.no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "on" -> handleOn(sender, args);
            case "off" -> handleOff(sender);
            case "status" -> handleStatus(sender);
            case "reload" -> handleReload(sender);
            case "help" -> sendHelp(sender);
            default -> sender.sendMessage(plugin.getLang().get(sender, "command.unknown-subcommand"));
        }

        return true;
    }

    private void handleOn(CommandSender sender, String[] args) {

        if (maintenanceManager.isEnabled()) {
            sender.sendMessage(plugin.getLang().get(sender, "command.already-enabled"));
            return;
        }

        long duration = -1;
        int reasonStart = 1;

        if (args.length > 1) {

            long parsed = DurationUtil.parseSeconds(args[1]);

            if (parsed > 0) {
                duration = parsed;
                reasonStart = 2;
            }
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, reasonStart, args.length));

        maintenanceManager.enable(reason, duration);

        sender.sendMessage(plugin.getLang().get(sender, "command.enabled.console"));

        if (!plugin.getConfig().getBoolean("broadcast.on-enable", true)) {
            return;
        }

        plugin.getServer().sendMessage(plugin.getLang().get(plugin.getServer().getConsoleSender(), "command.enabled.broadcast"));

        if (!reason.isBlank()) {
            plugin.getServer().sendMessage(plugin.getLang().get(plugin.getServer().getConsoleSender(), "command.enabled.broadcast-reason", Map.of("reason", reason)));
        }

        if (duration > 0) {
            plugin.getServer().sendMessage(plugin.getLang().get(plugin.getServer().getConsoleSender(), "command.enabled.broadcast-scheduled", Map.of("duration", DurationUtil.format(duration))));
        }
    }

    private void handleOff(CommandSender sender) {

        if (!maintenanceManager.isEnabled()) {
            sender.sendMessage(plugin.getLang().get(sender, "command.already-disabled"));
            return;
        }

        maintenanceManager.disable();

        sender.sendMessage(plugin.getLang().get(sender, "command.disabled.console"));

        if (plugin.getConfig().getBoolean("broadcast.on-disable", true)) {
            plugin.getServer().sendMessage(plugin.getLang().get(plugin.getServer().getConsoleSender(), "command.disabled.broadcast"));
        }
    }

    private void handleStatus(CommandSender sender) {

        boolean enabled = maintenanceManager.isEnabled();

        sender.sendMessage(plugin.getLang().get(sender, enabled ? "command.status.enabled" : "command.status.disabled"));

        if (!enabled) {
            return;
        }

        String reason = maintenanceManager.getReason();

        if (!reason.isBlank()) {
            sender.sendMessage(plugin.getLang().get(sender, "command.status.reason", Map.of("reason", reason)));
        }

        if (maintenanceManager.isScheduled()) {
            sender.sendMessage(plugin.getLang().get(sender, "command.status.time-left", Map.of("time", DurationUtil.format(maintenanceManager.getRemainingSeconds()))));
        }
    }

    private void handleReload(CommandSender sender) {
        plugin.reloadConfigWithDefaults();
        plugin.getLang().reload();
        sender.sendMessage(plugin.getLang().get(sender, "command.reload"));
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.getLang().get(sender, "command.help.header"));
        sender.sendMessage(plugin.getLang().get(sender, "command.help.on"));
        sender.sendMessage(plugin.getLang().get(sender, "command.help.off"));
        sender.sendMessage(plugin.getLang().get(sender, "command.help.status"));
        sender.sendMessage(plugin.getLang().get(sender, "command.help.reload"));
        sender.sendMessage(plugin.getLang().get(sender, "command.help.help"));
    }
}
