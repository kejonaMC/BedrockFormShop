package dev.kejona.bedrockformshop.handlers;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CommandHandler {
    private final ConfigurationHandler SECTION = BedrockFormShop.getInstance().getSECTION();
    /**
     * Command execution logic
     */
    public void executeCommand(UUID uuid, List<String> commands, BigDecimal price) {
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

        for (String command : commands) {
            // Split command config string to get the command send option.
            String[] commandParts = command.split(" ", 2);
            assert player != null;
            // The actual command.
            String getCommand = Placeholders.set(commandParts[1], player);
            // Command options.
            switch (commandParts[0].replace(";", "")) {
                // Preform command as op.
                case "op" -> {
                    // If player is not op then set op and add player into hashmap.
                    if (!player.isOp()) {
                        tempOpPlayer.put(uuid, true);
                        player.setOp(true);
                    }
                    player.performCommand(getCommand);
                    // Deop player if they were not op before.
                    if (tempOpPlayer.containsKey(uuid)) {
                        player.setOp(false);
                        tempOpPlayer.remove(uuid);
                    }
                }
                // Preform command as normal player.
                case "player" -> player.performCommand(getCommand);
                // Preform command as console.
                case "console" -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getCommand);
            }
        }
    }
}
