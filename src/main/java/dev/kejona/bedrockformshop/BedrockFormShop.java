package dev.kejona.bedrockformshop;

import dev.kejona.bedrockformshop.commands.ShopCommand;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.handlers.VaultAPI;
import dev.kejona.bedrockformshop.listeners.CommandInterceptor;
import dev.kejona.bedrockformshop.listeners.PlacedSpawner;
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
    private ConfigurationHandler SECTION;

    @Override
    public void onEnable() {
        INSTANCE = this;
        // Create the logger.
        Logger logger = new JavaUtilLogger(this.getLogger());
        // load configuration
        createFiles();
        SECTION = new ConfigurationHandler(this.getConfig());
        // Check if config file is up-to-date.
        if (SECTION.getVersion() > 1) {
            logger.severe("The configuration file is outdated. Please update it.");
        }
        // Enable Vault
        new VaultAPI();
        // Register commands.
        Objects.requireNonNull(this.getCommand("shop")).setExecutor(new ShopCommand());
        // Register event for Spawners block-state update.
        getServer().getPluginManager().registerEvents(new PlacedSpawner(), this);
        // Register event for command interceptor if enabled and get a list of commands to intercept.
        if (SECTION.getCommandOverrides().getBoolean("enable")) {
            logger.info("Command override enabled. All configured commands will be overridden by BedrockFormShop.");
            getServer().getPluginManager().registerEvents(
                    new CommandInterceptor(
                            SECTION.getCommandOverrides().getStringList("commands")), this);
        }

        logger.info("BedrockFormShop enabled!");
    }

    /**
     * Load config or create.
     */
    public void createFiles() {
        Logger logger = Logger.getLogger();
        File configFile = new File(this.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            this.saveResource("config.yml", false);
        }

        FileConfiguration config = new YamlConfiguration();

        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            logger.severe("Could not load config.yml" + e.getMessage());
        }
    }

    public static BedrockFormShop getInstance() {
        return INSTANCE;
    }

    public ConfigurationHandler getSECTION() {
        return SECTION;
    }
}
