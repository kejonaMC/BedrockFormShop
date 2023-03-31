package dev.kejona.bedrockformshop;

import dev.kejona.bedrockformshop.commands.ShopCommand;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.handlers.VaultAPI;
import dev.kejona.bedrockformshop.listeners.CommandInterceptor;
import dev.kejona.bedrockformshop.listeners.PlacedSpawner;
import dev.kejona.bedrockformshop.logger.JavaUtilLogger;
import dev.kejona.bedrockformshop.logger.Logger;
import dev.kejona.bedrockformshop.utils.Metrics;
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
        new Metrics(this, 16668);
        // Create the logger.
        Logger logger = new JavaUtilLogger(this.getLogger());
        // load configuration.
        configFiles();
        SECTION = new ConfigurationHandler(this.getConfig());
        // Check if config file is up-to-date.
        if (SECTION.getVersion() > 1) {
            logger.severe("The configuration file is outdated. Please update it.");
        }
        // Check if debug is enabled.
        if (SECTION.isDebug()) {
            logger.info("Debug logger is enabled!");
            logger.setDebug(true);
        }
        // Enable Vault.
        new VaultAPI();
        // Register commands.
        Objects.requireNonNull(this.getCommand("bshop")).setExecutor(new ShopCommand());
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
    public void configFiles() {
        Logger logger = Logger.getLogger();
        File configFile = new File(this.getDataFolder(), "config.yml");
        // Create file
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            this.saveResource("config.yml", false);
        }
        // load config
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            logger.severe("Could not load config.yml" + e.getMessage());
        }
    }
    // Reload Config file.
    public void reloadConfigFiles() {
        this.reloadConfig();
        SECTION = new ConfigurationHandler(this.getConfig());
    }
    // Get the server api version. split to remove snapshot.
    public String getServerVersion() {
        return getServer().getBukkitVersion().substring(0,4).split("-")[0];
    }

    public static BedrockFormShop getInstance() {
        return INSTANCE;
    }

    public ConfigurationHandler getSECTION() {
        return SECTION;
    }
}
