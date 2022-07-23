package dev.kejona.bedrockformshop.handlers;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.utils.Placeholders;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CommandHandler {

    public FileConfiguration config = BedrockFormShop.getInstance().getConfig();
    public void executeCommand(UUID uuid, String command, double price) {
        HashMap<UUID, Boolean> tempOpPlayer = new HashMap<>();
        // Get Player Instance.
        Player player = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        // Check if player has enough money.
        if (VaultAPI.eco().getBalance(player) < price) {
            assert player != null;
            player.sendMessage(Placeholders.colorCode(config.getString("messages.not-enough-money")));
            return;
        }
        // Withdraw money from player.
        VaultAPI.eco().withdrawBalance(player, price);
        assert player != null;
        // If player is not op then set op and add player into hashmap.
        if (!player.isOp()) {
            tempOpPlayer.put(uuid, true);
            player.setOp(true);
        }
        // Execute command.
        player.performCommand(Placeholders.placeholder(command, player));
        player.sendMessage(Placeholders.placeholder(config.getString("messages.command-bought"), command, price));
        // Deop player if they were not op before.
        if (tempOpPlayer.containsKey(uuid)) {
            player.setOp(false);
            tempOpPlayer.remove(uuid);
        }
    }
}
