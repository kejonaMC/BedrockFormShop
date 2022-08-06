package dev.kejona.bedrockformshop.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;

public interface ConfigurationInterface {

    int getVersion();

    String getMessages(String message);

    Set<String> getButtons(String menuID);

    ConfigurationSection getButtonData(String menuID, String buttonID);

    ConfigurationSection getMenuData(String menuID);

    ConfigurationSection getCommandOverrides();
}
