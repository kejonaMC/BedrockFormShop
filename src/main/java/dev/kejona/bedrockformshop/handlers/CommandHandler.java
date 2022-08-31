package dev.kejona.bedrockformshop.handlers;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.utils.Placeholders;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

public class CommandHandler {
    private final ConfigurationHandler SECTION = BedrockFormShop.getInstance().getSECTION();
    /**
     * Command execution logic
     */
    public void executeCommand(UUID uuid, String command, BigDecimal price) {
        HashMap<UUID, Boolean> tempOpPlayer = new HashMap<>();
        // Get Player Instance.
        Player player = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        // Check if player has enough money.
        if (VaultAPI.eco().getBalance(player).doubleValue() < price.doubleValue()) {
            assert player != null;
            player.sendMessage(Placeholders.colorCode(SECTION.getMessages("not-enough-money")));
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
        player.performCommand(Placeholders.set(command, player));
        player.sendMessage(Placeholders.set(SECTION.getMessages("command-bought"), command, price));
        // Deop player if they were not op before.
        if (tempOpPlayer.containsKey(uuid)) {
            player.setOp(false);
            tempOpPlayer.remove(uuid);
        }
    }
}
