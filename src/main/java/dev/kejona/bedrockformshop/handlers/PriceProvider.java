package dev.kejona.bedrockformshop.handlers;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.logger.Logger;
import me.gypopo.economyshopgui.api.EconomyShopGUIHook;
import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.HashMap;

public class PriceProvider {

    private final ConfigurationHandler SECTION = BedrockFormShop.getInstance().getSECTION();
    private final HashMap<Boolean, String> supportedPluginMap = BedrockFormShop.getInstance().getSupportedPluginMap();
    private Dependencies supportedPlugin;
    private final String menuID;
    private final String buttonID;
    private final Logger logger = Logger.getLogger();
    private final int negativeInt = -1;

    public PriceProvider(String menuID, String buttonID) {
        this.menuID = menuID;
        this.buttonID = buttonID;
    }
    /**
     * Buy price provider logic. Check if another shop plugin is present and always default back to our price list.
     */
    @Nullable
    public BigDecimal buyPrice(Material material) {
        // Always start at out own price provider
        if (supportedPluginMap.isEmpty()) {
            return defaultBuyPrice();
        }

        supportedPlugin = Dependencies.valueOf(supportedPluginMap.get(true));
        try {
            BigDecimal price;
            switch (supportedPlugin) {
                // All dependency price getters
                case ShopGUIPlus -> {
                    price = BigDecimal.valueOf(ShopGuiPlusApi.getItemStackPriceBuy(new ItemStack(material)));
                    logger.debug("API item price " + price);
                    if (price.intValue() == negativeInt) {
                        logger.debug("API item price (" + material.name() + "): " + price + " We default back to BedrockFormShop price");
                        return defaultBuyPrice();
                    }
                    return price;
                }

                case EconomyShopGUI, EconomyShopGuiPremium -> {
                    price = BigDecimal.valueOf(EconomyShopGUIHook.getItemBuyPrice(new ItemStack(material)));
                    logger.debug("API item price " + price);
                    if (price.intValue() == negativeInt) {
                        logger.debug("API item price (" + material.name() + "): " + price + " We default back to BedrockFormShop price");
                        return defaultBuyPrice();
                    }
                    return price;
                }

                // End if hooks
            }
        } catch (Exception ignored) {
            // incase a return on dependency returns a null we check our prices
            logger.debug("Was unable to get the buy price of (" + material.name() + ") from " + supportedPlugin + " If a price is present in BedrockFormShop config that price will be used.");
            return defaultBuyPrice();
        }
        return null;
    }
    /**
     * Sell price provider logic. Check if another shop plugin is present and always default back to our price list.
     */
    @Nullable
    public BigDecimal sellPrice(Material material) {
        // Always start at out own price provider
        if (supportedPluginMap.isEmpty()) {
            return defaultSellPrice();
        }

        supportedPlugin = Dependencies.valueOf(supportedPluginMap.get(true));
        try {
            BigDecimal price;
            switch (supportedPlugin) {
                // All dependency price getters
                case ShopGUIPlus -> {
                    price = BigDecimal.valueOf(ShopGuiPlusApi.getItemStackPriceSell(new ItemStack(material)));
                    logger.debug("API item price " + price);
                    if (price.intValue() == negativeInt) {
                        logger.debug("API item price of (" + material.name() + "): " + price + " We default back to BedrockFormShop price");

                        return defaultSellPrice();
                    }
                    return price;
                }

                case EconomyShopGUI, EconomyShopGuiPremium -> {
                    price = BigDecimal.valueOf(EconomyShopGUIHook.getItemSellPrice(new ItemStack(material)));
                    logger.debug("API item price " + price);
                    if (price.intValue() == negativeInt) {
                        logger.debug("API item price (" + material.name() + "): " + price + " We default back to BedrockFormShop price");
                        return defaultBuyPrice();
                    }
                    return price;
                }

                // End of hooks
            }
        } catch (Exception ignored) {
            // incase a return on dependency returns a null we check our prices
            logger.debug("Was unable to get the sell price of (" + material.name() + ") from " + supportedPlugin + " If a price is present in BedrockFormShop config that price will be used.");
            return defaultSellPrice();
        }
        return null;
    }
    /**
     * Our own buy price list.
     */
    public BigDecimal defaultBuyPrice() {
        if (SECTION.getButtonData(menuID, buttonID).isSet("buy-price")) {
            return BigDecimal.valueOf(SECTION.getButtonData(menuID, buttonID).getDouble("buy-price"));
        } else {
            return null;
        }
    }
    /**
     * Our own sell price list.
     */
    public BigDecimal defaultSellPrice() {
        if (SECTION.getButtonData(menuID, buttonID).isSet("sell-price")) {
            return BigDecimal.valueOf(SECTION.getButtonData(menuID, buttonID).getDouble("sell-price"));
        } else {
            return null;
        }
    }

    private enum Dependencies {
        ShopGUIPlus,
        EconomyShopGUI,
        EconomyShopGuiPremium
    }
}
