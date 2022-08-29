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
        if (plugins.isEmpty() || !SECTION.getApiEnable()) {
                return defaultBuyPrice();
        }

        try {
            // Get plugins name.
            String pluginName = plugins.get(true);
            // GuiShop prices
            if (pluginName.equals("ShopGUI")) {
                double price = ShopGuiPlusApi.getItemStackPriceBuy(new ItemStack(material));
                if (price == -0.1) {
                    return defaultBuyPrice();
                }
                return BigDecimal.valueOf(price);
            }
            // EconomyShopGUI prices
            if (pluginName.equals("EconomyShopGUI")) {
                return BigDecimal.valueOf(EconomyShopGUIHook.getItemBuyPrice(new ItemStack(material)));
            }
            // END of hooks
        } catch (Exception ignored) {
            logger.debug("Was unable to get a price of (" + material.name() + ") from " + plugins.get(true) + " If a price is present in BedrockFormShop config that price will be used.");
            return defaultBuyPrice();
        }
        return null;
    }

    public BigDecimal sellPrice(Material material) {
        if (plugins.isEmpty() || !SECTION.getApiEnable()) {
            return defaultSellPrice();
        }

        try {
            // Get plugins name.
            String pluginName = plugins.get(true);
            // GuiShop prices
            if (pluginName.equals("ShopGUI")) {
                double sellPrice = ShopGuiPlusApi.getItemStackPriceSell(new ItemStack(material));
                if (sellPrice == -0.1) {
                    return defaultSellPrice();
                }
                return BigDecimal.valueOf(sellPrice);
            }
            // EconomyShopGUI prices
            if (pluginName.equals("EconomyShopGUI")) {
                return BigDecimal.valueOf(EconomyShopGUIHook.getItemSellPrice(new ItemStack(material)));
            }
            // END of hooks
        } catch (Exception ignored) {
            logger.debug("Was unable to get a price of (" + material.name() + ") from " + plugins.get(true) + " If a price is present in BedrockFormShop config that price will be used.");
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
        if (SECTION.getButtonData(menuID, buttonID).isSet("buy-price")) {
            return BigDecimal.valueOf(SECTION.getButtonData(menuID, buttonID).getDouble("sell-price"));
        } else {
            return null;
        }
    }
}
