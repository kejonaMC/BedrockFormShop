package dev.kejona.bedrockformshop.handlers;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.logger.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultAPI {

    public Economy economy;

    public VaultAPI() {
        if (!initVault()) {
            Logger.getLogger().severe("Vault not found! Disabling EcoDatabase!");
        }
    }

    private boolean initVault() {
        if (BedrockFormShop.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        final RegisteredServiceProvider<Economy> rsp = BedrockFormShop.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;

        economy = rsp.getProvider();

        return economy != null;
    }

    public double getBalance(Player player) {
        return this.economy.getBalance(player);
    }

    public void withdrawBalance(Player player, double amount) {
        this.economy.withdrawPlayer(player, amount);
    }

    public void depositBalance(Player player, double amount) {
        this.economy.depositPlayer(player, amount);
    }

    public static VaultAPI eco() {
        return new VaultAPI();
    }

    public Economy getEconomy() {
        return economy;
    }
}
