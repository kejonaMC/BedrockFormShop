package dev.kejona.bedrockformshop.handlers;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.UUID;

public class ItemHandler {

    public static void buyItem(UUID uuid, String item, double price, int amount) {
        FileConfiguration config = BedrockFormShop.getInstance().getConfig();
        // Get Player Instance
        Player player = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        // Get Item from form
        ItemStack itemStack = new ItemStack(Material.valueOf(item));
        // Check if player has enough money
        if (VaultAPI.eco().getBalance(player) < price * amount) {
            assert player != null;
            player.sendMessage(Utils.colorCode(config.getString("messages.not-enough-money")));
            return;
        }
        // Withdraw money from player
        VaultAPI.eco().withdrawBalance(player, price * amount);
        assert player != null;
        // Give item to player
        itemStack.setAmount(amount);
        player.getInventory().addItem(itemStack);
        player.sendMessage(Utils.transactionPlaceholder(config.getString("messages.item-bought"), item, price, amount));
    }

    public static void sellItem(UUID uuid, String item, double price, int amount) {
        FileConfiguration config = BedrockFormShop.getInstance().getConfig();
        // Get Player Instance
        Player player = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        // Get Item from form
        assert player != null;

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null) {
                player.sendMessage(Utils.textPlaceholder(config.getString("messages.no-items"), item));
                return;
            }
            if (itemStack.getType() == Material.valueOf(item) && itemStack.getAmount() >= amount) {
                // Withdraw money from player
                VaultAPI.eco().depositBalance(player, price * amount);
                // Remove item from player
                itemStack.setAmount(itemStack.getAmount() - amount);
                player.sendMessage(Utils.transactionPlaceholder(config.getString("messages.item-sold"), item, price, amount));
                return;
            }
            else if (itemStack.getType() == Material.valueOf(item) && itemStack.getAmount() < amount) {
                player.sendMessage(Utils.textPlaceholder(config.getString("messages.not-enough-items"), item));
                return;
            }
        }
    }
}