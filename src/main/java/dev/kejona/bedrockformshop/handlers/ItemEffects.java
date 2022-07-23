package dev.kejona.bedrockformshop.handlers;

import dev.kejona.bedrockformshop.BedrockFormShop;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class ItemEffects {

    public static FileConfiguration config = BedrockFormShop.getInstance().getConfig();
    /**
     * Adds an enchantment to meta data
     * @param enchantment the enchantment to add to item
     * @param level the level of the enchantment
     * @param itemstack the item to add the enchantment to
     * @return returns enchantment meta to add to item
     */
    public static EnchantmentStorageMeta addEnchantmentToBook(Enchantment enchantment, int level, ItemStack itemstack) {
        // Enchantments on Enchanted books needs to be in its item meta.
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemstack.getItemMeta();
        assert meta != null;
        assert enchantment != null;
        meta.addStoredEnchant(enchantment, level, true);

        return meta;
    }
    /**
     * Adds potion effect to item meta data
     * @param dataPath the path to the potion effect in the config
     * @param itemstack the item to add the enchantment to
     * @return returns potion meta to add to item
     */
    public static PotionMeta addPotionEffect(String dataPath, ItemStack itemstack) {
        // Check if potion is a splash potion.
        boolean isSplash = config.getBoolean(dataPath + ".splash");
        // Get and set potion type.
        if (isSplash) {
            itemstack.setType(Material.SPLASH_POTION);
        }
        // Add all potion effects to the potion.
        PotionMeta potionmeta = (PotionMeta) itemstack.getItemMeta();
        PotionType potionType = PotionType.valueOf(config.getString(dataPath + ".type"));
        // set potion data.
        assert potionmeta != null;
        PotionData data = new PotionData(potionType, config.getBoolean(dataPath + ".extended"), config.getBoolean(dataPath + ".upgraded"));
        potionmeta.setBasePotionData(data);

        return potionmeta;
    }
}
