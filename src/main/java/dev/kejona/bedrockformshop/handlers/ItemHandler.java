package dev.kejona.bedrockformshop.handlers;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.utils.Placeholders;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ItemHandler {

    public FileConfiguration config = BedrockFormShop.getInstance().getConfig();
    public void buyItem(UUID uuid, String itemName, double price, int amount, String dataPath, boolean isEnchantment, boolean isPotion) {
        // Get Player Instance.
        Player player = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        // Check if player has enough space in inventory.
        assert player != null;
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Placeholders.colorCode(config.getString("messages.inventory-full")));
            return;
        }
        // Get Item name from config.
        ItemStack itemStack = new ItemStack(Material.valueOf(itemName));
        // Check if item is enchanted.
        if (isEnchantment) {
            // Get enchantment name from config
            String getEnchantment = config.getString(dataPath);
            // Get level from enchantmentData with a split.
            assert getEnchantment != null;
            int level = Integer.parseInt(getEnchantment.split(":")[1]);
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(getEnchantment.replace(":" + level, "").toLowerCase()));
            if (itemStack.getType() == Material.ENCHANTED_BOOK) {
                itemStack.setItemMeta(ApplyItemEffects.addEnchantmentToBook(enchantment, level, itemStack));
            } else {
                // Add enchantment to normal items.
                assert enchantment != null;
                itemStack.addEnchantment(enchantment, level);
            }
            itemName = itemStack.getType().name().toLowerCase() + " " + getEnchantment.toLowerCase();
        }
        // Check if item is a potion.
        if (isPotion) {
            itemStack.setItemMeta(ApplyItemEffects.addPotionEffect(dataPath, itemStack));
            // Add potion name to item.
            itemName = Objects.requireNonNull(config.getString(dataPath + ".type")).toLowerCase() + " " + itemStack.getType().name().toLowerCase();
        }
        // Check if player has enough money.
        if (VaultAPI.eco().getBalance(player) < price * amount) {
            player.sendMessage(Placeholders.colorCode(config.getString("messages.not-enough-money")));
            return;
        }
        // Withdraw money from player.
        VaultAPI.eco().withdrawBalance(player, price * amount);
        // Give item to player.
        itemStack.setAmount(amount);
        player.getInventory().addItem(itemStack);
        player.sendMessage(Placeholders.set(config.getString("messages.item-bought"), itemName, price, amount));
    }

    public void sellItem(UUID uuid, String ItemName, double price, int amount) {
        // Get Player Instance.
        Player player = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        // Get Item from form.
        assert player != null;
        // Get an itemstack list from all items in player inventory.
        List<ItemStack> list = new ArrayList<>();
        for (ItemStack i : player.getInventory().getContents()){
            if (i != null)
                list.add(i);
        }

        ItemStack[] inv = list.toArray(new ItemStack[0]);
        for (ItemStack fullinventory : inv) {
            if (fullinventory == null) {
                player.sendMessage(Placeholders.set(config.getString("messages.no-items"), ItemName));
                return;
            }
            // Get item from itemname and has the correct amount of items.
            if (fullinventory.getType() == Material.valueOf(ItemName) && fullinventory.getAmount() >= amount) {
                // Withdraw money from player
                VaultAPI.eco().depositBalance(player, price * amount);
                // Remove item from player.
                fullinventory.setAmount(fullinventory.getAmount() - amount);
                player.sendMessage(Placeholders.set(config.getString("messages.item-sold"), ItemName, price, amount));
                return;
                // Player does not have the right amount of items.
            } else if (fullinventory.getType() == Material.valueOf(ItemName) && fullinventory.getAmount() < amount) {
                player.sendMessage(Placeholders.set(config.getString("messages.not-enough-items"), ItemName));
                return;
            }
        }
    }
}