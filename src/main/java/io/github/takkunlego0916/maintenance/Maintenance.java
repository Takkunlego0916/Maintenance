package io.github.takkunlego0916.maintenance;

import io.github.takkunlego0916.maintenance.lang.LangManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Maintenance extends JavaPlugin {

    private static Maintenance instance;

    private LangManager langManager;
    private MaintenanceManager maintenanceManager;

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();
        reloadConfigWithDefaults();

        langManager = new LangManager(this);
        maintenanceManager = new MaintenanceManager(this);
        maintenanceManager.resumeAfterStartup();

        getCommand("maintenance").setExecutor(new MaintenanceCommand(this, maintenanceManager));
        getCommand("maintenance").setTabCompleter(new MaintenanceTabCompleter());

        getServer().getPluginManager().registerEvents(new JoinListener(this, maintenanceManager), this);
        getServer().getPluginManager().registerEvents(new ServerPingListener(this, maintenanceManager), this);

        if (getConfig().getBoolean("update-checker.enabled", true)) {
            UpdateChecker updateChecker = new UpdateChecker(this);
            getServer().getPluginManager().registerEvents(updateChecker, this);
            updateChecker.check();
        }

        getLogger().info("Maintenance has been enabled.");
    }

    @Override
    public void onDisable() {

        if (maintenanceManager != null) {
            maintenanceManager.cancelCountdown();
        }

        getLogger().info("Maintenance has been disabled.");
    }

    public void reloadConfigWithDefaults() {

        reloadConfig();

        try (InputStreamReader reader = new InputStreamReader(getResource("config.yml"), StandardCharsets.UTF_8)) {

            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(reader);

            getConfig().setDefaults(defaults);
            getConfig().options().copyDefaults(true);
            saveConfig();

        } catch (Exception exception) {
            getLogger().warning("Failed to merge default configuration values: " + exception.getMessage());
        }
    }

    public static Maintenance getInstance() {
        return instance;
    }

    public LangManager getLang() {
        return langManager;
    }

    public MaintenanceManager getMaintenanceManager() {
        return maintenanceManager;
    }
}
