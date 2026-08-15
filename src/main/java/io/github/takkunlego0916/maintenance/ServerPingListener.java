package io.github.takkunlego0916.maintenance;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerPingListener implements Listener {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    private final Maintenance plugin;
    private final MaintenanceManager maintenanceManager;

    public ServerPingListener(Maintenance plugin, MaintenanceManager maintenanceManager) {
        this.plugin = plugin;
        this.maintenanceManager = maintenanceManager;
    }

    @EventHandler
    public void onPing(PaperServerListPingEvent event) {

        boolean maintenance = maintenanceManager.isEnabled();
        String path = maintenance ? "motd.maintenance" : "motd.normal";

        String line1 = plugin.getConfig().getString(path + ".line1", "");
        String line2 = plugin.getConfig().getString(path + ".line2", "");

        line2 = line2.replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%max%", String.valueOf(Bukkit.getMaxPlayers()));

        Component motd = SERIALIZER.deserialize(line1 + "\n" + line2);
        event.motd(motd);

        if (!maintenance) {
            return;
        }

        if (plugin.getConfig().getBoolean("motd.hide-player-count", true)) {
            event.setHidePlayers(true);
        }

        String versionText = plugin.getConfig().getString("motd.version-text", "");

        if (!versionText.isBlank()) {
            event.setProtocolVersion(-1);
            event.setVersion(LegacyComponentSerializer.legacySection().serialize(SERIALIZER.deserialize(versionText)));
        }
    }
}
