package io.github.takkunlego0916.maintenance.lang;

import io.github.takkunlego0916.maintenance.Maintenance;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LangManager {

    private final Maintenance plugin;
    private final Map<String, YamlConfiguration> languages = new HashMap<>();

    public LangManager(Maintenance plugin) {
        this.plugin = plugin;
        loadLanguages();
    }

    private void loadLanguages() {

        File langFolder = new File(plugin.getDataFolder(), "lang");

        if (!langFolder.exists()) {
            langFolder.mkdirs();
            plugin.saveResource("lang/ja_jp.yml", false);
            plugin.saveResource("lang/en_us.yml", false);
        }

        File[] files = langFolder.listFiles();

        if (files == null) return;

        for (File file : files) {

            if (!file.getName().endsWith(".yml")) continue;

            String langCode = file.getName().replace(".yml", "").toLowerCase();

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            languages.put(langCode, config);

            plugin.getLogger().info("Loaded language: " + langCode);
        }
    }

    public String get(Player player, String path) {

        String locale = player.getLocale().toLowerCase();

        if (!languages.containsKey(locale)) {
            String shortLocale = locale.split("_")[0];

            for (String lang : languages.keySet()) {

                if (lang.startsWith(shortLocale)) {
                    locale = lang;
                    break;
                }

            }
        }

        if (!languages.containsKey(locale)) {
            locale = plugin.getConfig().getString("default-language", "en_us");
        }

        YamlConfiguration lang = languages.get(locale);

        String message = lang.getString(path);

        if (message == null) {
            return "Missing message: " + path;
        }

        return message.replace("&", "§");
    }
}
