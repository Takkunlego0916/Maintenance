package io.github.takkunlego0916.maintenance;

import io.github.takkunlego0916.maintenance.util.DurationUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MaintenanceManager {

    private static final long[] WARNING_MARKS = {3600, 1800, 900, 600, 300, 60, 30, 10, 5, 4, 3, 2, 1};

    private final Maintenance plugin;
    private BukkitTask countdownTask;
    private long scheduledEndMillis;

    public MaintenanceManager(Maintenance plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("maintenance.enabled", false);
    }

    public String getReason() {
        return plugin.getConfig().getString("maintenance.reason", "");
    }

    public boolean isScheduled() {
        return scheduledEndMillis > 0;
    }

    public long getRemainingSeconds() {

        if (scheduledEndMillis <= 0) {
            return -1;
        }

        return Math.max(0, (scheduledEndMillis - System.currentTimeMillis()) / 1000);
    }

    public void resumeAfterStartup() {

        if (!isEnabled()) {
            return;
        }

        long savedEnd = plugin.getConfig().getLong("maintenance.scheduled-end", 0);

        if (savedEnd <= 0) {
            return;
        }

        long remaining = (savedEnd - System.currentTimeMillis()) / 1000;

        if (remaining <= 0) {
            disable();
            return;
        }

        startCountdown(savedEnd, remaining);
    }

    public void enable(String reason, long durationSeconds) {

        cancelCountdown();

        plugin.getConfig().set("maintenance.enabled", true);
        plugin.getConfig().set("maintenance.reason", reason == null ? "" : reason);

        if (durationSeconds > 0) {

            long end = System.currentTimeMillis() + (durationSeconds * 1000);

            plugin.getConfig().set("maintenance.scheduled-end", end);
            plugin.saveConfig();

            startCountdown(end, durationSeconds);

        } else {

            plugin.getConfig().set("maintenance.scheduled-end", 0);
            plugin.saveConfig();
        }

        kickNonBypassedPlayers();
    }

    public void disable() {

        cancelCountdown();

        plugin.getConfig().set("maintenance.enabled", false);
        plugin.getConfig().set("maintenance.reason", "");
        plugin.getConfig().set("maintenance.scheduled-end", 0);
        plugin.saveConfig();
    }

    public void cancelCountdown() {

        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }

        scheduledEndMillis = 0;
    }

    private void kickNonBypassedPlayers() {

        String reason = getReason();
        String path = reason.isBlank() ? "maintenance.kick.default" : "maintenance.kick.reason";
        Map<String, String> placeholders = reason.isBlank() ? Map.of() : Map.of("reason", reason);

        for (Player player : Bukkit.getOnlinePlayers()) {

            if (!player.hasPermission("maintenance.bypass")) {
                player.kick(plugin.getLang().get(player, path, placeholders));
            }
        }
    }

    private void startCountdown(long endMillis, long initialRemainingSeconds) {

        scheduledEndMillis = endMillis;

        Set<Long> pendingWarnings = new LinkedHashSet<>();

        for (long mark : WARNING_MARKS) {
            if (mark <= initialRemainingSeconds) {
                pendingWarnings.add(mark);
            }
        }

        countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            long remaining = getRemainingSeconds();

            if (remaining <= 0) {

                disable();

                if (plugin.getConfig().getBoolean("broadcast.on-disable", true)) {
                    Bukkit.getServer().sendMessage(plugin.getLang().get(Bukkit.getConsoleSender(), "command.disabled.broadcast"));
                }

                return;
            }

            boolean warningsEnabled = plugin.getConfig().getBoolean("broadcast.countdown-warnings", true);

            if (warningsEnabled && pendingWarnings.remove(remaining)) {
                Bukkit.getServer().sendMessage(plugin.getLang().get(Bukkit.getConsoleSender(), "maintenance.countdown", Map.of("time", DurationUtil.format(remaining))));
            }

        }, 20L, 20L);
    }
}
