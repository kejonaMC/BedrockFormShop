package dev.kejona.bedrockformshop.handlers;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.logger.Logger;
import dev.kejona.bedrockformshop.utils.Placeholders;
import dev.kejona.bedrockformshop.utils.ShopData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;

public class ItemHandler {
    public ConfigurationHandler SECTION = BedrockFormShop.getInstance().getConfigurationHandler();
    Logger logger = Logger.getLogger();
    public void buyItem(UUID uuid, String itemName, double price, int amount, String menuID, String button, String shopType) {
        boolean isEnchantment = shopType.equals("ENCHANTMENT");
        boolean isPotion = shopType.equals("POTION");
        boolean isSpawner = shopType.equals("SPAWNER");
        // Get Player Instance.
        Player player = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        // Check if player has enough space in inventory.
        assert player != null;
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Placeholders.colorCode(SECTION.getMessages("inventory-full")));
            return;
        }
        // Get Item name from config.
        ItemStack itemStack;
        try {
            itemStack = new ItemStack(Material.valueOf(itemName));
        } catch (IllegalArgumentException e) {
            Logger.getLogger().severe("Item: " + itemName + " is not a valid item.");
            return;
        }
        // Check if item is enchanted.
        if (isEnchantment) {
            // Get enchantment name from config
            String getEnchantment = SECTION.getButtonData(menuID, button).getString("enchantment");
            // Get level from enchantmentData with a split.
            assert getEnchantment != null;
            int level = Integer.parseInt(getEnchantment.split(":")[1]);

            try {
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(getEnchantment.replace(":" + level, "").toLowerCase()));
                if (itemStack.getType() == Material.ENCHANTED_BOOK) {
                    itemStack.setItemMeta(ApplyItemEffects.addEnchantmentToBook(enchantment, level, itemStack));
                } else {
                    // Add enchantment to normal items.
                    assert enchantment != null;
                    itemStack.addEnchantment(enchantment, level);
                }
            } catch (Exception e) {
                Logger.getLogger().severe("Enchantment: " + getEnchantment + " is not a valid enchantment.");
                return;
            }
            itemName = itemStack.getType().name().toLowerCase() + " " + getEnchantment.toLowerCase();
        }
        // Check if item is a potion.
        if (isPotion) {
            itemStack.setItemMeta(ApplyItemEffects.addPotionEffect(SECTION.getButtonData(menuID, button), itemStack));
            // Add potion name to item.
            itemName = Objects.requireNonNull(SECTION.getButtonData(menuID, button).getString("potion-data.type")).toLowerCase() + " " + itemStack.getType().name().toLowerCase();
        }
        // Check if item is a spawner.
        if (isSpawner) {
            itemStack.setItemMeta(ApplyItemEffects.addMobToBlock(SECTION.getButtonData(menuID, button), itemStack));
            // Add spawner name to item.
            itemName = Objects.requireNonNull(SECTION.getButtonData(menuID, button).getString("mob-type")).toLowerCase() + " " + itemStack.getType().name().toLowerCase();
        }
        // Check if player has enough money.
        if (VaultAPI.eco().getBalance(player) < price * amount) {
            player.sendMessage(Placeholders.colorCode(SECTION.getMessages("not-enough-money")));
            return;
        }
        // Withdraw money from player.
        VaultAPI.eco().withdrawBalance(player, price * amount);
        // Give item to player.
        itemStack.setAmount(amount);
        player.getInventory().addItem(itemStack);
        try {
            new ShopData(player.getName(), price, amount, itemName);
        } catch (IOException e) {
            logger.severe("Could not save shop data." + e.getMessage());
        }
        player.sendMessage(Placeholders.set(SECTION.getMessages("item-bought"), itemName, price, amount));
    }

    public void sellItem(UUID uuid, String itemName, double price, int amount) {
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
                player.sendMessage(Placeholders.set(SECTION.getMessages("no-items"), itemName));
                return;
            }
            // Get item from itemname and has the correct amount of items.
            if (fullinventory.getType() == Material.valueOf(itemName) && fullinventory.getAmount() >= amount) {
                // Withdraw money from player
                VaultAPI.eco().depositBalance(player, price * amount);
                // Remove item from player.
                fullinventory.setAmount(fullinventory.getAmount() - amount);
                try {
                    new ShopData(player.getName(), price, amount, itemName);
                } catch (IOException e) {
                    logger.severe("Could not save shop data." + e.getMessage());
                }
                player.sendMessage(Placeholders.set(SECTION.getMessages("item-sold"), itemName, price, amount));
                return;
                // Player does not have the right amount of items.
            } else if (fullinventory.getType() == Material.valueOf(itemName) && fullinventory.getAmount() < amount) {
                player.sendMessage(Placeholders.set(SECTION.getMessages("not-enough-items"), itemName));
                return;
            }
        }
    }
}