package dev.kejona.bedrockformshop.handlers;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.logger.Logger;
import me.gypopo.economyshopgui.api.EconomyShopGUIHook;
import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.HashMap;

public class PriceProvider {

    private final ConfigurationHandler SECTION = BedrockFormShop.getInstance().getSECTION();
    private final String menuID;
    private final String buttonID;
    private final HashMap<Boolean, String> plugins = BedrockFormShop.getInstance().getHookedPlugins();

    private final Logger logger = Logger.getLogger();

    public PriceProvider(String menuID, String buttonID) {
        this.menuID = menuID;
        this.buttonID = buttonID;
    }

    public BigDecimal buyPrice(Material material) {
        if (plugins.containsKey(true)) {
            // plugin hook for price
            //
            try {
                // Get plugins name.
                String pluginName = plugins.get(true);
                // GuiShop prices
                if (pluginName.equals("ShopGUI")) {
                    return BigDecimal.valueOf(ShopGuiPlusApi.getItemStackPriceBuy(new ItemStack(material)));
                }
                // EconomyShopGUI prices
                if (pluginName.equals("EconomyShopGUI")) {
                    return BigDecimal.valueOf(EconomyShopGUIHook.getItemBuyPrice(new ItemStack(material)));
                }
                // END of hooks
            } catch (Exception ignored) {
                logger.debug("Was unable to get a price of (" + material.name() + ") from " + plugins.get(true) + " If a price is present in BedrockFormShop config that price will be used.");
            }
        }
        // If api's returned a null or is not enabled we check our own price list if price is present.
        if (SECTION.getButtonData(menuID, buttonID).isSet("buy-price")) {
            return BigDecimal.valueOf(SECTION.getButtonData(menuID, buttonID).getDouble("buy-price"));
        }

        return null;
    }

    public BigDecimal sellPrice(Material material) {
        if (plugins.containsKey(true)) {
            // plugin hook for price
            //
            try {
                String pluginName = plugins.get(true);
                // GuiShop prices
                if (pluginName.equals("ShopGUI")) {
                    return BigDecimal.valueOf(ShopGuiPlusApi.getItemStackPriceSell(new ItemStack(material)));
                }
                // EconomyShopGUI prices.
                if (pluginName.equals("EconomyShopGUI")) {
                    return BigDecimal.valueOf(EconomyShopGUIHook.getItemSellPrice(new ItemStack(material)));
                }
                // END
                // If somehow api's returned a null we check our own price list if price is present.
            } catch (Exception ignored) {
                logger.debug("Was unable to get a price of (" + material.name() + ") from " + plugins.get(true) + ". If a price is present in BedrockFormShop config that price will be used.");
            }
        }
        // If somehow api's returned a null we check our own price list if price is present.
        if (SECTION.getButtonData(menuID, buttonID).isSet("sell-price")) {
            return BigDecimal.valueOf(SECTION.getButtonData(menuID, buttonID).getDouble("sell-price"));
        }

        return null;
    }
}
