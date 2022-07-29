package dev.kejona.bedrockformshop.config;

import dev.kejona.bedrockformshop.BedrockFormShop;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public class Configuration {

    public static FileConfiguration config = BedrockFormShop.getInstance().getConfig();

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
