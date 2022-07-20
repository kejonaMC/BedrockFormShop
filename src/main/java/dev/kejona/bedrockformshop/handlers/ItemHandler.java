package dev.kejona.bedrockformshop.handlers;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.utils.Placeholders;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemHandler {

    public void buyItem(UUID uuid, String itemName, double price, int amount, FileConfiguration config, String dataPath, boolean isEnchantment, boolean isPotion) {
        // Get Player Instance.
        Player player = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        // Get Item from form.
        ItemStack itemStack = new ItemStack(Material.valueOf(itemName));
        // Check if item is enchanted.
        if (isEnchantment) {
            String getEnchantment = config.getString(dataPath);
            // Get level from enchantmentData with a split.
            assert getEnchantment != null;
            int level = Integer.parseInt(getEnchantment.split(":")[1]);
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(getEnchantment.replace(":" + level, "").toLowerCase()));
            // Enchantments on Enchanted books needs to be in its item meta.
            if (itemStack.getType() == Material.ENCHANTED_BOOK) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta)itemStack.getItemMeta();
                assert meta != null;
                assert enchantment != null;
                meta.addStoredEnchant(enchantment, level, true);
                itemStack.setItemMeta(meta);
                // Enchantments on normal items needs to be in the item stack.
            } else {
                assert enchantment != null;
                itemStack.addEnchantment(enchantment, level);
            }
            itemName = itemStack.getType() + " " + getEnchantment.toLowerCase();
            // Check if item is a potion.
        }
        if (isPotion) {
            // Check if potion is a splash potion.
            boolean isSplash = config.getBoolean(dataPath + ".splash");
            // Get potion type.
            if (isSplash) {
                itemStack.setType(Material.SPLASH_POTION);
            }
            // Add all potion effects to the potion.
            PotionMeta potionmeta = (PotionMeta) itemStack.getItemMeta();
            PotionType potionType = PotionType.valueOf(config.getString(dataPath + ".type"));
            // set potion data:
            assert potionmeta != null;
            PotionData data = new PotionData(potionType, config.getBoolean(dataPath + ".extended"), config.getBoolean(dataPath + ".upgraded"));
            potionmeta.setBasePotionData(data);
            itemStack.setItemMeta(potionmeta);
            // Add potion name to item
            itemName = potionType.name().toLowerCase() + " " + itemStack.getType();
        }
        // Check if player has enough money.
        if (VaultAPI.eco().getBalance(player) < price * amount) {
            assert player != null;
            player.sendMessage(Placeholders.colorCode(config.getString("messages.not-enough-money")));
            return;
        }
        // Withdraw money from player.
        VaultAPI.eco().withdrawBalance(player, price * amount);
        assert player != null;
        // Give item to player.
        itemStack.setAmount(amount);
        player.getInventory().addItem(itemStack);
        player.sendMessage(Placeholders.placeholder(config.getString("messages.item-bought"), itemName, price, amount));
    }

    public void sellItem(UUID uuid, String ItemName, double price, int amount) {
        FileConfiguration config = BedrockFormShop.getInstance().getConfig();
        // Get Player Instance.
        Player player = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        // Get Item from form.
        assert player != null;
        // Get an itemstack list from all items in player inventory.
        List<ItemStack> list = new ArrayList<>();
        for(ItemStack i : player.getInventory().getContents()){
            if(i != null)
                list.add(i);
        }

        ItemStack[] inv = list.toArray(new ItemStack[0]);
        for (ItemStack fullinventory : inv) {
            if (fullinventory == null) {
                player.sendMessage(Placeholders.placeholder(config.getString("messages.no-items"), ItemName));
                return;
            }
            // Get item from itemname and has the correct amount of items.
            if (fullinventory.getType() == Material.valueOf(ItemName) && fullinventory.getAmount() >= amount) {
                // Withdraw money from player
                VaultAPI.eco().depositBalance(player, price * amount);
                // Remove item from player.
                fullinventory.setAmount(fullinventory.getAmount() - amount);
                player.sendMessage(Placeholders.placeholder(config.getString("messages.item-sold"), ItemName, price, amount));
                return;
            }
            // Player does not hav the right amount of items.
            else if (fullinventory.getType() == Material.valueOf(ItemName) && fullinventory.getAmount() < amount) {
                player.sendMessage(Placeholders.placeholder(config.getString("messages.not-enough-items"), ItemName));
                return;
            }
        }
    }
}