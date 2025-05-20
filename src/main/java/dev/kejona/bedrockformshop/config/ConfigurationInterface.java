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
     * @return set of buttons from config
     */
    Set<String> getButtons(String menuID);

    /**
     * Get list of shops.
     *
     * @return set of shops from config
     */
    Set<String> getShops();

    /**
     * Get data from button yaml block
     * @param menuID set menuID path
     * @param buttonID set buttonID path
     * @return yaml block section
     */
    ConfigurationSection itemData(String menuID, String buttonID);

    ConfigurationSection shopData(String buttonID);

    /**
     * Get a list of command overrides from config
     */
    ConfigurationSection getCommandOverrides();

    /**
     * enables debug logger.
     */
    boolean isDebug();
}
