package io.github.takkunlego0916.maintenance;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerPingListener implements Listener {

    private final Maintenance plugin;

    public ServerPingListener(Maintenance plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPing(ServerListPingEvent event) {

        boolean maintenance = plugin.getConfig().getBoolean("maintenance");

        String line1 = plugin.getConfig().getString("motd.line1");
        String line2;

        if (maintenance) {
            line2 = plugin.getConfig().getString("motd.maintenance-line2");
        } else {
            line2 = plugin.getConfig().getString("motd.line2");
        }

        event.setMotd(color(line1) + "\n" + color(line2));
    }

    private String color(String text) {
        return text.replace("&", "§");
    }
}