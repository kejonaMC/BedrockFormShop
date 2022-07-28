package dev.kejona.bedrockformshop.config;

import dev.kejona.bedrockformshop.BedrockFormShop;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;
import java.util.Set;

public class Configuration {

    public static FileConfiguration config = BedrockFormShop.getInstance().getConfig();

    public static String getMessages(String data) {
        return config.getString("messages." + data);
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

    public static String getButtonData(String menuID, String button, String data) {
        return config.getString("form." + menuID + ".buttons." + button + "." + data);
    }
}
