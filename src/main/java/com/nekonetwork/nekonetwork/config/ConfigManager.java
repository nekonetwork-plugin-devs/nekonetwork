package com.nekonetwork.nekonetwork.config;

import com.nekonetwork.nekonetwork.NekoNetwork;
import com.nekonetwork.nekonetwork.helpers.NekoModule;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.*;

public class ConfigManager {

    public NekoNetwork plugin;
    public File configFile;
    public FileConfiguration config;

    public ConfigManager(NekoNetwork plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource("config.yml", false);
        }

        config = new YamlConfiguration();
        try {
            config.load(configFile);

            for(NekoModule module : plugin.modules.values()) {
                module.loadConfig(config);
            }
        } catch (NullPointerException | IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void editConfig() {
        try {
            for(NekoModule module : plugin.modules.values()) {
                module.editConfig(config);
            }

            config.save(configFile);
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
    }
}
