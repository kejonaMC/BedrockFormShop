package dev.kejona.bedrockformshop.handlers;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.utils.Placeholders;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemHandler {

    public void buyItem(UUID uuid, String item, double price, int amount) {
        FileConfiguration config = BedrockFormShop.getInstance().getConfig();
        // Get Player Instance
        Player player = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        // Get Item from form
        ItemStack itemStack = new ItemStack(Material.valueOf(item));
        // Check if player has enough money
        if (VaultAPI.eco().getBalance(player) < price * amount) {
            assert player != null;
            player.sendMessage(Placeholders.colorCode(config.getString("messages.not-enough-money")));
            return;
        }
        // Withdraw money from player
        VaultAPI.eco().withdrawBalance(player, price * amount);
        assert player != null;
        // Give item to player
        itemStack.setAmount(amount);
        player.getInventory().addItem(itemStack);
        player.sendMessage(Placeholders.placeholder(config.getString("messages.item-bought"), item, price, amount));
    }

    public void sellItem(UUID uuid, String item, double price, int amount) {
        FileConfiguration config = BedrockFormShop.getInstance().getConfig();
        // Get Player Instance
        Player player = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        // Get Item from form
        assert player != null;

        List<ItemStack> list = new ArrayList<>();
        for(ItemStack i : player.getInventory().getContents()){
            if(i != null)
                list.add(i);
        }
        ItemStack[] inv = list.toArray(new ItemStack[0]);
        for (ItemStack fullinventory : inv) {
            if (fullinventory == null) {
                player.sendMessage(Placeholders.placeholder(config.getString("messages.no-items"), item));
                return;
            }

            if (fullinventory.getType() == Material.valueOf(item) && fullinventory.getAmount() >= amount) {
                // Withdraw money from player
                VaultAPI.eco().depositBalance(player, price * amount);
                // Remove item from player
                fullinventory.setAmount(fullinventory.getAmount() - amount);
                player.sendMessage(Placeholders.placeholder(config.getString("messages.item-sold"), item, price, amount));
                return;
            }
            else if (fullinventory.getType() == Material.valueOf(item) && fullinventory.getAmount() < amount) {
                player.sendMessage(Placeholders.placeholder(config.getString("messages.not-enough-items"), item));
                return;
            }
        }
    }
}