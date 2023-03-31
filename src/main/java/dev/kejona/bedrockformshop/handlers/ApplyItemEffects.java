package dev.kejona.bedrockformshop.handlers;

import dev.kejona.bedrockformshop.logger.Logger;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ApplyItemEffects {
    /**
     * Adds an enchantment to meta data
     * @param enchantment the enchantment to add to item
     * @param level the level of the enchantment
     * @param itemstack the item to add the enchantment to
     * @return returns enchantment meta to add to item
     */
    public @NotNull EnchantmentStorageMeta addEnchantmentToBook(Enchantment enchantment, int level, @NotNull ItemStack itemstack) {
        // Enchantments on Enchanted books needs to be in its item meta.
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemstack.getItemMeta();
        assert meta != null;
        assert enchantment != null;
        meta.addStoredEnchant(enchantment, level, true);

        return meta;
    }
    /**
     * Adds potion effect to item meta data
     * @param SECTION the config section block
     * @param itemstack the item to add the enchantment to
     * @return returns potion meta to add to item
     */
    public PotionMeta addPotionEffect(@NotNull ConfigurationSection SECTION, ItemStack itemstack) {
        // Check if potion is a splash potion.
        if (SECTION.getBoolean("potion-data.splash")) {
            itemstack.setType(Material.SPLASH_POTION);
        }
        // Add all potion effects to the potion.

        String getPotionType = SECTION.getString("potion-data.type");
        PotionMeta potionmeta = null;
        try {
            potionmeta = (PotionMeta) itemstack.getItemMeta();
            PotionType potionType = PotionType.valueOf(getPotionType);
            // set potion data.
            assert potionmeta != null;
            PotionData data = new PotionData(potionType, SECTION.getBoolean("potion-data.extended"), SECTION.getBoolean("potion-data.upgraded"));
            potionmeta.setBasePotionData(data);
        } catch (IllegalArgumentException e) {
            Logger.getLogger().severe("Potion: " + getPotionType + " is not a valid potion type.");
        }

        return potionmeta;
    }
    /**
     * Adds a mob to the spawner
     * @param SECTION the config section block
     * @param itemstack the item to add the mob type to
     * @return returns spawner with set type and blockstate
     */
    public @NotNull BlockStateMeta addMobToBlock (@NotNull ConfigurationSection SECTION, @NotNull ItemStack itemstack) {
        // Get meta from block.
        BlockStateMeta bsm = (BlockStateMeta) itemstack.getItemMeta();
        // Set mob type.
        CreatureSpawner cs = (CreatureSpawner) Objects.requireNonNull(bsm).getBlockState();
        String mobType = SECTION.getString("mob-type");
        try {
            cs.setSpawnedType(EntityType.valueOf(mobType));
            bsm.setDisplayName(mobType);
        } catch (IllegalArgumentException e) {
            Logger.getLogger().severe("Mob: " + mobType + " is not a valid mob type.");
        }
        bsm.setBlockState(cs);
        return bsm;
    }
}
