package dev.kejona.bedrockformshop;

import dev.kejona.bedrockformshop.commands.ShopCommand;
import dev.kejona.bedrockformshop.config.Configuration;
import dev.kejona.bedrockformshop.handlers.VaultAPI;
import dev.kejona.bedrockformshop.listeners.PlacedSpawner;
import dev.kejona.bedrockformshop.logger.JavaUtilLogger;
import dev.kejona.bedrockformshop.logger.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class BedrockFormShop extends JavaPlugin {
    private static BedrockFormShop INSTANCE;
    @Override
    public void onEnable() {
        INSTANCE = this;
        // Create / load configuration
        new Configuration(this);
        // Enable Vault
        new VaultAPI();
        // Create the logger.
        Logger logger = new JavaUtilLogger(this.getLogger());
        // Register commands.
        Objects.requireNonNull(this.getCommand("shop")).setExecutor(new ShopCommand());
        // Register event for Spawners block-state update.
        getServer().getPluginManager().registerEvents(new PlacedSpawner(), this);
        logger.info("BedrockFormShop enabled!");
    }
    public static BedrockFormShop getInstance() {
        return INSTANCE;
    }
}
