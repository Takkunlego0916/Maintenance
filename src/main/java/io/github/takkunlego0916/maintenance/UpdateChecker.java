package io.github.takkunlego0916.maintenance;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateChecker implements Listener {

    private static final Pattern VERSION_PATTERN = Pattern.compile("\"version_number\"\\s*:\\s*\"([^\"]+)\"");

    private final Maintenance plugin;
    private volatile String latestVersion;

    public UpdateChecker(Maintenance plugin) {
        this.plugin = plugin;
    }

    public void check() {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            try {

                String projectId = plugin.getConfig().getString("update-checker.modrinth-id", "maintenance");
                String currentVersion = plugin.getPluginMeta().getVersion();

                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(5))
                        .build();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.modrinth.com/v2/project/" + projectId + "/version"))
                        .header("User-Agent", "takkunlego0916/Maintenance/" + currentVersion)
                        .timeout(Duration.ofSeconds(5))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    return;
                }

                Matcher matcher = VERSION_PATTERN.matcher(response.body());

                if (!matcher.find()) {
                    return;
                }

                String remoteVersion = matcher.group(1);

                if (!remoteVersion.equalsIgnoreCase(currentVersion)) {
                    latestVersion = remoteVersion;
                    plugin.getLogger().info("A new version of Maintenance is available: " + remoteVersion + " (running " + currentVersion + ")");
                }

            } catch (Exception exception) {
                plugin.getLogger().fine("Update check skipped: " + exception.getMessage());
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        if (latestVersion == null) {
            return;
        }

        if (!plugin.getConfig().getBoolean("update-checker.notify-ops-on-join", true)) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.isOp()) {
            return;
        }

        player.sendMessage(plugin.getLang().get(player, "update.available", Map.of("version", latestVersion)));
    }
}
