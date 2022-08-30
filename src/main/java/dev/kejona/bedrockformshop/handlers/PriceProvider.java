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
    private final Dependencies supportedPlugin;
    private final String menuID;
    private final String buttonID;
    private final Logger logger = Logger.getLogger();

    public PriceProvider(String menuID, String buttonID) {
        this.menuID = menuID;
        this.buttonID = buttonID;
        supportedPlugin = Dependencies.valueOf(supportedPluginMap.get(true));
    }

    @Nullable
    public BigDecimal buyPrice(Material material) {
        // Always start at out own price provider
        if (supportedPluginMap.isEmpty()) {
            return defaultBuyPrice();
        }

        try {
            switch (supportedPlugin) {
                // All dependency price getters
                case ShopGui:
                    double price = ShopGuiPlusApi.getItemStackPriceBuy(new ItemStack(material));
                    if (price == -0.1) {
                        return defaultBuyPrice();
                    }
                    return BigDecimal.valueOf(price);

                case EconomyShopGUI:
                    return BigDecimal.valueOf(EconomyShopGUIHook.getItemBuyPrice(new ItemStack(material)));

                    // End if hooks
            }
        } catch (Exception ignored) {
            // incase a return on dependency returns a null we check our prices
            logger.debug("Was unable to get the buy price of (" + material.name() + ") from " + supportedPlugin + " If a price is present in BedrockFormShop config that price will be used.");
            return defaultBuyPrice();
        }
        return null;
    }

    @Nullable
    public BigDecimal sellPrice(Material material) {
        // Always start at out own price provider
        if (supportedPluginMap.isEmpty()) {
            return defaultSellPrice();
        }

        try {
            switch (supportedPlugin) {
                // All dependency price getters
                case ShopGui:
                    double price = ShopGuiPlusApi.getItemStackPriceSell(new ItemStack(material));
                    if (price == -0.1) {
                        return defaultSellPrice();
                    }
                    return BigDecimal.valueOf(price);

                case EconomyShopGUI:
                    return BigDecimal.valueOf(EconomyShopGUIHook.getItemSellPrice(new ItemStack(material)));

                    // End of hooks
            }
        } catch (Exception ignored) {
            // incase a return on dependency returns a null we check our prices
            logger.debug("Was unable to get the sell price of (" + material.name() + ") from " + supportedPlugin + " If a price is present in BedrockFormShop config that price will be used.");
            return defaultSellPrice();
        }
        return null;
    }

    // Default BedrockFormShop price list
    public BigDecimal defaultBuyPrice() {
        if (SECTION.getButtonData(menuID, buttonID).isSet("buy-price")) {
            return BigDecimal.valueOf(SECTION.getButtonData(menuID, buttonID).getDouble("buy-price"));
        } else {
            return null;
        }
    }

    public BigDecimal defaultSellPrice() {
        if (SECTION.getButtonData(menuID, buttonID).isSet("sell-price")) {
            return BigDecimal.valueOf(SECTION.getButtonData(menuID, buttonID).getDouble("sell-price"));
        } else {
            return null;
        }
    }

    private enum Dependencies {
        ShopGui,
        EconomyShopGUI
    }
}
