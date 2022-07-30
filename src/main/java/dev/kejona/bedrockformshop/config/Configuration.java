package dev.kejona.bedrockformshop.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public class Configuration {
    private static FileConfiguration config;

    public Configuration(Plugin plugin) {
        createFiles(plugin);
    }
    /**
     * Load config or create.
     */
    public void createFiles(Plugin plugin) {
        File configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource("config.yml", false);
        }
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static String getMessages(String message) {
        return config.getString("messages." + message);
    }

    public static Set<String> getButtons(String menuID) {
        return Objects.requireNonNull(config.getConfigurationSection("form." + menuID + ".buttons")).getKeys(false);
    }

    public static ConfigurationSection getButtonData(String menuID, String buttonID) {
        return config.getConfigurationSection("form." + menuID + ".buttons." + buttonID);
    }

    public static ConfigurationSection getMenuData(String menuID) {
        return config.getConfigurationSection("form." + menuID);
    }
}
