package io.github.takkunlego0916.maintenance.lang;

import io.github.takkunlego0916.maintenance.Maintenance;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LangManager {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    private final Maintenance plugin;
    private final Map<String, YamlConfiguration> languages = new HashMap<>();

    public LangManager(Maintenance plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {

        languages.clear();

        File langFolder = new File(plugin.getDataFolder(), "lang");

        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        if (!new File(langFolder, "en_us.yml").exists()) {
            plugin.saveResource("lang/en_us.yml", false);
        }

        if (!new File(langFolder, "ja_jp.yml").exists()) {
            plugin.saveResource("lang/ja_jp.yml", false);
        }

        File[] files = langFolder.listFiles((dir, name) -> name.endsWith(".yml"));

        if (files == null) {
            return;
        }

        for (File file : files) {
            String langCode = file.getName().substring(0, file.getName().length() - 4).toLowerCase();
            languages.put(langCode, YamlConfiguration.loadConfiguration(file));
        }
    }

    private String resolveLocale(String requested) {

        if (languages.containsKey(requested)) {
            return requested;
        }

        String shortLocale = requested.split("_")[0];

        for (String lang : languages.keySet()) {
            if (lang.startsWith(shortLocale)) {
                return lang;
            }
        }

        String fallback = plugin.getConfig().getString("default-language", "en_us");

        if (languages.containsKey(fallback)) {
            return fallback;
        }

        return languages.isEmpty() ? null : languages.keySet().iterator().next();
    }

    private String raw(String locale, String path) {

        YamlConfiguration lang = locale == null ? null : languages.get(locale);

        if (lang == null || !lang.isString(path)) {
            return "Missing message: " + path;
        }

        return lang.getString(path);
    }

    public Component get(CommandSender sender, String path) {
        return get(sender, path, Map.of());
    }

    public Component get(CommandSender sender, String path, Map<String, String> placeholders) {

        String requestedLocale = sender instanceof Player player
                ? player.locale().toString().toLowerCase()
                : plugin.getConfig().getString("default-language", "en_us");

        String locale = resolveLocale(requestedLocale);
        String message = raw(locale, path);

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("%" + entry.getKey() + "%", entry.getValue());
        }

        return SERIALIZER.deserialize(message);
    }
}
