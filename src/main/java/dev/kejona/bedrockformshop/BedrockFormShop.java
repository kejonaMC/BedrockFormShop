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
import java.util.HashMap;
import java.util.Objects;

public final class BedrockFormShop extends JavaPlugin {
    private static BedrockFormShop INSTANCE;
    private ConfigurationHandler SECTION;
    private final HashMap<Boolean, String> pluginHook = new HashMap<>();
    private Logger logger;

    @Override
    public void onEnable() {
        INSTANCE = this;
        // Create the logger.
        logger = new JavaUtilLogger(this.getLogger());
        // load configuration.
        createFiles();
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
        // Check if api's are present.
        logger.info("Checking for supported shop plugins.");
        apisChecker();
        // Enable Vault.
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

    public void apisChecker() {
        // List of api names.
        String[] plugins = {"ShopGUI", "EconomyShopGUI"};
        for (String plugin : plugins) {
            if (getServer().getPluginManager().getPlugin(plugin) != null) {
                if (SECTION.getApiEnable()) {
                    logger.info("Hooked successfully into " + plugin + ". If prices are available on " + plugin + " we will use those prices in BedrockFormShop");
                    pluginHook.put(true, plugin);
                } else {
                    logger.info("Found a shop plugin; " + plugin + ". You can set 'enable-hook:' in config.yml to true if you want to use " + plugin + "prices!");
                }
            }
        }
    }

    /**
     * Load config or create.
     */
    public void createFiles() {
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

    // Get the server api version. split to remove snapshot.
    public String getServerVersion() {
        return getServer().getBukkitVersion().split("-")[0];
    }

    public static BedrockFormShop getInstance() {
        return INSTANCE;
    }

    public ConfigurationHandler getSECTION() {
        return SECTION;
    }

    public HashMap<Boolean, String> getHookedPlugins() {
        return pluginHook;
    }
}
