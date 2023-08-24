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
    public Set<String> getButtons(String shopName) {
        return Objects.requireNonNull(config.getConfigurationSection("shops." + shopName + ".buttons")).getKeys(false);
    }

    @Override
    public Set<String> getShops() {
        return Objects.requireNonNull(config.getConfigurationSection("shops")).getKeys(false);
    }

    @Override
    public ConfigurationSection itemData(String shopName, String buttonName) {
        return config.getConfigurationSection("shops." + shopName + ".buttons." + buttonName);
    }

    @Override
    public ConfigurationSection shopData(String shopName) {
        return config.getConfigurationSection("shops." + shopName);
    }

    public ConfigurationSection shopFormData(String shopName) {
        return config.getConfigurationSection(shopName);
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
    public boolean isDebug() { return config.getBoolean("debug"); }

}
