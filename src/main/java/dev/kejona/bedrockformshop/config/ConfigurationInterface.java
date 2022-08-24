package dev.kejona.bedrockformshop.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;

public interface ConfigurationInterface {

    /**
     * Get config version
     */
    int getVersion();

    /**
     * Get messages
     * @param message path to the message
     * @return message from config
     */
    String getMessages(String message);

    /**
     * Get list of buttons.
     * @param menuID path to the buttons
     * @return list of buttons from config
     */
    Set<String> getButtons(String menuID);

    /**
     * Get data from button yaml block
     * @param menuID set menuID path
     * @param buttonID set buttonID path
     * @return yaml block section
     */
    ConfigurationSection getButtonData(String menuID, String buttonID);

    /**
     * Get data from menu yaml block
     * @param menuID set menuID path
     * @return yaml block section
     */
    ConfigurationSection getMenuData(String menuID);

    /**
     * Get a list of command overrides from config
     */
    ConfigurationSection getCommandOverrides();
}
