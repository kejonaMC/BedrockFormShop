package dev.kejona.bedrockformshop.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;
import java.util.Set;

public class ConfigurationHandler implements ConfigurationInterface {

    private final FileConfiguration config;

    public ConfigurationHandler(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public String getMessages(String message) {
        return config.getString("messages." + message);
    }

    @Override
    public Set<String> getButtons(String menuID) {
        return Objects.requireNonNull(config.getConfigurationSection(menuID + ".buttons")).getKeys(false);
    }

    @Override
    public ConfigurationSection getButtonData(String menuID, String buttonID) {
        return config.getConfigurationSection(menuID + ".buttons." + buttonID);
    }

    @Override
    public ConfigurationSection getMenuData(String menuID) {
        return config.getConfigurationSection(menuID);
    }

    @Override
    public ConfigurationSection getCommandOverrides() {
        return config.getConfigurationSection("command-override");
    }

    @Override
    public int getVersion() {
        return config.getInt("version");
    }

    @Override
    public ConfigurationSection getDependencies() { return config.getConfigurationSection("dependencies"); }

    @Override
    public boolean isDebug() { return config.getBoolean("debug"); }

}
