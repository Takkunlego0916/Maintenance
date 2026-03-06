package io.github.takkunlego0916.maintenance;

import io.github.takkunlego0916.maintenance.lang.LangManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Maintenance extends JavaPlugin {

    private static Maintenance instance;
    private LangManager langManager;

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();

        langManager = new LangManager(this);

        getCommand("maintenance").setExecutor(new MaintenanceCommand(this));

        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new ServerPingListener(this), this);

        getLogger().info("Maintenance Plugin Enabled");
    }

    public static Maintenance getInstance() {
        return instance;
    }

    public LangManager getLang() {
        return langManager;
    }
}