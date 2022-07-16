package dev.kejona.bedrockformshop.handlers;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.utils.Placeholders;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandHandler {

    public void executeCommand(UUID uuid, String command, double price, FileConfiguration config) {
        // Get Player Instance
        Player player = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        // Check if player has enough money
        if (VaultAPI.eco().getBalance(player) < price) {
            assert player != null;
            player.sendMessage(Placeholders.colorCode(config.getString("messages.not-enough-money")));
            return;
        }
        // Withdraw money from player
        VaultAPI.eco().withdrawBalance(player, price);
        assert player != null;
        // Execute command
        player.setOp(true);
        player.performCommand(Placeholders.placeholder(command, player));
        player.setOp(false);
        player.sendMessage(Placeholders.placeholder(config.getString("messages.command-bought"), command, price));
    }
}
