package io.github.takkunlego0916.maintenance;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Map;

public class JoinListener implements Listener {

    private final Maintenance plugin;
    private final MaintenanceManager maintenanceManager;

    public JoinListener(Maintenance plugin, MaintenanceManager maintenanceManager) {
        this.plugin = plugin;
        this.maintenanceManager = maintenanceManager;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLogin(PlayerLoginEvent event) {

        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }

        if (!maintenanceManager.isEnabled()) {
            return;
        }

        if (event.getPlayer().hasPermission("maintenance.bypass")) {
            return;
        }

        String reason = maintenanceManager.getReason();
        String path = reason.isBlank() ? "maintenance.kick.default" : "maintenance.kick.reason";
        Map<String, String> placeholders = reason.isBlank() ? Map.of() : Map.of("reason", reason);

        Component message = plugin.getLang().get(event.getPlayer(), path, placeholders);

        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, message);
    }
}
