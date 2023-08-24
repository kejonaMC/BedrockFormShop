package dev.kejona.bedrockformshop.handlers;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.logger.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class VaultAPI {

    public Economy economy;
    //
    public VaultAPI() {
        if (!initVault()) {
            Logger.getLogger().severe("Vault not found!");
        }
    }
    // Initialize Vault.
    private boolean initVault() {
        if (BedrockFormShop.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        final RegisteredServiceProvider<Economy> rsp = BedrockFormShop.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;

        economy = rsp.getProvider();

        return economy != null;
    }
    // Returns the balance of a player.
    public BigDecimal getBalance(Player player) {
        return BigDecimal.valueOf(this.economy.getBalance(player));
    }
    // Withdraws money from a player.
    public void withdrawBalance(Player player, @NotNull BigDecimal amount) {
        this.economy.withdrawPlayer(player, amount.doubleValue());
    }
    // Deposits money to a player.
    public void depositBalance(Player player, @NotNull BigDecimal amount) {
        this.economy.depositPlayer(player, amount.doubleValue());
    }
    // Instance
    @Contract(" -> new")
    public static @NotNull VaultAPI eco() {
        return new VaultAPI();
    }
}
