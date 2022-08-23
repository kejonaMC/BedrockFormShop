package dev.kejona.bedrockformshop.handlers;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.logger.Logger;
import dev.kejona.bedrockformshop.shopdata.ShopData;
import dev.kejona.bedrockformshop.shopdata.ShopType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;


public class ItemBuilder extends ShopData {
    private final ConfigurationHandler SECTION = BedrockFormShop.getInstance().getSECTION();

    private final String shopType;
    private final Material material;
    private final int quantity;

    public ItemBuilder(Material material, int quantity, String shopType) {
        this.quantity = quantity;
        this.shopType = shopType;
        this.material = material;
    }

    public ItemStack item() {
        ItemStack item = new ItemStack(material);
        // Set material to ItemStack
        try {
            item.setType(material);
        } catch (Exception e) {
            Logger.getLogger().severe("Item: " + material + " is not a valid item.");
        }
        switch (ShopType.valueOf(shopType)) {

            case SPAWNER -> {
                // Add spawner name to item.
                item.setItemMeta(ApplyItemEffects.addMobToBlock(SECTION.getButtonData(getMenuID(), getButtonID()), item));
            }

            case ENCHANTMENT -> {
                // Get enchantment name from config
                String getEnchantment = SECTION.getButtonData(getMenuID(), getButtonID()).getString("enchantment");
                // Get level from enchantmentData with a split.
                assert getEnchantment != null;
                int level = Integer.parseInt(getEnchantment.split(":")[1]);
                try {
                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(getEnchantment.replace(":" + level, "").toLowerCase()));
                    if (item.getType() == Material.ENCHANTED_BOOK) {
                        item.setItemMeta(ApplyItemEffects.addEnchantmentToBook(enchantment, level, item));
                    } else {
                        // Add enchantment to normal items.
                        assert enchantment != null;
                        item.addEnchantment(enchantment, level);
                    }
                } catch (Exception e) {
                    Logger.getLogger().severe("Enchantment: " + getEnchantment + " is not a valid enchantment.");
                    return null;
                }
            }

            case POTION -> {
                item.setItemMeta(ApplyItemEffects.addPotionEffect(SECTION.getButtonData(getMenuID(), getButtonID()), item));
                // Add potion name to item.
            }
        }
        item.setAmount(quantity);

        return item;
    }

    public String getItemName() {
        return this.material.name();
    }

}
