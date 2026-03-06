package io.github.takkunlego0916.maintenance;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class JoinListener implements Listener {

    private final Maintenance plugin;

    public JoinListener(Maintenance plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerLoginEvent event) {

        boolean maintenance = plugin.getConfig().getBoolean("maintenance");

        if (maintenance) {

            if (!event.getPlayer().hasPermission("maintenance.admin")) {

                String msg = plugin.getLang().get(event.getPlayer(), "maintenance.kick");

                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, msg);
            }
        }
    }
}