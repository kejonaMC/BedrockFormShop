package dev.kejona.bedrockformshop;

import dev.kejona.bedrockformshop.commands.ShopCommand;
import dev.kejona.bedrockformshop.handlers.VaultAPI;
import dev.kejona.bedrockformshop.logger.JavaUtilLogger;
import dev.kejona.bedrockformshop.logger.Logger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class BedrockFormShop extends JavaPlugin {
    private static BedrockFormShop INSTANCE;

    @Override
    public void onEnable() {
        // plugin startup logic
        INSTANCE = this;
        Logger logger = new JavaUtilLogger(this.getLogger());
        loadConfig();
        if (getConfig().getInt("version") < 1) {
            logger.severe("Your config file is outdated, Please update/regenerate config by renaming your current config file to config.yml.old");
        }
        // Register commands
        Objects.requireNonNull(this.getCommand("shop")).setExecutor(new ShopCommand());
        // Check if there are hooks and if so register them.
        new VaultAPI();
        logger.info("BedrockFormShop enabled!");
    }

    /**
     * Load config or create
     */
    private void loadConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static BedrockFormShop getInstance() {
        return INSTANCE;
    }

}
