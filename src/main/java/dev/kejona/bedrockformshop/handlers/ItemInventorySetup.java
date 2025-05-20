package dev.kejona.bedrockformshop.handlers;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.logger.Logger;
import dev.kejona.bedrockformshop.shopdata.ShopData;
import dev.kejona.bedrockformshop.shopdata.ShopType;
import dev.kejona.bedrockformshop.utils.Placeholders;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class ItemInventorySetup extends ShopData {
    private final ConfigurationHandler SECTION = BedrockFormShop.getInstance().getSECTION();
    private final ApplyItemEffects itemEffects = new ApplyItemEffects();
    private final String shopType;
    private final Material material;
    private final Player player;
    private final int quantity;

    public ItemInventorySetup(Material material, String shopType, Player player, int quantity) {
        this.shopType = shopType;
        this.material = material;
        this.quantity = quantity;
        this.player = player;
    }
    /**
     * ItemStack creator, Create itemStack apply Enchantment, spawner or potion effect if present.
     * Also calculates item space availability in inventory.
     */
    public boolean buyItemSuccess() {
        ItemStack item = new ItemStack(this.material);
        // Set material to ItemStack
        try {
            item.setType(this.material);
        } catch (Exception e) {
            Logger.getLogger().severe("Item: " + this.material + " is not a valid item.");
            return false;
        }
        switch (ShopType.valueOf(shopType)) {
            // Add spawner name to item.
            case SPAWNER -> item.setItemMeta(itemEffects.addMobToBlock(SECTION.itemData(getShopName(), getButtonName()), item));
            case ENCHANTMENT -> {
                // Get enchantment name from config
                String getEnchantment = SECTION.itemData(getShopName(), getButtonName()).getString("enchantment");
                // Get level from enchantmentData with a split.
                assert getEnchantment != null;
                int level = Integer.parseInt(getEnchantment.split(":")[1]);
                try {
                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(getEnchantment.replace(":" + level, "").toLowerCase()));
                    if (item.getType() == Material.ENCHANTED_BOOK) {
                        item.setItemMeta(itemEffects.addEnchantmentToBook(enchantment, level, item));
                    } else {
                        // Add enchantment to normal items.
                        assert enchantment != null;
                        item.addEnchantment(enchantment, level);
                    }
                } catch (Exception e) {
                    Logger.getLogger().severe("Enchantment: " + getEnchantment + " is not a valid enchantment.");
                    return false;
                }
            }
            // Add potion name to item.
            case POTION -> item.setItemMeta(itemEffects.addPotionEffect(SECTION.itemData(getShopName(), getButtonName()), item));
        }
        // Set item inventory logic
        boolean notStackable = item.getMaxStackSize() == 1;
        int freeslots = this.inventorySpace(player.getInventory(), material);
        if (notStackable) {
            if (freeslots >= quantity) {
                for (int i = quantity; i > 0; i--) {
                    item.setAmount(1);
                    player.getInventory().addItem(item);
                }
            } else {
                player.sendMessage(Placeholders.colorCode(SECTION.getMessages("no-free-space")));
                return false;
            }
        } else {
            if (freeslots >= quantity) {
                item.setAmount(quantity);
                player.getInventory().addItem(item);
            } else {
                player.sendMessage(Placeholders.colorCode(SECTION.getMessages("no-free-space")));
                return false;
            }
        }
        return true;
    }
    /**
     * Check if player has enough room in their inventory to buy the item.
     */
    public int inventorySpace(PlayerInventory inventory, Material item) {
        int count = 0;
        for (int slot = 0; slot < 36; slot ++) {
            ItemStack is = inventory.getItem(slot);
            if (is == null) {
                count += item.getMaxStackSize();
            }
            if (is != null) {
                if (is.getType() == item){
                    count += (item.getMaxStackSize() - is.getAmount());
                }
            }
        }
        return count;
    }

    public String getItemName() {
        return this.material.name();
    }

}
